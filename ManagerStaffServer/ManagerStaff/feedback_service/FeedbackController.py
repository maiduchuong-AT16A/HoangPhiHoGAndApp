from django.shortcuts import render
from user_service.models import User,Account
from .models import Feedback
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializer import FeedbackSerializer
from rest_framework import status
from datetime import datetime
from django.db.models import Q
from user_service.jsonwebtokens import verify_jwt
from notification_service.models import NotificationPost
from notification_service.serializer import NotificationPostSerializer
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from notification_service.NotificationController import formatTime

channel_layer = get_channel_layer()
# Create your views here.
from ManagerStaff.settings import ALLOWED_HOSTS
mainUrl='http:/'+ALLOWED_HOSTS[0]+':8000/media/'

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

@api_view(['POST'])
def add_feedback(request,id_user):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        user=User.objects.get(id_user=id_user)
        data=request.GET
        if user is not None:
            feedback=Feedback(
            time_feedback=data.get("time_feedback"),
            content=data.get("content"),
            is_read=1,
            user=user
            )
            feedback.save()
            admin=User.objects.get(is_admin=1)
            notify=NotificationPost(title_notification=str(user.full_name)+' đã gửi phản hồi mới',body_notification=feedback.content,time_notification=feedback.time_feedback,is_read=0,type_notification=3,id_data=feedback.id_feedback,user=admin)
            notify.save()
            notify.set_time_notification(formatTime(str(notify.time_notification)))
            message = NotificationPostSerializer(notify)
            async_to_sync(channel_layer.group_send)('test',{
                'type': 'chat_message',
                'data': message.data,
            })
            return Response({"data":"","message":"Success","code":201},status=status.HTTP_201_CREATED)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)

@api_view(['GET'])
def all_feedback(request):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        listFeedback=Feedback.objects.all().order_by('-time_feedback')
        data=request.GET
        if listFeedback is not None:
            listFeedbackFormat=[]
            page=int(data.get('page'))
            size=int(data.get('size'))
            start=page*size
            end=(page+1)*size
            if end>len(listFeedback): end=len(listFeedback)
            listFeedback=listFeedback[start:end]
            for feedback in listFeedback:
                specific_time = datetime.fromisoformat(str(feedback.time_feedback))
                time_string_without_offset = specific_time.strftime("%Y-%m-%d %H:%M:%S")
                input_time = datetime.strptime(time_string_without_offset, "%Y-%m-%d %H:%M:%S")
                time_formated_day = input_time.strftime("%d-%m-%Y")
                time_formatted_hour = input_time.strftime("%H:%M")
                
                current_time = datetime.now()
                time_difference = current_time - input_time
                seconds = time_difference.total_seconds()
                minutes = seconds // 60
                hours = minutes // 60
                time=""
                if hours>48:
                    time=time_formated_day+" lúc "+time_formatted_hour
                elif hours<=48 and hours>=24:
                    time="Hôm qua lúc "+time_formatted_hour 
                elif hours>=1 and hours<24:
                    time=str(int(hours))+" giờ"
                elif minutes>0:
                    time=str(int(minutes))+" phút"
                else:
                    time="Vừa xong"
                feedback.set_time_feedback(time)
                feedback.user.avatar=mainUrl+feedback.user.avatar
                listFeedbackFormat.append(feedback)
            serializer = FeedbackSerializer(listFeedbackFormat,many=True)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":[],"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":[],"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":[],"message":"Failed","code":400},status=status.HTTP_200_OK)


@api_view(['DELETE'])
def delete_feedback(request,id_feedback):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        feedback=Feedback.objects.get(id_feedback=id_feedback)
        if feedback is not None:
            try:
                notification=NotificationPost.objects.get(Q(type_notification=3) & Q(id_data=feedback.id_feedback))
                notification.delete()
            except NotificationPost.DoesNotExist:
                print('không có thông báo')
            feedback.delete()
            return Response({"data":"","message":"Success","code":200},status=status.HTTP_200_OK)
        return Response({"data":"","message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        return Response({"data":"","message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        return Response({"data":"","message":"Failed","code":400},status=status.HTTP_200_OK)
    

@api_view(['GET'])
def feedback_detail(request,id_feedback):
    checkAu=checkAuthorization(request)
    if checkAu==0:
        feedback=Feedback.objects.get(id_feedback=id_feedback)
        if feedback is not None:
            feedback.user.avatar=mainUrl+feedback.user.avatar
            serializer = FeedbackSerializer(feedback)
            return Response({"data":serializer.data,"message":"Success","code":200},status=status.HTTP_200_OK)
        serializer = FeedbackSerializer(Feedback())
        return Response({"data":serializer.data,"message":"Not found","code":404},status=status.HTTP_200_OK)
    elif checkAu==1:
        serializer = FeedbackSerializer(Feedback())
        return Response({"data":serializer.data,"message":"Expired","code":401},status=status.HTTP_200_OK)
    else:
        serializer = FeedbackSerializer(Feedback())
        return Response({"data":serializer.data,"message":"Failed","code":400},status=status.HTTP_200_OK)
