from django.urls import path
from feedback_service import FeedbackController

urlpatterns = [
    path("add_feedback/<int:id_user>",FeedbackController.add_feedback,name="add_feedback"),
    path("all_feedback",FeedbackController.all_feedback,name="all_feedback"),
    path("delete_feedback/<int:id_feedback>",FeedbackController.delete_feedback,name="delete_feedback"),
    path("feedback_detail/<int:id_feedback>",FeedbackController.feedback_detail,name="feedback_detail"),
]