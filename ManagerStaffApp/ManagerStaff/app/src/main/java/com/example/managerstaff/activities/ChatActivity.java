package com.example.managerstaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.CommentAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityChatBinding;
import com.example.managerstaff.models.Comment;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.CommentResponse;
import com.example.managerstaff.models.responses.ListCommentResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PostResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    private List<Comment> listComments;
    private User user, admin;
    private Post post;
    private int IdUser,IdAdmin,IdPost,IdComment;
    private CommentAdapter commentAdapter;
    private boolean isEnterContent,showMore;
    private boolean mIsLoading;
    private WebSocketClient client;
    private String action;
    private int position;
    private boolean mIsLastPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 4);
        IdPost = getIntent().getIntExtra("id_post", 0);
        IdComment = getIntent().getIntExtra("id_comment", 0);
        action = getIntent().getStringExtra("action");
        position = getIntent().getIntExtra("position", -1);
        clickCallApiReadComment();
        user=new User();
        post=new Post();
        admin=new User();
        showMore=true;
        isEnterContent=false;
        commentAdapter=new CommentAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.rcvListComment.setLayoutManager(layoutManager);
        listComments=new ArrayList<>();
        if(IdPost>0) {
            clickCallApiGetUserDetail(IdUser, 0,IdPost);
            clickCallApiGetUserDetail(IdAdmin, 1,IdPost);
            clickCallApiGetPostDetail(IdPost);
        }else{
            clickCallApiGetCommentDetail();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        commentAdapter.setData(listComments);
        binding.rcvListComment.setAdapter(commentAdapter);
        commentAdapter.setOnClickListener(position -> {
            Comment commentDelete=commentAdapter.getmListComment().get(position);
            showDialog(commentDelete);
        });

        binding.rcvListComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition == 0) {
                        if (showMore) {
                            mIsLoading = true;
                            binding.pbLoadShowMore.setVisibility(View.VISIBLE);
                            loadNextPage();
                        }
                    }
                }
            }
        });

        binding.edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtComment.getText().toString().length()>0){
                    isEnterContent=true;
                    binding.send.setImageDrawable(getDrawable(R.drawable.icon_send_blue));
                }else{
                    binding.send.setImageDrawable(getDrawable(R.drawable.icon_send_gray));
                    isEnterContent=false;
                }
            }
        });

        binding.btnShowPostDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this, PostDetailActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ChatActivity.this, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_post",IdPost);
                startActivity(intent,bndlanimation);
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action.equals("notification")){
                    Intent intent=new Intent(ChatActivity.this, ListChatActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ChatActivity.this, R.anim.slide_in_left,R.anim.slide_out_right).toBundle();
                    intent.putExtra("id_user",IdUser);
                    intent.putExtra("id_admin",IdAdmin);
                    intent.putExtra("position",position);
                    startActivity(intent,bndlanimation);
                }else {
                    finish();
                }
            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnterContent){
                    clickCallApiAddComment();
                }
            }
        });

        CountDownLatch latch = new CountDownLatch(10);
        Thread workerThread = new Thread(() -> {
            try {

                latch.await();
                int lastItemPosition = listComments.size() - 1;
                binding.rcvListComment.smoothScrollToPosition(lastItemPosition);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        workerThread.start();
        latch.countDown();

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
            if(notificationData.getTypeNotify()==4){
                clickCallApiUpdateChat(notificationData.getIdData());
            }
            Support.showNotification(ChatActivity.this, IdUser, IdAdmin, notificationData);
        }

    }

    private void clickCallApiReadComment() {
        ApiService.apiService.readComment(Support.getAuthorization(ChatActivity.this),IdUser,IdPost).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.closeWebSocket();
        }
        EventBus.getDefault().unregister(this);
    }

    private void clickCallApiUpdateChat(int IdCmt) {
        ApiService.apiService.getCommentDetail(Support.getAuthorization(this),IdCmt).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (commentResponse != null) {
                    if(commentResponse.getCode()==200){
                        Comment comment =commentResponse.getComment();
                        if(comment.getIdPost()==IdPost) {
                            commentAdapter.addComment(comment);
                            binding.rcvListComment.smoothScrollToPosition(0);
                        }
                    }else{
                        if(commentResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickCallApiGetCommentDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getCommentDetail(Support.getAuthorization(this),IdComment).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (commentResponse != null) {
                    if(commentResponse.getCode()==200){
                        IdPost=commentResponse.getComment().getIdPost();
                        clickCallApiGetUserDetail(IdUser, 0,IdPost);
                        clickCallApiGetUserDetail(IdAdmin, 1,IdPost);
                        clickCallApiGetPostDetail(IdPost);
                    }else{
                        if(commentResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetUserDetail(int id, int isAdmin,int IdPost) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetail(Support.getAuthorization(this),id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        if(isAdmin==0){
                            user=userResponse.getUser();
                            commentAdapter.setUser(user);
                        }else{
                            admin=userResponse.getUser();
                            commentAdapter.setUserAdmin(admin);
                            clickCallApiGetAllComment(IdPost);
                        }
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void showDialog(Comment commentDelete){
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setMessage("Bạn có muốn xoá tin nhắn này không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickCallApiDeleteComment(commentDelete);
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    private void clickCallApiDeleteComment(Comment commentDelete) {
        ApiService.apiService.deleteComment(Support.getAuthorization(this),commentDelete.getIdComment()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        commentAdapter.removeData(commentDelete);
                        Toast.makeText(ChatActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickCallApiAddComment() {
        ApiService.apiService.addComment(Support.getAuthorization(this),IdUser,IdPost, Support.getTimeNow(),binding.edtComment.getText().toString()).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (commentResponse != null) {
                    if(commentResponse.getCode()==201){
                        binding.edtComment.setText("");
                        commentAdapter.addComment(commentResponse.getComment());
                    }else{
                        if(commentResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickCallApiGetPostDetail(int IdPost) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getPostDetail(Support.getAuthorization(this),IdPost).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                if (postResponse != null) {
                    if(postResponse.getCode()==200){
                        post=postResponse.getPost();
                        binding.txtHeaderPost.setText(post.getHeaderPost());
                        binding.txtTypePost.setText(post.getTypePost().getTypeName());
                    }else{
                        if(postResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this,getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllComment(int IdPost) {
        if(commentAdapter.getmListComment().size()==0)
            binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllComment(Support.getAuthorization(this),IdPost,commentAdapter.getmListComment().size(),20).enqueue(new Callback<ListCommentResponse>() {
            @Override
            public void onResponse(Call<ListCommentResponse> call, Response<ListCommentResponse> response) {
                ListCommentResponse listCommentResponse = response.body();
                if (listCommentResponse != null) {
                    if(listCommentResponse.getCode()==200){
                        if(listCommentResponse.getListComments().size()==0) showMore=false;
                        if(commentAdapter.getmListComment().size()>0){
                            commentAdapter.addAllData(listCommentResponse.getListComments());
                        }else{
                            commentAdapter.setData(listCommentResponse.getListComments());
                            binding.rcvListComment.smoothScrollToPosition(0);
                        }
                    }else{
                        if(listCommentResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChatActivity.this);
                        }else{
                            Toast.makeText(ChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListCommentResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this,getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                clickCallApiGetAllComment(IdPost);
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}