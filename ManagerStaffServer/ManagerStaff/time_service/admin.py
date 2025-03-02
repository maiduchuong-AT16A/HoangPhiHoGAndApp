from django.contrib import admin
from .models import CheckIn,CheckOut,Workday,PaySlip,SalaryPayment,StatisticalTimeKeeping

# Register your models here.
admin.site.register(Workday)
admin.site.register(CheckIn)
admin.site.register(CheckOut)
admin.site.register(PaySlip)
admin.site.register(SalaryPayment)
admin.site.register(StatisticalTimeKeeping)