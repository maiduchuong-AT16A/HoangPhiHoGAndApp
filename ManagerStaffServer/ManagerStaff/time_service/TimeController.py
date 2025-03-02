from django.shortcuts import render
from .models import CheckIn,CheckOut,Workday,PaySlip,StatisticalTimeKeeping
from user_service.models import User,Account
from rest_framework.decorators import api_view
from user_service.serializer import UserSerializer
from rest_framework.response import Response
from .serializer import CheckInSerializer,CheckOutSerializer,PaySlipSericalizer,WorkdaySerializer,StatisticalTimeKeepingSerializer
from rest_framework import status
from datetime import datetime
from notification_service.models import NotificationPost
from django.db.models import Q
from user_service.jsonwebtokens import create_jwt,verify_jwt
from notification_service.serializer import NotificationPostSerializer
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from notification_service.NotificationController import formatTime
from .models import Workday
import calendar
from setting_service.models import Setting

channel_layer = get_channel_layer()

def number_of_days_in_month(year, month):
    return calendar.monthrange(year, month)[1]

def findNumberMinute(start_str,end_str):
    start_time = datetime.strptime(start_str, '%Y-%m-%d %H:%M:%S')
    end_time = datetime.strptime(end_str, '%Y-%m-%d %H:%M:%S')
    time_difference = end_time - start_time
    minutes_difference = time_difference.total_seconds() // 60
    return minutes_difference

def checkTime(str1,str2,time):

    time1 = datetime.strptime(str(str1), '%H:%M:%S').time()
    time2 = datetime.strptime(str(str2), '%H:%M:%S').time()
    check_time = datetime.strptime(str(time), '%H:%M:%S').time()
    if time1 <= check_time <= time2:
        return True
    return False

def checkAuthorization(request):
    authorization_header = request.META.get('HTTP_AUTHORIZATION')
    decoded_data=verify_jwt(authorization_header)
    if isinstance(decoded_data, dict)==False:
        return 1
    else:
        authorization=User.objects.get(id_user=decoded_data['data']['id_user'])
        account_authorization=Account.objects.get(user=authorization)
        if authorization is not None and account_authorization is not None:
            return 0
    return 2

def getTimeNow():
    ngay_hien_tai = datetime.now()
    ngay_hien_tai_format = ngay_hien_tai.strftime('%d-%m-%Y')
    return ngay_hien_tai_format

def getNameDay(time_day):
    ngay_datetime = datetime.strptime(time_day, '%d-%m-%Y')
    thu_trong_tuan_eng = ngay_datetime.strftime('%A')   
    switcher = {
        'Monday': 'Thứ Hai',
        'Tuesday': 'Thứ Ba',
        'Wednesday': 'Thứ Tư',
        'Thursday': 'Thứ Năm',
        'Friday': 'Thứ Sáu',
        'Saturday': 'Thứ Bảy',
        'Sunday': 'Chủ Nhật',
    }
    thu_trong_tuan_vn=switcher.get(thu_trong_tuan_eng, 'Không xác định')
    return thu_trong_tuan_vn

@api_view(['POST'])
def add_time(request,id_user):
    data=request.GET
    user=User.objects.get(id_user=id_user)
    setting=Setting.objects.first()
    if user is not None:
        day=data.get("day")
        time=data.get("time")
        date_obj = datetime.strptime(day, '%Y-%m-%d')
        new_s = date_obj.strftime('%d-%m-%Y')
        
        try:
            payslip=PaySlip.objects.get(user=user,month=int(new_s.split('-')[1]),year=int(new_s.split('-')[2]))
        except PaySlip.DoesNotExist:
            payslip = PaySlip(num_day_off=number_of_days_in_month(int(new_s.split('-')[2]),int(new_s.split('-')[1])),
                              price=0,
                              evaluate="",
                              num_work_day=0,
                              num_leave_early=0,
                              note="",
                              month=(int)(new_s.split('-')[1]),
                              num_late_day=0,
                              year=(int)(new_s.split('-')[2]),
                              user=user)
            payslip.save()
            
        try:
            workday = Workday.objects.get(payslip=payslip, day=day)
        except Workday.DoesNotExist:
            workday = Workday(day=day, 
                              status='8 tiếng', 
                              payslip=payslip,
                              name_day=getNameDay(new_s),
                              wage_sum=0,
                              num_hour_work=0,
                              num_errors=0)
            workday.save()
        list_check_ins= CheckIn.objects.filter(workday=workday)
        list_check_outs=CheckOut.objects.filter(workday=workday)
 
        if list_check_outs.last() is not None:
            for i in range(len(list_check_outs)):
                if checkTime(list_check_ins[i].time_in,list_check_outs[i].time_out,time)==True:
                    return Response({"data":"attended","message":"Success","code":200},status=status.HTTP_200_OK)
 
        if len(list_check_ins)==len(list_check_outs):
            if list_check_outs.last() is not None:
                num_minute=findNumberMinute(day+" "+str(list_check_outs.last().time_out),str(day+" "+time))
                if num_minute<=1:
                    return Response({"data":"attended","message":"Success","code":200},status=status.HTTP_200_OK)
            
            num_minute=findNumberMinute(day+" "+str(setting.time_start),str(day+" "+time))
            
            num_price_in=0
            status_check_in="Đúng giờ"
            time_different='0 phút'
            if num_minute>0:
                num_price_in=-1*user.wage*0.05
                status_check_in="Đi muộn"
                if list_check_ins.last() is None:
                    workday.num_errors=workday.num_errors+(0 if workday.num_errors==1 else (1 if workday.num_errors==0 else -1))
                    payslip.set_num_late_day(payslip.num_late_day+1)
            
            if num_minute>=60:
                time_different=str(int(num_minute//60))+' giờ'
            elif num_minute>0:
                time_different=str(int(num_minute))+' phút'
                
            workday.save()
            payslip.save()
            checkin=CheckIn()
            checkin.set_time_in(time)
            checkin.set_money(num_price_in)
            checkin.set_status(status_check_in)
            checkin.set_workday(workday)
            checkin.set_time_difference(time_different)
            checkin.save()
            notify= NotificationPost(title_notification='Chấm công thành công',body_notification='Bạn vừa đến công ty vào ngày '+new_s+' lúc '+time,time_notification=day+' '+time,is_read=0,type_notification=2,id_data=checkin.id_check_in,user=user)
            notify.save()
            notify.set_time_notification(formatTime(str(notify.time_notification)))
            message = NotificationPostSerializer(notify)
            async_to_sync(channel_layer.group_send)('test',{
                'type': 'chat_message',
                'data': message.data,
            })
            serializer = CheckInSerializer(checkin)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        else:
            if list_check_ins.last() is not None:
                num_minute=findNumberMinute(day+" "+str(list_check_ins.last().time_in),str(day+" "+time))
                if num_minute<=1:
                    return Response({"data":"attended","message":"Success","code":200},status=status.HTTP_200_OK)
            
            num_minute=findNumberMinute(day+" "+str(setting.time_end),str(day+" "+time))
            
            num_hour_work=findNumberMinute(str(workday.day)+' '+ str(list_check_ins[0].time_in,),str(day)+" "+str(time))//60
            if num_hour_work<=8:
                wage_day=(user.wage/8)*num_hour_work
            else:
                wage_day=(user.wage)+((user.wage/8)*setting.overtime*(num_hour_work-8))
            num_price_out=0
            status_check_out="Đúng giờ"
            time_different='0 phút'
            if num_minute>=60:
                num_price_out=(user.wage/8)*setting.overtime*(num_minute/60)
                status_check_out="Làm thêm"
                time_different=str(int(num_minute//60))+' giờ'
            elif num_minute<0:
                num_price_out=-1*user.wage*0.05
                status_check_out="Về sớm"
                if workday.num_errors<2:
                    workday.num_errors=workday.num_errors+1
                    payslip.set_num_leave_early(payslip.num_leave_early+1)
                time_different=(str(abs(int(num_minute//60)))+' giờ' if abs(num_minute)>=60 else str(abs(int(num_minute)))+' phút')
            payslip.set_price(float(payslip.price)-float(workday.wage_sum))
            if list_check_outs.last() is None:
                payslip.set_num_day_off(payslip.num_day_off-1)
                payslip.set_num_work_day(payslip.num_work_day+1)
        
            wage_day+=list_check_ins[0].money+num_price_out
            workday.set_num_hour_work(num_hour_work)
            workday.set_wage_sum(0 if wage_day<0 else wage_day)
            workday.save()
            
            payslip.set_price(float(payslip.price)+float(workday.wage_sum))
            payslip.save()
            
            
            checkout=CheckOut()
            checkout.set_time_out(time)
            checkout.set_money(num_price_out)
            checkout.set_status(status_check_out)
            checkout.set_workday(workday)
            checkout.set_time_difference(time_different)
            checkout.save()
            notify=NotificationPost.objects.create(title_notification='Chấm công thành công',body_notification='Bạn vừa rời công ty vào ngày '+new_s+' lúc '+time,time_notification=day+' '+time,is_read=0,type_notification=2,id_data=checkout.id_check_out,user=user)
            notify.save()
            notify.set_time_notification(formatTime(str(notify.time_notification)))
            message = NotificationPostSerializer(notify)
            async_to_sync(channel_layer.group_send)('test',{
                'type': 'chat_message',
                'data': message.data,
            })
            serializer = CheckOutSerializer(checkout)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)


@api_view(['GET'])
def get_time_user(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        day_start=data.get("day_start")
        day_end=data.get("day_end")
        date_object1 = datetime.strptime(day_start, "%d-%m-%Y")
        start_day_new = date_object1.strftime("%Y-%m-%d")   
        date_object2 = datetime.strptime(day_end, "%d-%m-%Y")
        end_day_new = date_object2.strftime("%Y-%m-%d") 
        user=User.objects.get(id_user=id_user)
        if user is not None:
            serializer = UserSerializer(user, context={'start_day': start_day_new, 'end_day': end_day_new})
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        serializer = UserSerializer(User())
        return Response({"data":serializer.data,"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = UserSerializer(User())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = UserSerializer(User())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)


@api_view(['POST'])
def add_payslip_detail(request):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        month=data.get("month")
        year=data.get("year")
        time=data.get("list_time")
        name_time=data.get("list_name_time")
        list_time=time.split(' ')
        list_name_time=name_time.split('-')
        list_user=User.objects.all()
        for user in list_user:
            try:
                payslip=PaySlip.objects.get(user=user,month=month,year=year)
            except PaySlip.DoesNotExist:
                payslip = PaySlip(num_day_off=len(list_time),price=0,num_leave_early=0,evaluate="",note="",month=month,num_late_day=0,year=year,user=user)
                payslip.set_num_work_day(0)
                payslip.save()
            for index, itime in enumerate(list_time):
                if len(itime)>0:
                    try:
                        workday = Workday.objects.get(payslip=payslip, day=itime)
                    except Workday.DoesNotExist:
                        workday = Workday(day=itime, status='8 tiếng', payslip=payslip,wage_sum=0,num_hour_work=0,num_errors=(0 if list_name_time[index]== 'Thứ Bảy' or list_name_time[index]== 'Chủ Nhật' else 1),name_day=list_name_time[index])
                        workday.save()         
        
        return Response({"data":"","message":"Success","code":201},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)


@api_view(['GET'])
def get_workday_detail(request,id_workday):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        workday=Workday.objects.get(id_workday=id_workday)
        date_obj = datetime.strptime(str(workday.day), '%Y-%m-%d')
        new_s = date_obj.strftime('%d-%m-%Y')
        workday.set_day(new_s)
        serializer = WorkdaySerializer(workday)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = WorkdaySerializer(Workday())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = WorkdaySerializer(Workday())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['PUT'])
def update_time(request,id_time_in,id_time_out):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        time_in=data.get("time_in")
        time_out=data.get("time_out")
        setting=Setting.objects.first()
        timeIn=CheckIn.objects.get(id_check_in=id_time_in)
        list_check_ins=CheckIn.objects.filter(workday=timeIn.workday)
        list_check_outs=CheckOut.objects.filter(workday=timeIn.workday)       
        if id_time_in>0:
            check_late_work=False
            check_early_work=False
            list_check_ins=CheckIn.objects.filter(workday=timeIn.workday)
            list_check_outs=CheckOut.objects.filter(workday=timeIn.workday)
            if list_check_outs.last() is not None:
                for i in range(len(list_check_outs)):
                    if list_check_ins[i].id_check_in!=id_time_in and (checkTime(list_check_ins[i].time_in,list_check_outs[i].time_out,time_in)==True):
                        return Response({"data":"attended","message":"Success","code":200},status=status.HTTP_200_OK)
            if list_check_ins.first() is not None and list_check_ins.first().money<0:
                check_late_work=True
            timeIn.set_time_in(time_in)
            num_minute=findNumberMinute(str(timeIn.workday.day)+' '+str(setting.time_start),str(timeIn.workday.day)+' '+time_in)
            timeIn.set_status('Đúng giờ' if num_minute<=0 else 'Đi muộn')
            timeIn.set_money(0 if num_minute<=0 else -1*timeIn.workday.payslip.user.wage*0.05)
            timeIn.set_time_difference(str(int(num_minute//60))+' giờ' if num_minute>=60 else ('0 phút' if num_minute<=0 else str(int(num_minute))+' phút'))
            timeIn.save()
        if id_time_out>0:
            timeOut=CheckOut.objects.get(id_check_out=id_time_out)
            if list_check_outs.last() is not None:
                for i in range(len(list_check_outs)):
                    if list_check_ins[i].id_check_in!=id_time_in and (checkTime(list_check_ins[i].time_in,list_check_outs[i].time_out,time_out)==True):
                        return Response({"data":"attended","message":"Success","code":200},status=status.HTTP_200_OK)
            if list_check_outs.last() is not None and list_check_outs.last().money<0:
                check_early_work=True
            timeOut.set_time_out(time_out)
            num_minute=findNumberMinute(str(timeIn.workday.day)+' '+str(setting.time_end),str(timeIn.workday.day)+' '+time_out)
            timeOut.set_status('Về sớm' if num_minute<0 else ('Làm thêm' if num_minute>=60 else 'Đúng giờ'))
            timeOut.set_money((timeIn.workday.payslip.user.wage/8)*setting.overtime*(num_minute//60) if num_minute>=60 else (-50000 if num_minute<0 else 0))
            timeOut.set_time_difference(str(num_minute//60)+' giờ' if abs(num_minute)>=60 else ('0 phút' if num_minute>=0 else str(abs(int(num_minute)))+' phút'))
            timeOut.save()
        

        num_error=0
        num_money_fine=0
        num_hour_work=0
        num_wage=0
        if list_check_ins.first() is not None:
            
            if list_check_ins.first().money<0:
                num_error=num_error+1
            num_money_fine+=list_check_ins.first().money    
        if list_check_outs.last() is not None:
            num_minute=findNumberMinute(str(timeIn.workday.day)+' '+str(list_check_ins.first().time_in),str(timeIn.workday.day)+' '+str(list_check_outs.last().time_out))   
            if list_check_outs.last().money<0:
                num_error+=1
            num_money_fine+=list_check_outs.last().money    
            num_hour_work=num_minute//60
            if num_hour_work<=8:
                num_wage=(timeIn.workday.payslip.user.wage/8)*num_hour_work
            else:
                num_wage=(timeIn.workday.payslip.user.wage)+((timeIn.workday.payslip.user.wage/8)*1.5*(num_hour_work-8))
            num_wage+=num_money_fine
        
        workday=timeIn.workday
        payslip=workday.payslip
        payslip.set_price(float(payslip.price)-float(workday.wage_sum)+num_wage)
        workday.set_wage_sum(num_wage)
        workday.set_num_hour_work(num_hour_work)
        workday.set_num_errors(num_error)
        workday.save()
        payslip.set_num_late_day(payslip.num_late_day if list_check_ins.first() is not None and ((list_check_ins.first().money<0 and check_late_work==True) or
                                 (list_check_ins.first().money>=0 and check_late_work==False)) else (payslip.num_late_day-1 if list_check_ins.first().money>=0 and check_late_work==True else payslip.num_late_day+1))
        payslip.set_num_leave_early(payslip.num_leave_early if list_check_outs.last() is None or (list_check_outs.last() is not None and ((list_check_outs.last().money<0 and check_early_work==True) or
                                 (list_check_outs.last().money>=0 and check_early_work==False))) else (payslip.num_leave_early-1 if list_check_outs.last() is not None and list_check_outs.last().money>=0 and check_early_work==True else payslip.num_leave_early+1))
        payslip.save()
        
        return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)
  
@api_view(['GET'])
def get_payslip(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        month=data.get('month')
        year=data.get('year')
        user=User.objects.get(id_user=id_user)
        payslip=PaySlip.objects.get(user=user,month=int(month),year=int(year))
        serializer = PaySlipSericalizer(payslip)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = PaySlipSericalizer(PaySlip())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = PaySlipSericalizer(PaySlip())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['GET'])
def get_list_statistical(request):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        time_start=data.get('time_start')
        time_end=data.get('time_end')
        month_start=int(time_start.split('-')[1])
        year_start=int(time_start.split('-')[2])
        month_end=int(time_end.split('-')[1])
        year_end=int(time_end.split('-')[2])
        
        list_user=User.objects.all()
        list_statistical=[]
        for user in list_user:
            account=Account.objects.get(user=user)
            if account.username!='admin':
                statistical=StatisticalTimeKeeping(id_statistical=1,user=user,num_day_off=0,num_workday=0,num_late_day=0,num_leave_early=0,total_errors=0)
                num_day_off=0
                num_workday=0
                num_late_day=0
                num_leave_early=0
                total_errors=0
                workdays=[]
                for i in range(year_start,year_end+1):
                    start=1
                    end=12
                    if i==year_start:
                        start=month_start
                    if i==year_end:
                        end=month_end
                    for j in range(start,end+1):
                        payslip=PaySlip.objects.get(user=user,month=j,year=i)
                        num_day_off+=payslip.num_day_off
                        num_workday+=payslip.num_work_day
                        num_late_day+=payslip.num_late_day
                        num_leave_early+=payslip.num_leave_early
                        list_workday=Workday.objects.filter(payslip=payslip)
                        for workday in list_workday:
                            if workday.num_errors>0:
                                total_errors+=workday.num_errors
                                workdays.append(workday)
                statistical.set_num_day_off(num_day_off)
                statistical.set_num_late_day(num_late_day)
                statistical.set_num_leave_early(num_leave_early)
                
                statistical.set_num_workday(num_workday)
                statistical.set_total_errors(total_errors)
                statistical.save()
                statistical.workdays.set(workdays)
                list_statistical.append(statistical)
                
        serializer = StatisticalTimeKeepingSerializer(list_statistical,many=True)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = PaySlipSericalizer(PaySlip())
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['GET'])
def get_list_statistical_user(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        data=request.GET
        time_start=data.get('time_start')
        time_end=data.get('time_end')
        month_start=int(time_start.split('-')[1])
        year_start=int(time_start.split('-')[2])
        month_end=int(time_end.split('-')[1])
        year_end=int(time_end.split('-')[2])
        user=User.objects.get(id_user=id_user)
        statistical=StatisticalTimeKeeping(id_statistical=1,user=user,num_day_off=0,num_workday=0,num_late_day=0,num_leave_early=0,total_errors=0)
        num_day_off=0
        num_workday=0
        num_late_day=0
        num_leave_early=0
        total_errors=0
        workdays=[]
        for i in range(year_start,year_end+1):
            start=1
            end=12
            if i==year_start:
                start=month_start
            if i==year_end:
                end=month_end
            for j in range(start,end+1):
                payslip=PaySlip.objects.get(user=user,month=j,year=i)
                num_day_off+=payslip.num_day_off
                num_workday+=payslip.num_work_day
                num_late_day+=payslip.num_late_day
                num_leave_early+=payslip.num_leave_early
                list_workday=Workday.objects.filter(payslip=payslip)
                for workday in list_workday:
                    if workday.num_errors>0:
                        total_errors+=workday.num_errors
                        workdays.append(workday)
        statistical.set_num_day_off(num_day_off)
        statistical.set_num_late_day(num_late_day)
        statistical.set_num_leave_early(num_leave_early)
        statistical.set_num_workday(num_workday)
        statistical.set_total_errors(total_errors)
        statistical.save()
        statistical.workdays.set(workdays)
        serializer = StatisticalTimeKeepingSerializer(statistical)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = StatisticalTimeKeepingSerializer(StatisticalTimeKeeping())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = StatisticalTimeKeepingSerializer(StatisticalTimeKeeping())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['DELETE'])
def delete_time(request,id_time_in,id_time_out):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        if id_time_in>0:
            timeIn=CheckIn.objects.get(id_check_in=id_time_in)
            check_late_work=False
            check_early_work=False
            list_check_ins=CheckIn.objects.filter(workday=timeIn.workday)
            list_check_outs=CheckOut.objects.filter(workday=timeIn.workday)
            
            if list_check_ins.first() is not None and list_check_ins.first().money<0:
                check_late_work=True
            
            if list_check_outs.last() is not None and list_check_outs.last().money<0:
                check_early_work=True
            timeIn.delete()
        if id_time_out>0:
            timeOut=CheckOut.objects.get(id_check_out=id_time_out)
            timeOut.delete()
            
        num_error=0
        num_money_fine=0
        num_hour_work=0
        num_wage=0
        if list_check_ins.first() is not None:
            
            if list_check_ins.first().money<0:
                num_error=num_error+1
            num_money_fine+=list_check_ins.first().money    
        if list_check_outs.last() is not None:
            num_minute=findNumberMinute(str(timeIn.workday.day)+' '+str(list_check_ins.first().time_in),str(timeIn.workday.day)+' '+str(list_check_outs.last().time_out))   
            if list_check_outs.last().money<0:
                num_error+=1
            num_money_fine+=list_check_outs.last().money    
            num_hour_work=num_minute//60
            if num_hour_work<=8:
                num_wage=(timeIn.workday.payslip.user.wage/8)*num_hour_work
            else:
                num_wage=(timeIn.workday.payslip.user.wage)+((timeIn.workday.payslip.user.wage/8)*1.5*(num_hour_work-8))
            num_wage+=num_money_fine
        
        workday=timeIn.workday
        payslip=workday.payslip
        payslip.set_price(float(payslip.price)-float(workday.wage_sum)+num_wage)
        workday.set_wage_sum(num_wage)
        workday.set_num_hour_work(num_hour_work)
        workday.set_num_errors(num_error)
        workday.save()
        payslip.set_num_late_day(payslip.num_late_day if list_check_ins.first() is None or (list_check_ins.first() is not None and ((list_check_ins.first().money<0 and check_late_work==True) or
                                 (list_check_ins.first().money>=0 and check_late_work==False))) else (payslip.num_late_day-1 if list_check_ins.first().money>=0 and check_late_work==True else payslip.num_late_day+1))
        payslip.set_num_leave_early(payslip.num_leave_early if list_check_outs.last() is None or (list_check_outs.last() is not None and ((list_check_outs.last().money<0 and check_early_work==True) or
                                 (list_check_outs.last().money>=0 and check_early_work==False))) else (payslip.num_leave_early-1 if list_check_outs.last() is not None and list_check_outs.last().money>=0 and check_early_work==True else payslip.num_leave_early+1))
        payslip.save()
        return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)