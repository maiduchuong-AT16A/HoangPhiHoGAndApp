from .models import Comment
from rest_framework import serializers
from user_service.serializer import UserSerializer

class CommentSerializer(serializers.ModelSerializer):
    user=UserSerializer()
    class Meta:
        model = Comment
        fields = '__all__'

    
        