package com.example.managerstaff.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.ChatActivity;
import com.example.managerstaff.activities.PostDetailActivity;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.Comment;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.responses.CommentResponse;
import com.example.managerstaff.models.responses.StaffFeedBackResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Support;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Activity mActivity;
    private List<NotificationData> listNotification;
    private int idUser=0,IdAdmin;

    private NotificationAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(NotificationAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public NotificationAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setData(List<NotificationData> listNotification){
        this.listNotification = listNotification;
        notifyDataSetChanged();
    }

    public void addAll(List<NotificationData> listNotification){
        this.listNotification.addAll(listNotification);
        notifyDataSetChanged();
    }

    public void readAllNotification(List<NotificationData> listNotification){
        for(int i=0;i<listNotification.size();i++){
            listNotification.get(i).setIsRead(2);
        }
        this.listNotification=listNotification;
        notifyDataSetChanged();
    }

    public void readNotification(int position){
        this.listNotification.get(position).setIsRead(2);
        notifyDataSetChanged();
    }

    public void addNotificationFirst(NotificationData notificationData){
        List<NotificationData> list=new ArrayList<>();
        list.add(notificationData);
        list.addAll(this.listNotification);
        this.listNotification=list;
        notifyDataSetChanged();
    }
    public List<NotificationData> getListNotification() {
        return listNotification;
    }

    public void removeData(NotificationData notificationData){
        this.listNotification.remove(notificationData);
        notifyDataSetChanged();
    }

    public void setIdUser(int idUser){
        this.idUser=idUser;
    }

    public void setIdAdmin(int idAdmin) {
        IdAdmin = idAdmin;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        int p =position;
        NotificationData notificationData = listNotification.get(position);
        if(notificationData !=null){

            holder.txtHeader.setText(notificationData.getTitle()+": "+notificationData.getBody());
            holder.txtTime.setText(notificationData.getTime());
            if(notificationData.getIsRead()<2){
                holder.imgIsRead.setVisibility(View.VISIBLE);
                holder.layoutData.setBackgroundColor(mActivity.getColor(R.color.gray2));
            }else{
                holder.imgIsRead.setVisibility(View.GONE);
                holder.layoutData.setBackgroundColor(mActivity.getColor(R.color.white));
            }
            if(notificationData.getTypeNotify()==1){
                holder.imgDescribe.setImageDrawable(mActivity.getDrawable(R.drawable.icon_new_notify));
                holder.imgDescribe.setBackgroundColor(mActivity.getColor(R.color.blue));
                clickCallApiGetUserDetail(IdAdmin,holder);
            }else{
                if(notificationData.getTypeNotify()==2){
                    holder.imgDescribe.setImageDrawable(mActivity.getDrawable(R.drawable.icon_table_notify));
                    holder.imgDescribe.setBackgroundColor(mActivity.getColor(R.color.red));
                    holder.imgImage.setImageDrawable(mActivity.getDrawable(R.drawable.logoapp));
                }else{
                    if(notificationData.getTypeNotify()==3){
                        holder.imgDescribe.setImageDrawable(mActivity.getDrawable(R.drawable.icon_reply_notify));
                        holder.imgDescribe.setBackgroundColor(mActivity.getColor(R.color.gray));
                        clickCallApiGetFeedBackDetail(notificationData.getIdData(),holder);
                    }else{
                        if(notificationData.getTypeNotify()==4){
                            holder.imgDescribe.setImageDrawable(mActivity.getDrawable(R.drawable.icon_comment_notify));
                            holder.imgDescribe.setBackgroundColor(mActivity.getColor(R.color.green1));
                            clickCallApiCommentDetail(notificationData.getIdData(),holder);
                        }else{
                            holder.imgDescribe.setImageDrawable(mActivity.getDrawable(R.drawable.icon_calendar_notify));
                            holder.imgDescribe.setBackgroundColor(mActivity.getColor(R.color.blue));
                            clickCallApiGetUserDetail(IdAdmin,holder);
                        }
                    }
                }
            }

            if(onClickListener!=null) {
                holder.layout_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onItemClick(p);
                    }
                });
            }
        }
    }

    private void clickCallApiCommentDetail(int IdCmt, @NonNull NotificationViewHolder holder) {
        ApiService.apiService.getCommentDetail(Support.getAuthorization(mActivity),IdCmt).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (commentResponse != null) {
                    if(commentResponse.getCode()==200){
                        if(commentResponse.getComment().getUser().getAvatar()!=null) {
                            Glide.with(mActivity).load(commentResponse.getComment().getUser().getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(holder.imgImage);
                        }
                    }else{
                        if(commentResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(mActivity);
                        }else{
                            Toast.makeText(mActivity, mActivity.getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(mActivity, mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clickCallApiGetUserDetail(int IdUser, @NonNull NotificationViewHolder holder) {
        ApiService.apiService.getUserDetail(Support.getAuthorization(mActivity),IdUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        if(userResponse.getUser().getAvatar()!=null) {
                            Glide.with(mActivity).load(userResponse.getUser().getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(holder.imgImage);
                        }
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(mActivity);
                        }else{
                            Toast.makeText(mActivity, mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(mActivity,mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickCallApiGetFeedBackDetail(int IdData, @NonNull NotificationViewHolder holder) {
        ApiService.apiService.getFeedBackDetail(Support.getAuthorization(mActivity),IdData).enqueue(new Callback<StaffFeedBackResponse>() {
            @Override
            public void onResponse(Call<StaffFeedBackResponse> call, Response<StaffFeedBackResponse> response) {
                StaffFeedBackResponse staffFeedBackResponse = response.body();
                if (staffFeedBackResponse != null) {
                    if(staffFeedBackResponse.getCode()==200){
                        if(staffFeedBackResponse.getFeedBack().getUser().getAvatar()!=null) {
                            Glide.with(mActivity).load(staffFeedBackResponse.getFeedBack().getUser().getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(holder.imgImage);
                        }
                    }else{
                        if(staffFeedBackResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(mActivity);
                        }else{
                            Toast.makeText(mActivity, mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StaffFeedBackResponse> call, Throwable t) {
                Toast.makeText(mActivity,mActivity.getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listNotification !=null) return listNotification.size();
        return 0;
    }
    public class  NotificationViewHolder extends RecyclerView.ViewHolder{
        private CardView layout_item;
        private TextView txtHeader,txtTime;
        private ImageView imgImage,imgDescribe,imgIsRead;
        public View layoutData;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_item=itemView.findViewById(R.id.layout_item_notification);
            layoutData=itemView.findViewById(R.id.layout_data);
            txtTime=itemView.findViewById(R.id.txt_time);
            txtHeader=itemView.findViewById(R.id.txt_header_notification);
            imgImage=itemView.findViewById(R.id.img_image);
            imgDescribe=itemView.findViewById(R.id.img_icon_describe);
            imgIsRead=itemView.findViewById(R.id.img_is_read);

        }
    }
}