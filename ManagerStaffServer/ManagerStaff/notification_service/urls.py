from django.urls import path
from notification_service import NotificationController

urlpatterns = [
        path("get_all_notification/<int:id_user>",NotificationController.get_all_notification,name="get_all_notification"),
        path("get_all_notification_detail/<int:id_user>",NotificationController.get_all_notification_detail,name="get_all_notification_detail"),
        path("delete_notification/<int:id_notification>",NotificationController.delete_notification,name="delete_notification"),
        path("read_all_notification/<int:id_user>",NotificationController.read_all_notification,name="read_all_notification"),
        path("read_notification/<int:id_notification>",NotificationController.read_notification,name="read_notification"),
]