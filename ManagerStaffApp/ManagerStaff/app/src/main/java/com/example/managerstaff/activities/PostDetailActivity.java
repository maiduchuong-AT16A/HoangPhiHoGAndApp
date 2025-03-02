package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.adapter.CommentAdapter;
import com.example.managerstaff.adapter.ImageAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityMainBinding;
import com.example.managerstaff.databinding.ActivityPostDetailBinding;
import com.example.managerstaff.models.Comment;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.CommentResponse;
import com.example.managerstaff.models.responses.PostResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    ActivityPostDetailBinding binding;
    private int IdUser;
    private int IdPost,IdAdmin;
    private Post post;
    private String action;
    private User user,userAdmin;
    private ImageAdapter adapter;
    private boolean isEnterContent;
    private WebSocketClient client;
    private boolean isChange;
    private int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        IdPost = getIntent().getIntExtra("id_post", 0);
        action = getIntent().getStringExtra("action");
        client = new WebSocketClient();
        userAdmin=new User();
        post=new Post();
        user=new User();
        isChange=false;
        isEnterContent=false;
        adapter=new ImageAdapter(this);
        adapter.setAction("show");
        adapter.setOnClickListener(position -> {
            Intent intent=new Intent(this, ShowImageActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("action","show");
            intent.putExtra("id_user",IdUser);
            intent.putExtra("id_admin",IdAdmin);
            intent.putExtra("uri_avatar",post.getListImages().get(position).getImage());
            startActivityForResult(intent,REQUEST_CODE);
        });
        adapter.setIdUser(IdUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvListImage.setLayoutManager(linearLayoutManager);
        onEventClick();
        clickCallApiGetPostDetail();
        clickCallApiGetUserDetail();

        binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PostDetailActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin", IdAdmin);
                intent.putExtra("id_post", IdPost);
                intent.putExtra("action", "edit");
                startActivity(intent, bndlanimation);
            }
        });

        if(IdUser==IdAdmin){
            binding.txtEdit.setVisibility(View.VISIBLE);
        }else{
            binding.txtEdit.setVisibility(View.GONE);
        }
    }

    public void onStart() {
        super.onStart();
        client.startWebSocket();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        NotificationData notificationData = event.notificationData;
        if(notificationData.getIdUser()==IdUser) {
            Support.showNotification(PostDetailActivity.this, IdUser, IdAdmin, notificationData);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.closeWebSocket();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clickCallApiGetPostDetail();
        clickCallApiGetUserDetail();
    }

    private void onEventClick(){
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action!=null && action.equals("notification")){
                    Intent intent=new Intent(PostDetailActivity.this, NewsActivity.class);
                    intent.putExtra("id_user",IdUser);
                    intent.putExtra("position",3);
                    intent.putExtra("id_admin",IdAdmin);
                    startActivity(intent);
                    finish();
                }else {
                    Intent returnIntent = new Intent(PostDetailActivity.this, NewsActivity.class);
                    returnIntent.putExtra("id_post", IdPost);
                    returnIntent.putExtra("image", (post.getListImages().size() > 0) ? post.getListImages().get(0).getImage() : "");
                    returnIntent.putExtra("header_post", post.getHeaderPost());
                    returnIntent.putExtra("id_type_post", post.getTypePost().getIdType());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        binding.cvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostDetailActivity.this, ShowImageActivity.class);
                intent.putExtra("position",0);
                intent.putExtra("action","show");
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("uri_avatar",userAdmin.getAvatar());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        binding.imgQuestionAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostDetailActivity.this, ChatActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PostDetailActivity.this, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",userAdmin.getIdUser());
                intent.putExtra("id_post",IdPost);
                intent.putExtra("action", "list_comment");
                startActivity(intent,bndlanimation);
            }
        });

    }



    private void clickCallApiGetUserDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetail(Support.getAuthorization(this),IdUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(PostDetailActivity.this);
                        }else{
                            Toast.makeText(PostDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetPostDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getPostDetail(Support.getAuthorization(this),IdPost).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                if (postResponse != null) {
                    if(postResponse.getCode()==200){
                        post=postResponse.getPost();
                        binding.txtTypePost.setText(post.getTypePost().getTypeName());
                        binding.txtHeaderPost.setText(post.getHeaderPost());
                        binding.txtTimeCreatePost.setText(post.getTimePost());
                        binding.txtBodyPost.setText(post.getContent());
                        adapter.setData(post.getListImages());
                        binding.rcvListImage.setAdapter(adapter);
                        userAdmin=post.getUser();
                        binding.txtNameUser.setText(userAdmin.getFullName());
                        if(userAdmin.getAvatar().length()>0){
                            Glide.with(PostDetailActivity.this).load(userAdmin.getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
                        }
                    }else{
                        if(postResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(PostDetailActivity.this);
                        }else{
                            Toast.makeText(PostDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this,getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}