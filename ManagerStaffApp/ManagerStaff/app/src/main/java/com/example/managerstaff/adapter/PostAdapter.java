package com.example.managerstaff.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Activity mActivity;

    private List<Post> listPosts;

    private int IdUser,IdAdmin;

    private PostAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(PostAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public PostAdapter(Activity mActivity) {

        this.mActivity = mActivity;
    }

    public void setIdUser(int IdUser){
        this.IdUser=IdUser;
    }

    public void setIdAdmin(int idAdmin) {
        IdAdmin = idAdmin;
    }

    public List<Post> getListPosts() {
        return listPosts;
    }

    public void setData(List<Post> listPosts){
        this.listPosts=listPosts;
        notifyDataSetChanged();
    }

    public void removeData(Post post){
        this.listPosts.remove(post);
        notifyDataSetChanged();
    }

    public void addAllData(List<Post> listPosts){
        this.listPosts.addAll(listPosts);
        notifyDataSetChanged();
    }

    public void resetData(){
        this.listPosts=new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_post_slider,parent,false);
        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post=listPosts.get(position);
        int p=position;
        if(post!=null){
            String img="";
            if(post.getListImages().size()>0){
                img=post.getListImages().get(0).getImage();
            }
            Glide.with(mActivity).load(img)
                    .error(R.drawable.img_notify)
                    .placeholder(R.drawable.img_notify)
                    .into(holder.imgPost);
            holder.txtTitlePost.setText(post.getTypePost().getTypeName());
            holder.txtBodyPost.setText(post.getHeaderPost());
            holder.txtTimePost.setText(post.getTimePost());

            if(onClickListener!=null) {
                holder.layoutItemPost.setOnClickListener(new View.OnClickListener() {
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
        if(listPosts!=null) return listPosts.size();
        return 0;
    }
    public class  PostViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitlePost,txtBodyPost,txtTimePost;
        private ImageView imgPost;
        private CardView layoutItemPost;
        public View layoutItem;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.img_post);
            txtTitlePost = itemView.findViewById(R.id.txt_title_post);
            txtBodyPost = itemView.findViewById(R.id.txt_body_post);
            txtTimePost = itemView.findViewById(R.id.txt_day_create_post);
            layoutItemPost=itemView.findViewById(R.id.cv_item_post);
            layoutItem=itemView.findViewById(R.id.layout_item_post);
        }
    }
}