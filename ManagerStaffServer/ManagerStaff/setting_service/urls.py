from django.urls import path
from setting_service import SettingController

urlpatterns = [
    path("update_setting",SettingController.update_setting,name="update_setting"),
    path("get_setting",SettingController.get_setting,name="get_setting"),
]