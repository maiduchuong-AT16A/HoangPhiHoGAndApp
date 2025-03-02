from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from user_service.jsonwebtokens import verify_jwt
from user_service.models import User,Account
from .serializer import NotificationPostSerializer
from .models import NotificationPost
from datetime import datetime
from django.db.models import Q
from rest_framework import status

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

def formatTime(timeOld):
    specific_time = datetime.fromisoformat(str(timeOld))
    time_string_without_offset = specific_time.strftime("%Y-%m-%d %H:%M:%S")
    input_time = datetime.strptime(time_string_without_offset, "%Y-%m-%d %H:%M:%S")
    
    current_time = datetime.now()
    time_difference = current_time - input_time
    seconds = time_difference.total_seconds()
    minutes = seconds // 60
    hours = minutes // 60
    days=hours//24
    weeks=days//7
    months=days//30
    years=months//12
    time=""
    if years>0:
        time=str(int(years))+" năm trước"
    elif months>0:
        time=str(int(months))+" tháng trước"
    elif weeks>0:
        time=str(int(weeks))+" tuần trước"
    elif days>0:
        time=str(int(days))+" ngày trước"
    elif hours>=1 and hours<24:
        time=str(int(hours))+" giờ trước"
    elif minutes>0:
        time=str(int(minutes))+" phút trước"
    else:
        time="Vừa xong"
    return time

# Create your views here.
@api_view(['GET'])
def get_all_notification(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        user=User.objects.get(id_user=id_user)
        listNotification=NotificationPost.objects.filter(Q(user=user) & Q(is_read=0)).order_by('-time_notification')
        for notification in listNotification:
            notification.set_is_read(1)
            notification.save()
        listFomatted=[]
        for notification in listNotification:
            specific_time = datetime.fromisoformat(str(notification.time_notification))
            time_string_without_offset = specific_time.strftime("%Y-%m-%d %H:%M:%S")
            input_time = datetime.strptime(time_string_without_offset, "%Y-%m-%d %H:%M:%S")
            
            current_time = datetime.now()
            time_difference = current_time - input_time
            seconds = time_difference.total_seconds()
            minutes = seconds // 60
            hours = minutes // 60
            days=hours//24
            weeks=days//7
            months=days//30
            years=months//12
            time=""
            if years>0:
                time=str(int(years))+" năm trước"
            elif months>0:
                time=str(int(months))+" tháng trước"
            elif weeks>0:
                time=str(int(weeks))+" tuần trước"
            elif days>0:
                time=str(int(days))+" ngày trước"
            elif hours>=1 and hours<24:
                time=str(int(hours))+" giờ trước"
            elif minutes>0:
                time=str(int(minutes))+" phút trước"
            else:
                time="Vừa xong"
            notification.set_time_notification(time)
            listFomatted.append(notification)
        serializer = NotificationPostSerializer(listFomatted,many=True)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['GET'])
def get_all_notification_detail(request,id_user):
    checkAu=checkAuthorization(request)
    data=request.GET
    if checkAu==0:
        page=int(data.get('page'))
        size=int(data.get('size'))
        user=User.objects.get(id_user=id_user)
        listNotification=NotificationPost.objects.filter(Q(user=user)).order_by('-time_notification')
        start=page
        end=page+size
        if end>len(listNotification): end=len(listNotification)
        listNotification=listNotification[start:end]
        listFomatted=[]
        for notification in listNotification:
            specific_time = datetime.fromisoformat(str(notification.time_notification))
            time_string_without_offset = specific_time.strftime("%Y-%m-%d %H:%M:%S")
            input_time = datetime.strptime(time_string_without_offset, "%Y-%m-%d %H:%M:%S")
            
            current_time = datetime.now()
            time_difference = current_time - input_time
            seconds = time_difference.total_seconds()
            minutes = seconds // 60
            hours = minutes // 60
            days=hours//24
            weeks=days//7
            months=days//30
            years=months//12
            time=""
            if years>0:
                time=str(int(years))+" năm trước"
            elif months>0:
                time=str(int(months))+" tháng trước"
            elif weeks>0:
                time=str(int(weeks))+" tuần trước"
            elif days>0:
                time=str(int(days))+" ngày trước"
            elif hours>=1 and hours<24:
                time=str(int(hours))+" giờ trước"
            elif minutes>0:
                time=str(int(minutes))+" phút trước"
            else:
                time="Vừa xong"
            notification.set_time_notification(time)
            listFomatted.append(notification)
        serializer = NotificationPostSerializer(listFomatted,many=True)
        return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['DELETE'])
def delete_notification(request,id_notification):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        notification=NotificationPost.objects.get(id_notification=id_notification)
        if notification is not None:
            notification.delete()
            return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['GET'])
def read_all_notification(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        user=User.objects.get(id_user=id_user)
        listNotification=NotificationPost.objects.filter(user=user)
        if listNotification is not None:
            for notification in listNotification:
                notification.set_is_read(2)
                notification.save()
            return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)
    
@api_view(['GET'])
def read_notification(request,id_notification):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        notification=NotificationPost.objects.get(id_notification=id_notification)
        if notification is not None:
            notification.set_is_read(2)
            notification.save()
            return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)