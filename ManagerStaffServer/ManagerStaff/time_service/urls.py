from django.urls import path
from time_service import TimeController

urlpatterns = [
    path("add_time/<int:id_user>",TimeController.add_time,name="add_time"),
    path("add_payslip_detail",TimeController.add_payslip_detail,name="add_payslip_detail"),
    path("delete_time/<int:id_time_in>/<int:id_time_out>",TimeController.delete_time,name="delete_time"),
    path("get_payslip/<int:id_user>",TimeController.get_payslip,name="get_payslip"),
    path("update_time/<int:id_time_in>/<int:id_time_out>",TimeController.update_time,name="update_time"),
    path("get_time_user/<int:id_user>",TimeController.get_time_user,name="get_time_user"),
    path("get_workday_detail/<int:id_workday>",TimeController.get_workday_detail,name="get_workday_detail"),
    path("get_list_statistical",TimeController.get_list_statistical,name="get_list_statistical"),
    path("get_list_statistical_user/<int:id_user>",TimeController.get_list_statistical_user,name="get_list_statistical_user"),
]