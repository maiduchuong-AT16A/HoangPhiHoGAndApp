from .models import CheckIn,CheckOut,Workday,PaySlip,StatisticalTimeKeeping
from rest_framework import serializers
from datetime import datetime
from calendar_service.models import Calendar
from calendar_service.serializer import CalendarSerializer
from user_service.serializer import UserSerializer

class PaySlipSericalizer(serializers.ModelSerializer):
    workdays=serializers.SerializerMethodField()
    user=UserSerializer()
    class Meta:
        model=PaySlip
        fields='__all__'
    
    def get_workdays(self, obj):
        workdays = Workday.objects.filter(payslip=obj).order_by('day')
        for i in range(len(workdays)):
            date_obj = datetime.strptime(str(workdays[i].day), '%Y-%m-%d')
            new_s = date_obj.strftime('%d-%m-%Y')
            workdays[i].day=new_s
        return WorkdaySerializer(workdays, many=True).data
    
    

class WorkdaySerializer(serializers.ModelSerializer):
    check_ins =serializers.SerializerMethodField()  
    check_outs = serializers.SerializerMethodField()  
    calendars = serializers.SerializerMethodField()  
    class Meta:
        model = Workday
        fields = '__all__'
    
    def get_calendars(self, obj):
        calendars = Calendar.objects.filter(work_day=obj)
        return CalendarSerializer(calendars, many=True).data
    
    def get_check_ins(self, obj):
        check_ins = CheckIn.objects.filter(workday=obj).order_by('time_in')
        return CheckInSerializer(check_ins, many=True).data

    def get_check_outs(self, obj):
        check_outs = CheckOut.objects.filter(workday=obj).order_by('time_out')
        return CheckOutSerializer(check_outs, many=True).data
    

class CheckInSerializer(serializers.ModelSerializer):
    class Meta:
        model = CheckIn
        fields = '__all__'
        
class StatisticalTimeKeepingSerializer(serializers.ModelSerializer):
    workdays=WorkdaySerializer(many=True)
    user=UserSerializer()
    class Meta:
        model = StatisticalTimeKeeping
        fields = '__all__'

class CheckOutSerializer(serializers.ModelSerializer):
    class Meta:
        model = CheckOut
        fields = '__all__'