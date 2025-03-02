from .models import Feedback
from rest_framework import serializers
from user_service.serializer import UserSerializer

class FeedbackSerializer(serializers.ModelSerializer):
    user=UserSerializer()
    class Meta:
        model = Feedback
        fields = '__all__'

        