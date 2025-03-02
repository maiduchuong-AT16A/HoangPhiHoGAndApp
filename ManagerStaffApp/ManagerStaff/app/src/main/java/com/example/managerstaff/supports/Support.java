package com.example.managerstaff.supports;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.managerstaff.R;
import com.example.managerstaff.activities.ChatActivity;
import com.example.managerstaff.activities.FeedBackActivity;
import com.example.managerstaff.activities.LoginActivity;
import com.example.managerstaff.activities.MainActivity;
import com.example.managerstaff.activities.ManagerFeedBackActivity;
import com.example.managerstaff.activities.PostDetailActivity;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.models.Image;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.Position;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.StatisticalTimeUser;
import com.example.managerstaff.models.TypeCalendar;
import com.example.managerstaff.models.TypePost;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.Workday;
import com.example.managerstaff.models.responses.ListNotificationResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Support {

    public static String UrlMain="http://192.168.100.195:8000/";

    public static boolean checkPassValidate(String password){
        if(password.length()<6) return false;
        String regex = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";
        Pattern specialCharPattern = Pattern.compile(regex);
        Matcher matcher = specialCharPattern.matcher(password);
        boolean containsSpecialChar = matcher.matches();
        boolean containsLetterOrDigit = password.matches(".*[a-zA-Z0-9].*");
        if(containsSpecialChar && containsLetterOrDigit) return true;
        return false;
    }

    public static boolean checkImageAddInPost(List<Image> list,Image image){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getIdImage()==image.getIdImage()){
                return false;
            }
        }
        return true;
    }

    public static boolean checkCommentOfUser(Post post, int idUser){
        for(int i=0;i<post.getListComments().size();i++){
            if(post.getListComments().get(i).getUser().getIdUser()==idUser) return true;
        }
        return false;
    }

    public static String getAuthorization(Activity activity){
        Database database=new Database(activity);
        return database.getSessionAuthorization();
    }


    public static void logOutExpiredToken(Activity activity){
        deleteAuthorization(activity);
        Intent intent=new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public static void deleteAuthorization(Activity activity){
        Database database=new Database(activity);
        database.deleteSession(database.getSessionAuthorization());
    }

    public static void showDialogWarningExpiredAu(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_notify_expired);

        TextView txtConfirm=dialog.findViewById(R.id.txt_btn_confirm);
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logOutExpiredToken(activity);
            }
        });

        dialog.show();

    }

    public static void showDialogWarning(Activity activity,String title){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_notify_expired);

        TextView txtConfirm=dialog.findViewById(R.id.txt_btn_confirm);
        TextView txtTitle=dialog.findViewById(R.id.title);
        txtTitle.setText(title);
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logOutExpiredToken(activity);
            }
        });

        dialog.show();

    }

    public static double convertStringToDouble(String wage){
        String wageNew="";
        for(int i=0;i<wage.length();i++){
            if(wage.charAt(i)!='.'){
                wageNew+=wage.charAt(i);
            }
        }
        return Double.parseDouble(wageNew);

    }

    public static MultipartBody.Part getBytesFromInputStream(Uri uri, Activity activity) throws IOException {

        if((uri.toString().length()>0 && uri.toString().substring(0,4).equals("http")) || uri.toString().length()==0){
            String[] arrayString=uri.toString().split("/");
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), "Nội dung mặc định");
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", arrayString[arrayString.length-1], requestBody);
            return part;
        }

        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteBuffer.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", System.currentTimeMillis()+".jpg", requestFile);
        return body;
    }

    public static List<StatisticalTimeUser> getDatesInMonth(int year, int month) {
        List<StatisticalTimeUser> list=new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < daysInMonth; i++) {
            StatisticalTimeUser timeDay=new StatisticalTimeUser();
            Date currentDate = cal.getTime();
            String dateString = fmt.format(currentDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            String dayOfWeekName = "";
            switch (dayOfWeek) {
                case Calendar.SUNDAY:
                    dayOfWeekName = "Chủ Nhật";
                    break;
                case Calendar.MONDAY:
                    dayOfWeekName = "Thứ Hai";
                    break;
                case Calendar.TUESDAY:
                    dayOfWeekName = "Thứ Ba";
                    break;
                case Calendar.WEDNESDAY:
                    dayOfWeekName = "Thứ Tư";
                    break;
                case Calendar.THURSDAY:
                    dayOfWeekName = "Thứ Năm";
                    break;
                case Calendar.FRIDAY:
                    dayOfWeekName = "Thứ Sáu";
                    break;
                case Calendar.SATURDAY:
                    dayOfWeekName = "Thứ Bảy";
                    break;
            }
            timeDay.setDayOfWeek(dateString);
            timeDay.setDayOfWeekName(dayOfWeekName);
            list.add(timeDay);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    public static int compareToDate(String timeStart,String timeEnd){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date startDate = dateFormat.parse(timeStart);
            Date endDate = dateFormat.parse(timeEnd);

            if (startDate.compareTo(endDate)==0) {
                return 0;
            }else{
                if (startDate.compareTo(endDate)<0) {
                    return -1;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int getIdTypePost(List<TypePost> list, String name){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getTypeName().equals(name)) return list.get(i).getIdType();
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean compareToTime(String timeStart, String timeEnd){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime startTime = LocalTime.parse(timeStart, formatter);
        LocalTime endTime = LocalTime.parse(timeEnd, formatter);

        if (startTime.isAfter(endTime)) return false;
        return true;
    }

    public static String changeReverDateTime(String inputDate,boolean reverse){
        SimpleDateFormat inputFormat = new SimpleDateFormat((reverse)?"dd-MM-yyyy":"yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat((reverse)?"yyyy-MM-dd":"dd-MM-yyyy");
        try {
            Date date = inputFormat.parse(inputDate);

            String formattedDate = outputFormat.format(date);

            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimeNow(){
        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStr = dateFormat.format(currentTime);
        return currentTimeStr;
    }

    public static int getIndexOfName(List<String> list,String name){
        for(int i=0;i<list.size();i++){
            if(list.get(i).equalsIgnoreCase(name)){
                return i;
            }
        }
        return 0;
    }

    public Timer StartTimer(Activity activity, int IdUser, int IdAdmin){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                clickCallApiGetNotification(activity,IdUser,IdAdmin);
            }
        };

        long delay = 1000;
        long period = 5000;

        timer.schedule(timerTask, delay, period);
        return timer;
    }

    public static void showNotification(Activity activity, int IdUser, int IdAdmin, NotificationData notificationData){
        if(notificationData.getTypeNotify()==1) {
            showNotifyPost(activity, notificationData, IdUser, IdAdmin);
        }else{
            if(notificationData.getTypeNotify()==2) {
                showNotifyTimeKeeping(activity, notificationData, IdUser, IdAdmin);
            }else{
                if(notificationData.getTypeNotify()==3) {
                    showNotifyFeedback(activity, notificationData, IdUser, IdAdmin);
                }else{
                    if(notificationData.getTypeNotify()==4) {
                        showNotifyComment(activity,notificationData,IdUser,IdAdmin);
                    }else{
                        showNotifyCalendar(activity,notificationData,IdUser,IdAdmin);
                    }
                }
            }
        }
    }

    public static void clickCallApiGetNotification(Activity activity,int IdUser,int IdAdmin) {
        ApiService.apiService.getNotification(Support.getAuthorization(activity),IdUser).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(Call<ListNotificationResponse> call, Response<ListNotificationResponse> response) {
                ListNotificationResponse notificationResponse = response.body();
                if (notificationResponse != null) {
                    if(notificationResponse.getCode()==200){
                        List<NotificationData> listNotificationPosts=notificationResponse.getListNotificationPosts();
                        for(int i=0;i<listNotificationPosts.size();i++){
                            if(listNotificationPosts.get(i).getTypeNotify()==1) {
                                showNotifyPost(activity, listNotificationPosts.get(i), IdUser, IdAdmin);
                            }else{
                                if(listNotificationPosts.get(i).getTypeNotify()==2) {
                                    showNotifyTimeKeeping(activity, listNotificationPosts.get(i), IdUser, IdAdmin);
                                }else{
                                    if(listNotificationPosts.get(i).getTypeNotify()==3) {
                                        showNotifyFeedback(activity, listNotificationPosts.get(i), IdUser, IdAdmin);
                                    }else{
                                        if(listNotificationPosts.get(i).getTypeNotify()==4) {
                                            showNotifyComment(activity,listNotificationPosts.get(i),IdUser,IdAdmin);
                                        }else{
                                            showNotifyCalendar(activity,listNotificationPosts.get(i),IdUser,IdAdmin);
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        if(notificationResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(activity);
                        }else{
                            Toast.makeText(activity, activity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, activity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListNotificationResponse> call, Throwable t) {
                Toast.makeText(activity, activity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void showNotifyPost(Context context, NotificationData notificationData, int IdUser, int IdAdmin){

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent = new Intent(context, PostDetailActivity.class);
        resultIntent.putExtra("id_user", IdUser);
        resultIntent.putExtra("id_admin", IdAdmin);
        resultIntent.putExtra("action", "notification");
        resultIntent.putExtra("position", 3);
        resultIntent.putExtra("id_post", notificationData.getIdData());
        stackBuilder.addNextIntentWithParentStack(resultIntent);


        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                notificationData.getIdNotify(),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE
        );


        RemoteViews notificationLayout=new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.txt_title, notificationData.getTitle());
        notificationLayout.setTextViewText(R.id.txt_body, notificationData.getBody());
        notificationLayout.setTextViewText(R.id.txt_time, notificationData.getTime());

        Notification notification=new NotificationCompat.Builder(context, SupportNotification.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.logoapp)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(notificationData.getIdNotify(),notification);
        }
    }

    private static void showNotifyTimeKeeping(Context context, NotificationData notificationData, int IdUser, int IdAdmin){

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("id_user", IdUser);
        resultIntent.putExtra("id_admin", IdAdmin);
        resultIntent.putExtra("action", "notification");
        resultIntent.putExtra("id_data", notificationData.getIdData());
        resultIntent.putExtra("day_set_calendar", notificationData.getTime());
        resultIntent.putExtra("position", 1);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                notificationData.getIdNotify(),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE
        );


        RemoteViews notificationLayout=new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.txt_title, notificationData.getTitle());
        notificationLayout.setTextViewText(R.id.txt_body, notificationData.getBody());
        notificationLayout.setTextViewText(R.id.txt_time, notificationData.getTime());

        Notification notification=new NotificationCompat.Builder(context, SupportNotification.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.logoapp)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setCustomBigContentView(notificationLayout)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(notificationData.getIdNotify(),notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatTime(String timeOld) {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime specificTime = LocalDateTime.parse(timeOld, isoFormatter);

        LocalDateTime currentTime = LocalDateTime.now();
        Duration timeDifference = Duration.between(specificTime, currentTime);

        long seconds = timeDifference.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = months / 12;

        String time = "";
        if (years > 0) {
            time = years + " năm trước";
        } else if (months > 0) {
            time = months + " tháng trước";
        } else if (weeks > 0) {
            time = weeks + " tuần trước";
        } else if (days > 0) {
            time = days + " ngày trước";
        } else if (hours >= 1 && hours < 24) {
            time = hours + " giờ trước";
        } else if (minutes > 0) {
            time = minutes + " phút trước";
        } else {
            time = "Vừa xong";
        }
        return time;
    }

    private static void showNotifyCalendar(Context context, NotificationData notificationData, int IdUser, int IdAdmin){

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("id_user", IdUser);
        resultIntent.putExtra("id_admin", IdAdmin);
        resultIntent.putExtra("position", 2);
        resultIntent.putExtra("id_data", notificationData.getIdData());
        resultIntent.putExtra("action", "notification");
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                notificationData.getIdNotify(),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE
        );


        RemoteViews notificationLayout=new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.txt_title, notificationData.getTitle());
        notificationLayout.setTextViewText(R.id.txt_body, notificationData.getBody());
        notificationLayout.setTextViewText(R.id.txt_time, notificationData.getTime());

        Notification notification=new NotificationCompat.Builder(context, SupportNotification.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.logoapp)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setCustomBigContentView(notificationLayout)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(notificationData.getIdNotify(),notification);
        }
    }

    private static void showNotifyComment(Context context, NotificationData notificationData, int IdUser, int IdAdmin){

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent = new Intent(context, ChatActivity.class);
        resultIntent.putExtra("id_user",IdUser);
        resultIntent.putExtra("id_comment",notificationData.getIdData());
        resultIntent.putExtra("action", "notification");
        resultIntent.putExtra("position",3);
        resultIntent.putExtra("id_admin",IdAdmin);
        stackBuilder.addNextIntentWithParentStack(resultIntent);


        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                notificationData.getIdNotify(),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE
        );


        RemoteViews notificationLayout=new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.txt_title, notificationData.getTitle());
        notificationLayout.setTextViewText(R.id.txt_body, notificationData.getBody());
        notificationLayout.setTextViewText(R.id.txt_time, notificationData.getTime());

        Notification notification=new NotificationCompat.Builder(context, SupportNotification.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.logoapp)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setCustomBigContentView(notificationLayout)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(notificationData.getIdNotify(),notification);
        }
    }

    private static void showNotifyFeedback(Context context, NotificationData notificationData, int IdUser, int IdAdmin){

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent=new Intent(context, (IdUser==IdAdmin)? ManagerFeedBackActivity.class: FeedBackActivity.class);
        resultIntent.putExtra("id_user",IdUser);
        resultIntent.putExtra("id_admin",IdAdmin);
        resultIntent.putExtra("action", "notification");
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                notificationData.getIdNotify(),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE
        );


        RemoteViews notificationLayout=new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.txt_title, notificationData.getTitle());
        notificationLayout.setTextViewText(R.id.txt_body, notificationData.getBody());
        notificationLayout.setTextViewText(R.id.txt_time, notificationData.getTime());

        Notification notification=new NotificationCompat.Builder(context, SupportNotification.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.logoapp)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(notificationData.getIdNotify(),notification);
        }
    }



    public static double getCoefficient(String time, Setting setting, String dayName){
        String day=time.substring(0,time.length()-4);
        if(day.equals("01-01") || day.equals("10-03") || day.equals("30-04") || day.equals("01-05") || day.equals("02-09")){
            return setting.getHoliday();
        }
        if(dayName.equals("Thứ Bảy") || dayName.equals("Chủ Nhật")){
            return setting.getDayOff();
        }
        return 1;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getNumberWorking(Workday workday){

        String timeStart=(workday.getListCheckIns()!=null && workday.getListCheckIns().size()>0)?workday.getListCheckIns().get(0).getTimeIn():"",
                timeEnd=(workday.getListCheckOuts()!=null && workday.getListCheckOuts().size()>0)?workday.getListCheckOuts().get(workday.getListCheckOuts().size()-1).getTimeOut():"";

        if(timeEnd.length()>0 && timeStart.length()>0) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            LocalTime localTime1 = LocalTime.parse((timeStart.length()>5)?timeStart:timeStart+":00", formatter);
            LocalTime localTime2 = LocalTime.parse((timeEnd.length()>5)?timeEnd:timeEnd+":00", formatter);

            Duration duration = Duration.between(localTime1, localTime2);

            return (long) duration.toHours();
        }
        return 0;
    }

    public static boolean checkNewNotify(List<NotificationData> list){
        for(NotificationData notificationData:list){
            if(notificationData.getIsRead()!=2) return true;
        }return false;
    }

    public static String formatWage(String wage){
        int t=wage.length();
        String wageFormat=(t<=3)?wage:wage.substring(t-3,t);
        t-=3;
        while(t-3>=0){
            wageFormat=wage.substring(t-3,t)+"."+wageFormat;
            t-=3;
        }
        return ((t>0)?wage.substring(0,t)+".":"")+wageFormat;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static double getWageOfDay(User user, String day, Setting setting,String dayName,int position) {

        double wage = 0;
        long hours = 0;
        double wageOneDay = user.getWage() * getCoefficient(day, setting, dayName);

        //hours = getNumberWorking(user.getListWorkdays().get(position));
        hours--;
        if (hours < 8 && hours >= 4) {
            wage = wageOneDay / 2;
        } else {
            if (hours >= 8) {
                wage = wageOneDay + (hours - 8) * setting.getOvertime() * (wageOneDay / 8);
            }
        }

        return wage;

    }

    public static List<String> getListTypePost(List<TypePost> listTypePosts,boolean isFillter){
        List<String> listType=new ArrayList<>();
        if(isFillter) {
            listType.add("Tất cả");
        }
        for(int i=0;i<listTypePosts.size();i++){
            listType.add(listTypePosts.get(i).getTypeName());
        }
        return listType;
    }


    public static String convertListImageToString(List<Image> list){
        String str="";
        for(int i=0;i<list.size();i++){
            str+=list.get(i).getImage()+" ";
        }
        return str.trim();
    }

    public static boolean checkEqualsString(String parent,String str){
        for(int i=0;i<parent.length()-str.length()+1;i++){
            if(parent.substring(i,i+str.length()).equalsIgnoreCase(str)){
                return true;
            }
        }
        return false;
    }

    public static String getFormatString(int day, int month, int year){
        return ((day<10)?"0":"")+day+"-"+((month<10)?"0":"")+month+"-"+year;
    }

    public static boolean checkIsWithinApprox(String timeStart,String timeEnd,String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date startDate = dateFormat.parse(timeStart);
            Date endDate = dateFormat.parse(timeEnd);
            Date checkDate = dateFormat.parse(time);

            if (checkDate.compareTo(startDate) >= 0 && checkDate.compareTo(endDate) <= 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Post> filterPost(List<Post> listPosts, String timeStart, String timeEnd, String type){
        if(timeStart.length()==0 || timeEnd.length()==0 || type.length()==0) return  listPosts;
        List<Post> list=new ArrayList<>();
        for(int i=0;i<listPosts.size();i++){
            if((listPosts.get(i).getTypePost().equals(type)||type.equals("Tất cả")) && checkIsWithinApprox(timeStart,timeEnd,listPosts.get(i).getTimePost().substring(0,10)) ){
                list.add(listPosts.get(i));
            }
        }
        return list;
    }

    public static List<Post> searchListPosts(List<Post> list,String keysearch){
        if(keysearch.length()==0) return list;
        List<Post>listPostSearchs=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(Support.checkEqualsString(list.get(i).getHeaderPost(),keysearch)){
                listPostSearchs.add(list.get(i));
            }
        }
        return listPostSearchs;
    }

    public static List<User> searchListUsers(List<User> list,String keysearch){
        if(keysearch.length()==0) return list;
        List<User>listUserSearchs=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(Support.checkEqualsString(list.get(i).getFullName(),keysearch)){
                listUserSearchs.add(list.get(i));
            }
        }
        return listUserSearchs;
    }

    public static List<String> getNamePart(List<Part> list){
        List<String>listNamePart=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listNamePart.add(list.get(i).getNamePart());
        }
        return listNamePart;
    }

    public static List<String> getNameTypeCalendar(List<TypeCalendar> list){
        List<String>listNameCalendar=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listNameCalendar.add(list.get(i).getTypeName());
        }
        return listNameCalendar;
    }

    public  static void showDialogWarningSetTimeDay(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Thời gian bạn chọn không hợp lệ.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public  static void showDialogNotifySaveSuccess(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Lưu thành công.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public  static void showDialogNotifyUpdateSuccess(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Cập nhật thành công.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public  static void showDialogNotifyUpdateFalse(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Cập nhật không thành công.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public  static void showDialogNotifySaveFalse(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Lưu không thành công.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public  static void showDialogWarningSaveCalender(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("Xin lỗi! Thời gian diễn ra sự kiện đã bị trùng.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public static List<String> getNamePosition(List<Position> list){
        List<String>listNamePosition=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listNamePosition.add(list.get(i).getNamePosition());
        }
        return listNamePosition;
    }

    public static String getDayNow(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String timeNow=dateFormat.format(cal.getTime());
        return timeNow;
    }

    public static String getTimeNow2(){
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(currentTime);
        return formattedTime;
    }

    public static String defineTime(String time){
        String str[]=time.split("-");
        return "Ngày " +str[0]+" Tháng "+str[1];
    }

    public static String defineTimeDetail(String time){
        String str[]=time.split("-");
        return "Ngày " +str[0]+" Tháng "+str[1]+" Năm "+str[2];
    }


}
