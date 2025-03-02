package com.example.managerstaff.api;

import com.example.managerstaff.models.responses.CalendarResponse;
import com.example.managerstaff.models.responses.CheckInResponse;
import com.example.managerstaff.models.responses.CheckOutResponse;
import com.example.managerstaff.models.responses.CommentResponse;
import com.example.managerstaff.models.responses.ListCalendarResponse;
import com.example.managerstaff.models.responses.ListCommentResponse;
import com.example.managerstaff.models.responses.ListFeedbackResponse;
import com.example.managerstaff.models.responses.ListPostResponse;
import com.example.managerstaff.models.responses.ListStatisticalTimekeepingResponse;
import com.example.managerstaff.models.responses.ListTypeCalendarResponse;
import com.example.managerstaff.models.responses.ListTypePostResponse;
import com.example.managerstaff.models.responses.ListUserResponse;
import com.example.managerstaff.models.responses.ListNotificationResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.ListPartResponse;
import com.example.managerstaff.models.responses.PartResponse;
import com.example.managerstaff.models.responses.PaySlipResponse;
import com.example.managerstaff.models.responses.PositionResponse;
import com.example.managerstaff.models.responses.PostResponse;
import com.example.managerstaff.models.responses.SettingResponse;
import com.example.managerstaff.models.responses.StaffFeedBackResponse;
import com.example.managerstaff.models.responses.StatisticalTimeKeepingResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.models.responses.WorkdayResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    ApiService apiService = ApiConfig.getClient("http://192.168.100.195:8000")
            .create(ApiService.class);


    @GET("/api/user/login")
    Call<ObjectResponse> loginUser(@Query("username") String username,
                                   @Query("password") String password);


    @GET("/api/user/user_detail/{id}")
    Call<UserResponse> getUserDetail(@Header("Authorization") String Authorization,
                                     @Path("id") int id);

    @GET("/api/user/get_user_detail_first/{id}")
    Call<UserResponse> getUserDetailFirst(@Header("Authorization") String Authorization,
                                          @Path("id") int id);

    @GET("/api/user/get_part/{id}")
    Call<PartResponse> getPartDetail(@Header("Authorization") String Authorization,
                                     @Path("id") int idPart);

    @GET("/api/calendar/all_type_calendar")
    Call<ListTypeCalendarResponse> getAllTypeCalendar(@Header("Authorization") String Authorization);

    @GET("/api/post/all_post")
    Call<ListPostResponse> getAllPost(@Header("Authorization") String Authorization,
                                      @Query("page") int page,
                                      @Query("size") int size,
                                      @Query("keysearch") String keysearch,
                                      @Query("time_start") String time_start,
                                      @Query("time_end") String time_end,
                                      @Query("id_type_post") int idTypePost);

    @GET("/api/post/all_post_cmt")
    Call<ListPostResponse> getAllPostCmt(@Header("Authorization") String Authorization);

    @GET("/api/user/all_part")
    Call<ListPartResponse> getAllPart(@Header("Authorization") String Authorization);

    @GET("/api/comment/all_comment/{id}")
    Call<ListCommentResponse> getAllComment(@Header("Authorization") String Authorization,
                                            @Path("id") int idPost,
                                            @Query("page") int page,
                                            @Query("size") int size);

    @GET("/api/feedback/all_feedback")
    Call<ListFeedbackResponse> getAllFeedBack(@Header("Authorization") String Authorization,
                                              @Query("page") int page,
                                              @Query("size") int size);

    @DELETE("/api/feedback/delete_feedback/{id}")
    Call<ObjectResponse> deleteFeedback(@Header("Authorization") String Authorization,
                                        @Path("id") int idFeedback);

    @GET("/api/feedback/feedback_detail/{id}")
    Call<StaffFeedBackResponse> getFeedBackDetail(@Header("Authorization") String Authorization,
                                                  @Path("id") int idFeedback);

    @DELETE("/api/calendar/delete_calendar/{id}")
    Call<ObjectResponse> deleteCalendar(@Header("Authorization") String Authorization,
                                        @Path("id") int idCalendar);

    @DELETE("/api/notification/delete_notification/{id}")
    Call<ObjectResponse> deleteNotification(@Header("Authorization") String Authorization,
                                            @Path("id") int idNotification);

    @GET("/api/user/all_position_by_part/{id}")
    Call<PositionResponse> getAllPositionByPart(@Header("Authorization") String Authorization,
                                                @Path("id") int id);

    @POST("/api/user/forgot_password")
    Call<UserResponse> findUser(@Header("Authorization") String Authorization,
                                @Query("username") String username);

    @POST("/api/time/add_payslip_detail")
    Call<ObjectResponse> addPaySlipDetail(@Header("Authorization") String Authorization,
                                          @Query("month") int month,
                                          @Query("year") int year,
                                          @Query("list_time") String listTime,
                                          @Query("list_name_time") String listNameTime);


    @GET("/api/notification/get_all_notification_detail/{id}")
    Call<ListNotificationResponse> getNotificationAllDetail(@Header("Authorization") String Authorization,
                                                            @Path("id") int idUser,
                                                            @Query("page") int page,
                                                            @Query("size") int size);

    @GET("/api/time/get_payslip/{id}")
    Call<PaySlipResponse> getPaySlip(@Header("Authorization") String Authorization,
                                     @Path("id") int idUser,
                                     @Query("month") int month,
                                     @Query("year") int year);

    @GET("/api/notification/read_notification/{id}")
    Call<ObjectResponse> readNotification(@Header("Authorization") String Authorization,
                                          @Path("id") int idNotification);

    @GET("/api/notification/get_all_notification/{id}")
    Call<ListNotificationResponse> getNotification(@Header("Authorization") String Authorization,
                                                   @Path("id") int idUser);

    @GET("/api/notification/read_all_notification/{id}")
    Call<ObjectResponse> readAllNotification(@Header("Authorization") String Authorization,
                                             @Path("id") int idUser);

    @GET("/api/user/confirm_code_password/{id}")
    Call<ObjectResponse> confirmCode(@Header("Authorization") String Authorization,
                                     @Path("id") int idUser,
                                     @Query("code") String code);

    @GET("/api/comment/check_read_comment/{idu}/{idc}")
    Call<ObjectResponse> checkReadComment(@Header("Authorization") String Authorization,
                                          @Path("idu") int idUser,
                                          @Path("idc") int idComment);

    @GET("/api/comment/comment_detail/{idc}")
    Call<CommentResponse> getCommentDetail(@Header("Authorization") String Authorization,
                                           @Path("idc") int idComment);

    @GET("/api/user/check_token")
    Call<ObjectResponse> checkToken(@Query("Authorization") String Authorization);

    @POST("/api/comment/read_comment/{idu}/{idp}")
    Call<ObjectResponse> readComment(@Header("Authorization") String Authorization,
                                     @Path("idu") int idUser,
                                     @Path("idp") int idPost);

    @POST("/api/calendar/add_calendar/{idp}/{idt}")
    Call<ObjectResponse> addCalendar(@Header("Authorization") String Authorization,
                                     @Path("idp") int idPart,
                                     @Path("idt") int idType,
                                     @Query("header_calendar") String headerCalendar,
                                     @Query("body_calendar") String bodyCalendar,
                                     @Query("address") String address,
                                     @Query("day_calendar") String dayCalendar,
                                     @Query("time_start") String timeStart,
                                     @Query("time_end") String timeEnd
    );

    @POST("/api/calendar/add_all_calendar_of_month")
    Call<ObjectResponse> addAllCalendar(@Header("Authorization") String Authorization,
                                        @Query("day_calendar") String dayCalendar,
                                        @Query("month") int month,
                                        @Query("year") int year);

    @Multipart
    @PUT("/api/user/update_user/{id}")
    Call<UserResponse> updateUser(@Header("Authorization") String Authorization,
                                  @Path("id") int id,
                                  @Part MultipartBody.Part image,
                                  @Query("avatar") String avatar,
                                  @Query("full_name") String full_name,
                                  @Query("birthday") String birthday,
                                  @Query("gender") String gender,
                                  @Query("address") String address,
                                  @Query("email") String email,
                                  @Query("phone") String phone,
                                  @Query("wage") double wage,
                                  @Query("id_part") int idPart,
                                  @Query("id_position") int idPosition,
                                  @Query("username") String username,
                                  @Query("password") String password);

    @Multipart
    @POST("/api/user/register_user")
    Call<UserResponse> registerUser(@Header("Authorization") String Authorization,
                                    @Part MultipartBody.Part image,
                                    @Query("avatar") String avatar,
                                    @Query("full_name") String full_name,
                                    @Query("birthday") String birthday,
                                    @Query("gender") String gender,
                                    @Query("address") String address,
                                    @Query("email") String email,
                                    @Query("phone") String phone,
                                    @Query("wage") double wage,
                                    @Query("username") String username,
                                    @Query("password") String password,
                                    @Query("id_part") int idPart,
                                    @Query("id_position") int idPosition);

    @POST("/api/post/add_post/{id}")
    Call<PostResponse> addPost(@Header("Authorization") String Authorization,
                               @Path("id") int idUser,
                               @Query("id_type_post") int idTypePost,
                               @Query("header_post") String headerPost,
                               @Query("time_post") String timePost,
                               @Query("content") String content,
                               @Query("num_like") int numLike,
                               @Query("num_comment") int num_comment);

    @PUT("/api/post/update_post/{id}")
    Call<PostResponse> updatePost(@Header("Authorization") String Authorization,
                                  @Path("id") int idPost,
                                  @Query("id_type_post") int idTypePost,
                                  @Query("header_post") String headerPost,
                                  @Query("content") String content,
                                  @Query("image") String image);

    @PUT("/api/user/change_password/{id}")
    Call<UserResponse> changePassword(@Header("Authorization") String Authorization,
                                      @Path("id") int id,
                                      @Query("password_old") String passwordOld,
                                      @Query("password") String password);

    @PUT("/api/calendar/update_calendar/{idc}/{idp}/{idt}")
    Call<ObjectResponse> updateCalendar(@Header("Authorization") String Authorization,
                                        @Path("idc") int idCalendar,
                                        @Path("idp") int idPart,
                                        @Path("idt") int idType,
                                        @Query("header_calendar") String headerCalendar,
                                        @Query("body_calendar") String bodyCalendar,
                                        @Query("address") String address,
                                        @Query("day_calendar") String dayCalendar,
                                        @Query("time_start") String timeStart,
                                        @Query("time_end") String timeEnd);

    @DELETE("/api/user/delete_user/{id}")
    Call<ObjectResponse> deleteUser(@Header("Authorization") String Authorization,
                                    @Path("id") int id);

    @DELETE("/api/comment/delete_all_comment_user_in_post/{idu}/{idp}")
    Call<ObjectResponse> deleteAllCommentUserInPost(@Header("Authorization") String Authorization,
                                                    @Path("idu") int idUser,
                                                    @Path("idp") int idPost);

    @DELETE("/api/comment/delete_comment/{id}")
    Call<ObjectResponse> deleteComment(@Header("Authorization") String Authorization,
                                       @Path("id") int idComment);

    @GET("/api/post/all_type_post")
    Call<ListTypePostResponse> getAllTypePost(@Header("Authorization") String Authorization);

    @DELETE("/api/post/delete_post/{id}")
    Call<ObjectResponse> deletePost(@Header("Authorization") String Authorization,
                                    @Path("id") int id);

    @POST("/api/time/add_time/{id}")
    Call<CheckInResponse> addTimeIn(@Header("Authorization") String Authorization,
                                    @Path("id") int id,
                                    @Query("day") String day,
                                    @Query("time") String time);

    @POST("/api/time/add_time/{id}")
    Call<CheckOutResponse> addTimeOut(@Header("Authorization") String Authorization,
                                      @Path("id") int id,
                                      @Query("day") String day,
                                      @Query("time") String time);

    @PUT("/api/time/update_time/{id_in}/{id_out}")
    Call<ObjectResponse> updateTime(@Header("Authorization") String Authorization,
                                    @Path("id_in") int idTimeIn,
                                    @Path("id_out") int idTimeOut,
                                    @Query("time_in") String timeIn,
                                    @Query("time_out") String timeOut);

    @DELETE("/api/time/delete_time/{id_in}/{id_out}")
    Call<ObjectResponse> deleteTime(@Header("Authorization") String Authorization,
                                    @Path("id_in") int idTimeIn,
                                    @Path("id_out") int idTimeOut);

    @GET("/api/time/get_workday_detail/{id}")
    Call<WorkdayResponse> getWorkdayDetail(@Header("Authorization") String Authorization,
                                           @Path("id") int id);

    @GET("/api/setting/get_setting")
    Call<SettingResponse> getSetting(@Header("Authorization") String Authorization);

    @PUT("/api/setting/update_setting")
    Call<SettingResponse> updateSetting(@Header("Authorization") String Authorization,
                                        @Query("time_start") String timeStart,
                                        @Query("time_end") String timeEnd,
                                        @Query("overtime") double overtime,
                                        @Query("holiday") double holiday,
                                        @Query("day_off") double dayOff);

    @GET("/api/time/get_list_statistical")
    Call<ListStatisticalTimekeepingResponse> getListStatistic(@Header("Authorization") String Authorization,
                                                              @Query("time_start") String timeStart,
                                                              @Query("time_end") String timeEnd);

    @GET("/api/time/get_list_statistical_user/{id}")
    Call<StatisticalTimeKeepingResponse> getListStatisticOfUser(@Header("Authorization") String Authorization,
                                                                @Path("id") int idUser,
                                                                @Query("time_start") String timeStart,
                                                                @Query("time_end") String timeEnd);

    @GET("/api/calendar/get_calendar/{id}")
    Call<CalendarResponse> getCalendar(@Header("Authorization") String Authorization,
                                       @Path("id") int idCalendar);


    @POST("/api/feedback/add_feedback/{id}")
    Call<ObjectResponse> sendFeedBack(@Header("Authorization") String Authorization,
                                      @Path("id") int id,
                                      @Query("time_feedback") String timeFeedback,
                                      @Query("content") String content);

    @Multipart
    @POST("/api/post/upload_image/{idp}")
    Call<ObjectResponse> uploadImage(@Header("Authorization") String Authorization,
                                     @Path("idp") int idPost,
                                     @Part MultipartBody.Part image);

    @POST("/api/comment/add_comment/{idu}/{idp}")
    Call<CommentResponse> addComment(@Header("Authorization") String Authorization,
                                     @Path("idu") int idUser,
                                     @Path("idp") int idPart,
                                     @Query("time_cmt") String timeComment,
                                     @Query("content") String content);

    @GET("/api/calendar/list_calender_by_part/{id}")
    Call<ListCalendarResponse> getCalendar(@Header("Authorization") String Authorization,
                                           @Path("id") int idPart,
                                           @Query("day_calendar") String dayCalendar);

    @GET("/api/user/all_user")
    Call<ListUserResponse> getListUser(@Header("Authorization") String Authorization,
                                       @Query("page") int page,
                                       @Query("size") int size,
                                       @Query("keysearch") String keysearch);

    @GET("/api/post/post_detail/{id}")
    Call<PostResponse> getPostDetail(@Header("Authorization") String Authorization,
                                     @Path("id") int id);
    
}