package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.ChatAdapter;
import com.example.managerstaff.adapter.PostAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityListChatBinding;
import com.example.managerstaff.interfaces.ItemTouchHelperListener;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.responses.ListPostResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.RecyclerViewItemChatTouchHelper;
import com.example.managerstaff.supports.RecyclerViewItemPostTouchHelper;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListChatActivity extends AppCompatActivity implements ItemTouchHelperListener {

    ActivityListChatBinding binding;
    private int IdUser;
    private int IdAdmin;
    private List<Post> listPosts;
    private WebSocketClient client;
    private int position;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        position = getIntent().getIntExtra("position", -1);
        client = new WebSocketClient();
        listPosts = new ArrayList<>();
        chatAdapter = new ChatAdapter(this);
        chatAdapter.setIdUser(IdUser);
        chatAdapter.setIdAdmin(IdAdmin);
        chatAdapter.setP(position);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListChatActivity.this);
        binding.rcvListComment.setLayoutManager(linearLayoutManager);
        clickCallApiGetListPosts();
        chatAdapter.setData(listPosts);
        binding.rcvListComment.setAdapter(chatAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListComment.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemChatTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvListComment);

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clickCallApiGetListPosts();
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListChatActivity.this, MainActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ListChatActivity.this, R.anim.slide_in_left,R.anim.slide_out_right).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin", IdAdmin);
                intent.putExtra("action", "main");
                intent.putExtra("position", position);
                startActivity(intent,bndlanimation);
            }
        });
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
            Support.showNotification(ListChatActivity.this, IdUser, IdAdmin, notificationData);
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

    private void clickCallApiGetListPosts() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllPostCmt(Support.getAuthorization(this)).enqueue(new Callback<ListPostResponse>() {
            @Override
            public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                ListPostResponse listPostResponse = response.body();
                if (listPostResponse != null) {
                    if (listPostResponse.getCode() == 200) {
                        List<Post> list = listPostResponse.getListPosts();
                        listPosts.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if ((IdUser == IdAdmin && list.get(i).getListComments().size() > 0) || (Support.checkCommentOfUser(list.get(i), IdUser))) {
                                listPosts.add(list.get(i));
                            }
                        }
                        listPosts = Support.searchListPosts(listPosts, binding.edtSearch.getText().toString());
                        if (listPosts.size() == 0) {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        } else {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        }
                        chatAdapter.setData(listPosts);
                    } else{
                        if(listPostResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ListChatActivity.this);
                        }else{
                            Toast.makeText(ListChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ListChatActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListPostResponse> call, Throwable t) {
                Toast.makeText(ListChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clickCallApiGetListPosts();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ChatAdapter.ChatViewHolder) {

            int indexDelete = viewHolder.getAdapterPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Bạn có muốn xoá hội thoại này không?");
            Post postDelete = listPosts.get(indexDelete);
            alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clickCallApiDeleteAllCommentUserInPost(postDelete);
                }
            });
            alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    chatAdapter.setData(listPosts);
                }
            });
            alertDialog.show();

        }
    }

    private void clickCallApiDeleteAllCommentUserInPost(Post postDelete) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deleteAllCommentUserInPost(Support.getAuthorization(this), IdUser, postDelete.getIdPost()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        chatAdapter.removeData(postDelete);
                        Toast.makeText(ListChatActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    } else {
                        if (objectResponse.getCode() == 401) {
                            Support.showDialogWarningExpiredAu(ListChatActivity.this);
                        } else {
                            chatAdapter.setData(listPosts);
                            Toast.makeText(ListChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    chatAdapter.setData(listPosts);
                    Toast.makeText(ListChatActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                chatAdapter.setData(listPosts);
                Toast.makeText(ListChatActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }
}