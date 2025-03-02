from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializer import CalendarSerializer,TypeTypeCalendarSerializer
from .models import Calendar,TypeCalendar
from rest_framework import status
from user_service.models import Part,User,Account
from django.db.models import Q
from datetime import datetime
from user_service.jsonwebtokens import verify_jwt
from notification_service.models import NotificationPost
from notification_service.serializer import NotificationPostSerializer
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from notification_service.NotificationController import formatTime
from time_service.models import Workday,PaySlip
import calendar

channel_layer = get_channel_layer()

def number_of_days_in_month(year, month):
    return calendar.monthrange(year, month)[1]

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

# Create your views here.
@api_view(['GET'])
def list_calender_by_part(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        user=User.objects.get(id_user=id_user)
        data=request.GET
        if user is not None:
            day=data.get('day_calendar')
            date_obj = datetime.strptime(day, '%Y-%m-%d')
            new_s = date_obj.strftime('%d-%m-%Y')
            try:
                payslip=PaySlip.objects.get(user=user,month=(int)(new_s.split('-')[1]),year=(int)(new_s.split('-')[2]))
            except PaySlip.DoesNotExist:
                return Response({"data":[],"message":"Success","code":200},status=status.HTTP_200_OK)
            workday = Workday.objects.get(payslip=payslip, day=day)
            list_calendar=Calendar.objects.filter(work_day=workday).order_by('time_start')
            serializer = CalendarSerializer(list_calendar,many=True)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":[],"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)

@api_view(['DELETE'])
def delete_calendar(request,id_calendar):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        calendar=Calendar.objects.get(id_calendar=id_calendar)
        if calendar is not None:
            listNotification=NotificationPost.objects.filter(Q(type_notification=5) & Q(id_data=calendar.id_calendar))
            for notification in listNotification:
                notification.delete()
            calendar.delete()
            return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)

@api_view(['GET'])
def all_type_calendar(request):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        listTypeCalendar=TypeCalendar.objects.all()
        if listTypeCalendar is not None:
            serializer = TypeTypeCalendarSerializer(listTypeCalendar,many=True)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":[],"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)

@api_view(['GET'])
def get_calendar(request,id_calendar):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        calendar=Calendar.objects.get(id_calendar=id_calendar)
        if calendar is not None:
            serializer = CalendarSerializer(calendar)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        serializer = CalendarSerializer(Calendar())
        return Response({"data":serializer.data,"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = CalendarSerializer(Calendar())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = CalendarSerializer(Calendar())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)

def time_to_seconds(time_str):
    if len(time_str)>5:
        time_str=time_str[0:5]
    hours, minutes = map(int, time_str.split(':'))
    return hours * 3600 + minutes * 60

def is_overlapping(start1, end1, start2, end2):
    start1_seconds = time_to_seconds(start1)
    end1_seconds = time_to_seconds(end1)
    start2_seconds = time_to_seconds(start2)
    end2_seconds = time_to_seconds(end2)

    return (start1_seconds < end2_seconds and end1_seconds > start2_seconds)

@api_view(['POST'])
def add_calendar(request,id_user,id_type_calendar):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        user=User.objects.get(id_user=id_user)
        type_calendar=TypeCalendar.objects.get(id_type=id_type_calendar)
        data=request.GET
        if user is not None and type_calendar is not None:
            day_calendar=data.get('day_calendar')
            time_start=data.get('time_start')
            time_end=data.get('time_end')
            payslip=PaySlip.objects.get(user=user,month=int(day_calendar.split('-')[1]),year=int(day_calendar.split('-')[0]))
            workday = Workday.objects.get(payslip=payslip, day=day_calendar)
            check=True
            listCalendar=Calendar.objects.filter(Q(work_day=workday))
            if not listCalendar:
                check=True
            else:
                for calendar in listCalendar:
                    if is_overlapping(time_start,time_end,str(calendar.time_start),str(calendar.time_end)):
                        check=False
                        break
            if check:
                calendar = Calendar(work_day=workday,
                header_calendar=data.get('header_calendar'),
                body_calendar=data.get('body_calendar'),
                address=data.get('address'),
                time_start=time_start,
                time_end=time_end,
                type_calendar=type_calendar)
                calendar.save()
                account=Account.objects.get(username='admin')
                notify= NotificationPost(title_notification=str(account.user.full_name)+' đã thêm lịch làm việc',body_notification=calendar.header_calendar,time_notification=str(workday.day)+' '+calendar.time_start,is_read=0,type_notification=5,id_data=calendar.id_calendar,user=user)
                notify.save()
                notify.set_time_notification(formatTime(str(notify.time_notification)))
                message = NotificationPostSerializer(notify)
                async_to_sync(channel_layer.group_send)('test',{
                    'type': 'chat_message',
                    'data': message.data,
                })

                return Response({"data":"","message":"Success","code":201},status=status.HTTP_201_CREATED)
            return Response({"data":"","message":"Exist","code":400},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)


def checkHoliday(day,month):
    if day==30 and month==4:
        return True
    if day==1 and month ==1:
        return True
    if day==2 and month ==9 :
        return True
    if day==5 and month==1:
        return True
    return False

@api_view(['POST'])
def add_all_calendar_of_month(request):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        listUser=User.objects.all()
        if listUser is not None:
            data=request.GET
            day_calendar=data.get('day_calendar')
            month=data.get('month')
            year=data.get('year')
            list_day=day_calendar.split()
            for user in listUser:
                for day in list_day:
                    if checkHoliday(int(day.split('-')[2]),int(day.split('-')[1]))==False:
                        payslip=PaySlip.objects.get(user=user,month=int(month),year=int(year))
                        workday = Workday.objects.get(payslip=payslip, day=day)
                        listCalendar=Calendar.objects.filter(Q(work_day=workday))
                        typeCalendar1=TypeCalendar.objects.get(id_type=1)
                        typeCalendar2=TypeCalendar.objects.get(id_type=4)
                        if user.id_user==24:
                            print(len(listCalendar))
                        if len(listCalendar) ==0:
                            Calendar.objects.create(header_calendar='Làm việc', body_calendar='Làm việc đã được giao.', address='Tại văn phòng làm việc tầng 14',time_start='08:30',time_end='12:00',work_day=workday,type_calendar=typeCalendar1)
                            Calendar.objects.create(header_calendar='Giải lao', body_calendar='Nhân viên xuống nhà ăn trưa.', address='Tại nhà ăn tầng 2',time_start='12:00',time_end='13:00',work_day=workday,type_calendar=typeCalendar2)
                            Calendar.objects.create(header_calendar='Làm việc', body_calendar='Làm việc đã được giao.', address='Tại văn phòng làm việc tầng 14',time_start='13:00',time_end='17:30',work_day=workday,type_calendar=typeCalendar1)
            return Response({"data":"","message":"Success","code":201},status=status.HTTP_201_CREATED)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)

@api_view(['PUT'])
def update_calendar(request,id_calendar,id_user,id_type_calendar):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        calendar=Calendar.objects.get(id_calendar=id_calendar)
        user=User.objects.get(id_user=id_user)
        type_calendar=TypeCalendar.objects.get(id_type=id_type_calendar)
        data=request.GET
        if calendar is not None and type_calendar is not None and user is not None:
            day_calendar=data.get('day_calendar')
            time_start=data.get('time_start')
            time_end=data.get('time_end')
            payslip=PaySlip.objects.get(user=user,month=int(day_calendar.split('-')[1]),year=int(day_calendar.split('-')[0]))
            workday = Workday.objects.get(payslip=payslip, day=day_calendar)
            check=True
            listCalendar=Calendar.objects.filter(Q(work_day=workday))
            if not listCalendar:
                check=True
            else:
                for ca in listCalendar:
                    if ca.id_calendar!=id_calendar and is_overlapping(time_start,time_end,str(ca.time_start),str(ca.time_end)):
                        check=False
                        break
            if check:
                header_calendar=data.get('header_calendar')
                body_calendar=data.get('body_calendar')
                address=data.get('address')
                calendar.set_address(address)
                calendar.set_header_calendar(header_calendar)
                calendar.set_body_calendar(body_calendar)
                calendar.set_time_end(time_end)
                calendar.set_time_start(time_start)
                calendar.set_type_calendar(type_calendar)
                calendar.save()
                listNotification=NotificationPost.objects.filter(Q(type_notification=5) & Q(id_data=calendar.id_calendar))
                for notification in listNotification:
                    notification.set_body_notification(calendar.header_calendar)
                    notification.save()
                return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
            return Response({"data":"","message":"Exist","code":400},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)
