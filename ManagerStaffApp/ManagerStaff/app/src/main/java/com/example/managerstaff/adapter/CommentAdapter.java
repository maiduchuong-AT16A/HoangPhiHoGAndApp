package com.example.managerstaff.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.models.Comment;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Support;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Activity mActivity;
    private List<Comment> mListComment;
    private int idUser;
    private User user, userAdmin;

    private CommentAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(CommentAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public CommentAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserAdmin(User userAdmin) {
        this.userAdmin = userAdmin;
    }

    public CommentAdapter(Activity mActivity, User user, User userAdmin) {
        this.mActivity = mActivity;
        this.user=user;
        this.userAdmin=userAdmin;
    }

    public void setData(List<Comment> list) {
        this.mListComment = list;
        notifyDataSetChanged();
    }

    public void addAllData(List<Comment> list) {
        this.mListComment.addAll(list);
        notifyDataSetChanged();
    }

    public List<Comment> getmListComment() {
        return mListComment;
    }

    public void removeData(Comment comment){
        this.mListComment.remove(comment);
        notifyDataSetChanged();
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void addComment(Comment comment) {
        List<Comment> list=new ArrayList<>();
        list.add(comment);
        list.addAll(mListComment);
        this.mListComment=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        int p=position;
        Comment comment = mListComment.get(position);
        if(comment!=null){
            if(comment.getUser().getIdUser()==user.getIdUser()){
                holder.txtNameSender.setVisibility(View.VISIBLE);
                holder.cvUserSender.setVisibility(View.VISIBLE);
                holder.layoutBodySender.setVisibility(View.VISIBLE);
                holder.cvUserReceiver.setVisibility(View.GONE);
                holder.layoutBodyReceiver.setVisibility(View.GONE);
                holder.txtNameReceiver.setVisibility(View.GONE);

                holder.commentBodySender.setText(comment.getContent());
                if(position==mListComment.size()-1 || mListComment.get(position+1).getUser().getIdUser()!=comment.getUser().getIdUser()){
                    holder.txtSpaceSender.setVisibility(View.VISIBLE);
                    holder.txtNameSender.setVisibility(View.VISIBLE);
                    holder.cvUserSender.setVisibility(View.VISIBLE);
                    holder.txtNameSender.setText(mActivity.getString(R.string.you));
                }else{
                    holder.txtSpaceSender.setVisibility(View.GONE);
                    holder.cvUserSender.setVisibility(View.INVISIBLE);
                    holder.txtNameSender.setVisibility(View.GONE);
                }
                Glide.with(mActivity).load(user.getAvatar())
                        .error(R.drawable.icon_user_gray)
                        .placeholder(R.drawable.icon_user_gray)
                        .into(holder.imgUserSender);
                if(onClickListener!=null) {
                    holder.layoutBodySender.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            onClickListener.onItemClick(p);
                            return true;
                        }
                    });
                }
            }else{
                holder.txtNameSender.setVisibility(View.GONE);
                holder.cvUserSender.setVisibility(View.GONE);
                holder.layoutBodySender.setVisibility(View.GONE);
                holder.txtNameReceiver.setVisibility(View.VISIBLE);
                holder.cvUserReceiver.setVisibility(View.VISIBLE);
                holder.layoutBodyReceiver.setVisibility(View.VISIBLE);
                holder.commentBodyReceiver.setText(comment.getContent());
                if(position==mListComment.size()-1 || mListComment.get(position+1).getUser().getIdUser()!=comment.getUser().getIdUser()){
                    holder.txtSpace.setVisibility(View.VISIBLE);
                    holder.txtNameReceiver.setVisibility(View.VISIBLE);
                    holder.cvUserReceiver.setVisibility(View.VISIBLE);
                    holder.txtNameReceiver.setText(comment.getUser().getFullName());
                    if(user.getAvatar().length()>0){
                        Glide.with(mActivity).load(comment.getUser().getAvatar())
                                .error(R.drawable.icon_user_gray)
                                .placeholder(R.drawable.icon_user_gray)
                                .into(holder.imgUserReceiver);
                    }
                }else{
                    holder.txtSpace.setVisibility(View.GONE);
                    holder.cvUserReceiver.setVisibility(View.INVISIBLE);
                    holder.txtNameReceiver.setVisibility(View.GONE);
                }
            }
        }


    }


    @Override
    public int getItemCount() {
        if (mListComment != null) return mListComment.size();
        return 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUserReceiver,imgUserSender;
        private CardView cvUserReceiver,cvUserSender;
        private ConstraintLayout layoutBodyReceiver,layoutBodySender;
        private TextView commentBodyReceiver,commentBodySender,txtNameSender,txtNameReceiver,txtSpace,txtSpaceSender;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserReceiver=itemView.findViewById(R.id.ivuser_receive);
            imgUserSender=itemView.findViewById(R.id.ivuser_send);
            cvUserReceiver=itemView.findViewById(R.id.cardView1);
            cvUserSender=itemView.findViewById(R.id.cardView2);
            layoutBodyReceiver=itemView.findViewById(R.id.layout_receiver);
            txtSpace=itemView.findViewById(R.id.txt_space);
            txtSpaceSender=itemView.findViewById(R.id.txt_space_sender);
            layoutBodySender=itemView.findViewById(R.id.layout_sender);
            commentBodyReceiver=itemView.findViewById(R.id.body_commnet_receive);
            commentBodySender=itemView.findViewById(R.id.body_commnet_send);
            txtNameSender=itemView.findViewById(R.id.txt_name_user_sender);
            txtNameReceiver=itemView.findViewById(R.id.txt_name_user_receiver);

        }
    }
}