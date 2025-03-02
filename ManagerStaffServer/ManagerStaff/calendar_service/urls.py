from django.urls import path
from calendar_service import CalendarController

urlpatterns = [
    path("list_calender_by_part/<int:id_user>",CalendarController.list_calender_by_part,name="list_calender_by_part"),
    path("add_calendar/<int:id_user>/<int:id_type_calendar>",CalendarController.add_calendar,name="add_calendar"),
    path("add_all_calendar_of_month",CalendarController.add_all_calendar_of_month,name="add_all_calendar_of_month"),
    path("update_calendar/<int:id_calendar>/<int:id_user>/<int:id_type_calendar>",CalendarController.update_calendar,name="update_calendar"),
    path("all_type_calendar",CalendarController.all_type_calendar,name="all_type_calendar"),
    path("delete_calendar/<int:id_calendar>",CalendarController.delete_calendar,name="delete_calendar"),
    path('get_calendar/<int:id_calendar>',CalendarController.get_calendar,name="get_calendar"),
]