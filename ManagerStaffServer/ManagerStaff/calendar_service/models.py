from django.db import models
from user_service.models import Part
from time_service.models import Workday

class TypeCalendar(models.Model): #(tự viết vào bảng)
    id_type=models.AutoField(primary_key=True)
    type_name=models.CharField(max_length=250)
    describe=models.TextField()

    def set_id_type(self, id_type):
        self.id_type=id_type
    
    def set_type_name(self, type_name):
        self.type_name=type_name
        
    def set_describe(self, describe):
        self.describe=describe

# Create your models here.
class Calendar(models.Model): #lịch làm việc 
    id_calendar=models.AutoField(primary_key=True)
    header_calendar=models.CharField(max_length=250)
    body_calendar=models.TextField()
    address=models.CharField(max_length=250)
    time_start=models.TimeField()
    time_end=models.TimeField()
    work_day  =  models.ForeignKey(Workday, null=True, on_delete=models.CASCADE)
    type_calendar  =  models.ForeignKey(TypeCalendar, null=True, on_delete=models.CASCADE)

    def set_id_calendar(self, id_calendar):
        self.id_calendar = id_calendar

    def set_header_calendar(self, header_calendar):
        self.header_calendar = header_calendar

    def set_body_calendar(self, body_calendar):
        self.body_calendar = body_calendar

    def set_type_calendar(self, type_calendar):
        self.type_calendar = type_calendar

    def set_address(self, address):
        self.address = address

    def set_time_start(self, time_start):
        self.time_start = time_start

    def set_time_end(self, time_end):
        self.time_end = time_end
    
    def set_work_day(self, work_day):
        self.work_day = work_day