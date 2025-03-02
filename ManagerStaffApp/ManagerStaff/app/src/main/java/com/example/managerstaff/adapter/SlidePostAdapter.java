package com.example.managerstaff.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.PostDetailActivity;
import com.example.managerstaff.models.Post;

import java.util.List;

public class SlidePostAdapter extends PagerAdapter {

    private Context Mcontext;
    private List<Post> listPosts;
    private int IdUser;

    public SlidePostAdapter(Context Mcontext, List<Post> listPosts,int IdUser) {
        this.Mcontext = Mcontext;
        this.listPosts = listPosts;
        this.IdUser=IdUser;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Post post = listPosts.get(position);

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.item_layout_post_slider,null);

        ImageView imgPost = sliderLayout.findViewById(R.id.img_post);
        TextView txtTitlePost = sliderLayout.findViewById(R.id.txt_title_post);
        TextView txtBodyPost = sliderLayout.findViewById(R.id.txt_body_post);
        TextView txtTimePost = sliderLayout.findViewById(R.id.txt_day_create_post);
        ConstraintLayout layoutItemPost=sliderLayout.findViewById(R.id.layout_item_post);
        String img="";
        if(post.getListImages().size()>0){
            img=post.getListImages().get(0).getImage();
        }

        Glide.with(Mcontext).load(img)
                .error(R.drawable.img_notify)
                .placeholder(R.drawable.img_notify)
                .into(imgPost);
        txtTitlePost.setText(post.getTypePost().getTypeName());
        txtBodyPost.setText(post.getHeaderPost());
        txtTimePost.setText(post.getTimePost());

        layoutItemPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Mcontext, PostDetailActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(Mcontext, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("position",0);
                intent.putExtra("id_post",post.getIdPost());
                Mcontext.startActivity(intent,bndlanimation);
            }
        });

        container.addView(sliderLayout);
        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return listPosts.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}