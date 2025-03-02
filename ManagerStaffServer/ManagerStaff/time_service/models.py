from django.db import models
from user_service.models import User


class PaySlip(models.Model):
    id_pay_slip=models.AutoField(primary_key=True)
    num_day_off= models.IntegerField()
    price = models.FloatField()
    num_work_day=models.IntegerField()
    evaluate=models.CharField(max_length=250)
    note=models.CharField(max_length=250)
    month=models.IntegerField()
    num_late_day=models.IntegerField()
    num_leave_early=models.IntegerField()
    year=models.IntegerField()
    user  =  models.ForeignKey(User, null=True, on_delete=models.CASCADE)

    def set_id_pay_slip(self,id_pay_slip):
        self.id_pay_slip=id_pay_slip
    
    def set_num_day_off(self,num_day_off):
        self.num_day_off=num_day_off
    
    def set_price(self,price):
        self.price=price
    
    def set_num_work_day(self,num_work_day):
        self.num_work_day=num_work_day
    
    def set_evaluate(self,evaluate):
        self.evaluate=evaluate
    
    def set_note(self,note):
        self.note=note
    
    def set_month(self,month):
        self.month=month
    
    def set_year(self,year):
        self.year=year
    
    def set_user(self,user):
        self.user=user
    
    def set_num_late_day(self,num_late_day):
        self.num_late_day=num_late_day
    
    def set_num_leave_early(self,num_leave_early):
        self.num_leave_early=num_leave_early

class Workday(models.Model):
    id_workday=models.AutoField(primary_key=True)
    day=models.DateField()
    name_day=models.CharField(max_length=250)
    wage_sum=models.CharField(max_length=250)
    num_hour_work=models.IntegerField()
    status=models.CharField(max_length=250)
    num_errors=models.IntegerField()
    payslip  =  models.ForeignKey(PaySlip, null=True, on_delete=models.CASCADE)
    
    def set_id_workday(self,id_workday):
        self.id_workday=id_workday
    
    def set_day(self,day):
        self.day=day
            
    def set_name_day(self,name_day):
        self.name_day=name_day
    
    def set_num_errors(self,num_errors):
        self.num_errors=num_errors
        
    def set_status(self,status):
        self.status=status
        
    def set_wage_sum(self,wage_sum):
        self.wage_sum=wage_sum
    
    def set_num_hour_work(self,num_hour_work):
        self.num_hour_work=num_hour_work
        
    def set_payslip(self,payslip):
        self.payslip=payslip
   
class CheckIn(models.Model):
    id_check_in=models.AutoField(primary_key=True)
    time_in=models.TimeField()
    money=models.FloatField()
    status=models.CharField(max_length=250)
    time_difference=models.CharField(max_length=250)
    workday  =  models.ForeignKey(Workday, null=True, on_delete=models.CASCADE)

    def set_id_check_in(self,id_check_in):
        self.id_check_in=id_check_in

    def set_time_in(self,time_in):
        self.time_in=time_in
    
    def set_time_difference(self,time_difference):
        self.time_difference=time_difference

    def set_money(self,money):
        self.money=money

    def set_status(self,status):
        self.status=status

    def set_workday(self,workday):
        self.workday=workday 
    
class CheckOut(models.Model):
    id_check_out=models.AutoField(primary_key=True)
    time_out=models.TimeField()
    money=models.FloatField()
    status=models.CharField(max_length=250)
    time_difference=models.CharField(max_length=250)
    workday  =  models.ForeignKey(Workday, null=True, on_delete=models.CASCADE)

    def set_id_check_out(self,id_check_out):
        self.id_check_out=id_check_out

    def set_time_out(self,time_out):
        self.time_out=time_out

    def set_money(self,money):
        self.money=money

    def set_status(self,status):
        self.status=status

    def set_time_difference(self,time_difference):
        self.time_difference=time_difference

    def set_workday(self,workday):
        self.workday=workday 
    
class SalaryPayment(models.Model):
    id_payment=models.AutoField(primary_key=True)
    total_wage=models.FloatField()
    time=models.DateTimeField()
    content=models.CharField(max_length=250)
    type_payment=models.CharField(max_length=250)
    payslip  =  models.ForeignKey(PaySlip, null=True, on_delete=models.CASCADE)

    def set_id_payment(self,id_payment):
        self.id_payment=id_payment

    def set_total_wage(self,total_wage):
        self.total_wage=total_wage

    def set_time(self,time):
        self.time=time

    def set_content(self,content):
        self.content=content

    def set_type_payment(self,type_payment):
        self.type_payment=type_payment

    def set_payslip(self,payslip):
        self.payslip=payslip 

class StatisticalTimeKeeping(models.Model):
    id_statistical = models.IntegerField(default=0)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    num_day_off = models.IntegerField(default=0)
    num_workday = models.IntegerField(default=0)
    num_late_day = models.IntegerField(default=0)
    num_leave_early = models.IntegerField(default=0)
    total_errors = models.IntegerField(default=0)
    workdays = models.ManyToManyField(Workday)
    
    def set_id_statistical(self,id_statistical):
        self.id_statistical=id_statistical

    def set_user(self,user):
        self.user=user

    def set_num_day_off(self,num_day_off):
        self.num_day_off=num_day_off

    def set_num_workday(self,num_workday):
        self.num_workday=num_workday

    def set_num_late_day(self,num_late_day):
        self.num_late_day=num_late_day

    def set_num_leave_early(self,num_leave_early):
        self.num_leave_early=num_leave_early 

    def set_total_errors(self,total_errors):
        self.total_errors=total_errors

    def set_workdays(self,workdays):
        self.workdays=workdays 