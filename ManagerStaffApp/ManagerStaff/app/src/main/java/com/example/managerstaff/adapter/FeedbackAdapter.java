package com.example.managerstaff.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.models.StaffFeedBack;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Support;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private Activity mActivity;

    private List<StaffFeedBack> listFeedbacks;
    private int REQUEST_CODE=100;
    private String action;

    private int IdUser;

    private FeedbackAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(FeedbackAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public FeedbackAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setIdUser(int IdUser){
        this.IdUser=IdUser;
    }

    public void setData(List<StaffFeedBack> listFeedbacks){
        this.listFeedbacks=listFeedbacks;
        notifyDataSetChanged();
    }

    public List<StaffFeedBack> getListFeedbacks() {
        return listFeedbacks;
    }

    public void addAllData(List<StaffFeedBack> listFeedbacks){
        this.listFeedbacks.addAll(listFeedbacks);
        notifyDataSetChanged();
    }

    public void removeData(StaffFeedBack staffFeedBack){
        this.listFeedbacks.remove(staffFeedBack);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackAdapter.FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback,parent,false);
        return new FeedbackAdapter.FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        int p=position;
        StaffFeedBack staffFeedBack =listFeedbacks.get(position);
        if(staffFeedBack !=null){
            holder.txtTimeFeedback.setText(staffFeedBack.getTimeFeedback());
            holder.txtContentFeedback.setText(staffFeedBack.getContent());
            holder.txtNameUser.setText(staffFeedBack.getUser().getFullName());
            if(staffFeedBack.getUser().getAvatar().length()>0){
                Glide.with(mActivity).load(staffFeedBack.getUser().getAvatar())
                        .error(R.drawable.icon_user_gray)
                        .placeholder(R.drawable.icon_user_gray)
                        .into(holder.imgAvatarUser);
            }
            holder.imgShow.setTag("hide");
            holder.imgShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.imgShow.getTag().toString().equals("hide")){
                        holder.imgShow.setTag("show");
                        holder.imgShow.setImageDrawable(mActivity.getDrawable(R.drawable.icon_show_up));
                        holder.layoutContent.setVisibility(View.VISIBLE);
                        holder.layoutDelete.setVisibility(View.VISIBLE);
                    }else{
                        holder.imgShow.setTag("hide");
                        holder.imgShow.setImageDrawable(mActivity.getDrawable(R.drawable.icon_show_down));
                        holder.layoutContent.setVisibility(View.GONE);
                        holder.layoutDelete.setVisibility(View.GONE);
                    }
                }
            });
            if(onClickListener!=null) {
                holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onItemClick(p);
                    }
                });
            }
        }

    }


    @Override
    public int getItemCount() {
        if(listFeedbacks!=null) return listFeedbacks.size();
        return 0;
    }
    public class  FeedbackViewHolder extends RecyclerView.ViewHolder{

        private TextView txtNameUser,txtContentFeedback,txtTimeFeedback;
        private ImageView imgAvatarUser,imgShow;
        private ConstraintLayout layoutDelete,layoutContent;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameUser=itemView.findViewById(R.id.txt_name_user);
            txtContentFeedback=itemView.findViewById(R.id.txt_content_feedback);
            txtTimeFeedback=itemView.findViewById(R.id.txt_time_feedback);
            imgAvatarUser=itemView.findViewById(R.id.img_avatar_user);
            imgShow=itemView.findViewById(R.id.img_show);
            layoutDelete=itemView.findViewById(R.id.layout_delete);
            layoutContent=itemView.findViewById(R.id.layout_content_feedback);
        }
    }
}