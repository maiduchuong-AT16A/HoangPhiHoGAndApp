package com.example.managerstaff.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.ChangePasswordActivity;
import com.example.managerstaff.activities.PostDetailActivity;
import com.example.managerstaff.activities.ShowImageActivity;
import com.example.managerstaff.models.Image;
import com.example.managerstaff.models.Post;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.PostViewHolder> {
    private Activity mActivity;

    private List<Image> listImages;
    private int REQUEST_CODE=100;
    private String action;

    private int IdUser;

    private ImageAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(ImageAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public ImageAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setIdUser(int IdUser){
        this.IdUser=IdUser;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void updateData(String url, int index){
        this.listImages.get(index).setImage(url);
        notifyDataSetChanged();
    }

    public List<Image> getListImages() {
        return listImages;
    }

    public void setData(List<Image> listImages){
        this.listImages=listImages;
        notifyDataSetChanged();
    }

    public void removeData(Image image){
        this.listImages.remove(image);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        return new ImageAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        int p=position;
        Image image=listImages.get(position);
        if(image!=null){

            if(image.getImage().length()>0){
                Glide.with(mActivity).load(image.getImage())
                        .error(R.drawable.image_error)
                        .placeholder(R.drawable.image_error)
                        .into(holder.imgImage);
            }

            if(onClickListener!=null) {
                holder.layout_image.setOnClickListener(new View.OnClickListener() {
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
        if(listImages!=null) return listImages.size();
        return 0;
    }
    public class  PostViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout_image;
        ImageView imgImage;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_image=itemView.findViewById(R.id.layout_item_image);
            imgImage=itemView.findViewById(R.id.img_image);
        }
    }
}