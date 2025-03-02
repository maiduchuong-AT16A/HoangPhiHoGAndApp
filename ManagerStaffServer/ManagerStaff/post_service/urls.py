from django.urls import path
from post_service import PostController

urlpatterns = [
    path("add_post/<int:id_user>",PostController.add_post,name="add_post"),
    path("update_post/<int:id_post>",PostController.update_post,name="update_post"),
    path("all_post",PostController.all_post,name="all_post"),
    path("all_post_cmt",PostController.all_post_cmt,name="all_post_cmt"),
    path("all_type_post",PostController.all_type_post,name="all_type_post"),
    path("delete_post/<int:id_post>",PostController.delete_post,name="delete_post"),
    path('post_detail/<int:id_post>',PostController.post_detail,name="post_detail"),
    path("upload_image/<int:id_post>",PostController.upload_image,name="upload_image"),
]