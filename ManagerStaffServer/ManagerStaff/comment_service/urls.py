from django.urls import path
from comment_service import CommentController

urlpatterns = [
    path("add_comment/<int:id_user>/<int:id_post>",CommentController.add_comment,name="add_comment"),
    path("update_comment/<int:id_comment>",CommentController.update_comment,name="update_comment"),
    path("all_comment/<int:id_post>",CommentController.all_comment,name="all_comment"),
    path("check_read_comment/<int:id_user>/<int:id_comment>",CommentController.check_read_comment,name="check_read_comment"),
    path("read_comment/<int:id_user>/<int:id_post>",CommentController.read_comment,name="read_comment"),
    path("delete_comment/<int:id_comment>",CommentController.delete_comment,name="delete_comment"),
    path("delete_all_comment_user_in_post/<int:id_user>/<int:id_post>",CommentController.delete_all_comment_user_in_post,name="delete_all_comment_user_in_post"),
    path('comment_detail/<int:id_comment>',CommentController.comment_detail,name="comment_detail"),
]