from django.urls import path
from user_service import UserController

urlpatterns = [
    path("register_user",UserController.register_user,name="register_user"),
    path("check_token",UserController.check_token,name="check_token"),
    path("forgot_password",UserController.forgot_password,name="forgot_password"),
    path("confirm_code_password/<int:id_user>",UserController.confirm_code_password,name="confirm_code_password"),
    path("update_user/<int:id_user>",UserController.update_user,name="update_user"),
    path("login",UserController.login,name="login"),
    path("get_code_user",UserController.get_code_user,name="get_code_user"),
    path("all_user",UserController.all_user,name="all_user"),
    path("all_part",UserController.all_part,name="all_part"),
    path("all_position_by_part/<int:id_part>",UserController.all_position_by_part,name="all_position_by_part"),
    path("get_part/<int:id_part>",UserController.get_part,name="get_part"),
    path("delete_user/<int:id_user>",UserController.delete_user,name="delete_user"),
    path('user_detail/<int:id_user>',UserController.user_detail,name="user_detail"),
    path("get_user_detail_first/<int:id_user>",UserController.get_user_detail_first,name="get_user_detail_first"),
    path('change_password/<int:id_user>',UserController.change_password,name="change_password"),
]