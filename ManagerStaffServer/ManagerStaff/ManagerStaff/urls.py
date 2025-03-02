from django.contrib import admin
from django.urls import path,include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework_simplejwt import views as jwt_views

urlpatterns = [
    path("admin/", admin.site.urls),
    path("", include("web.urls")),
    path("api/user/", include("user_service.urls")),
    path("api/comment/", include("comment_service.urls")),
    path("api/post/", include("post_service.urls")),
    path("api/setting/", include("setting_service.urls")),
    path("api/time/", include("time_service.urls")),
    path("api/feedback/", include("feedback_service.urls")),
    path("api/calendar/", include("calendar_service.urls")),
    path("api/notification/", include("notification_service.urls")),
]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
