from .models import User,Account,Part,Position
from rest_framework import serializers
from notification_service.models import NotificationPost
from notification_service.serializer import NotificationPostSerializer

class PartSerializer(serializers.ModelSerializer):
    class Meta:
        model = Part
        fields = '__all__'
    
class PositionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Position
        fields = '__all__'

class AccountSerializer(serializers.ModelSerializer):
    class Meta:
        model = Account
        fields = '__all__'

class UserSerializer(serializers.ModelSerializer):
    part = PartSerializer()
    position = PositionSerializer()
    account = serializers.SerializerMethodField()     
    notifications = serializers.SerializerMethodField()  
    class Meta:
        model = User
        fields = '__all__'
    
    def get_account(self, obj):
        if obj:
            try:
                account = Account.objects.get(user=obj)
                return AccountSerializer(account).data
            except Account.DoesNotExist:
                return AccountSerializer(Account()).data 
        return AccountSerializer(Account()).data
    
    def get_notifications(self, obj):
        notifications = NotificationPost.objects.filter(user=obj)
        return NotificationPostSerializer(notifications, many=True).data
    
    
        