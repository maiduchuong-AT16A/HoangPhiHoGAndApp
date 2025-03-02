package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.NotificationAdapter;
import com.example.managerstaff.adapter.UserAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityNotificationBinding;
import com.example.managerstaff.interfaces.ItemTouchHelperListener;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListNotificationResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.PaginationScrollListener;
import com.example.managerstaff.supports.RecyclerViewItemNotificationTouchHelper;
import com.example.managerstaff.supports.RecyclerViewItemUserTouchHelper;
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

public class NotificationActivity extends AppCompatActivity implements ItemTouchHelperListener {

    ActivityNotificationBinding binding;
    private WebSocketClient client;
    private int IdUser,IdAdmin;
    private NotificationAdapter notificationAdapter;
    private boolean mIsLoading,showMore;
    private boolean mIsLastPage;
    private List<NotificationData> listNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        client = new WebSocketClient();
        showMore=true;
        mIsLoading = false;
        listNotifications=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(this);
        notificationAdapter.setIdUser(IdUser);
        notificationAdapter.setIdAdmin(IdAdmin);
        notificationAdapter.setOnClickListener(position -> {
            NotificationData itemNotification=notificationAdapter.getListNotification().get(position);
            clickCallApiReadNotification(itemNotification.getIdNotify(),position);
            if(itemNotification.getTypeNotify()==1) {
                Intent resultIntent = new Intent(this, PostDetailActivity.class);
                resultIntent.putExtra("id_user", IdUser);
                resultIntent.putExtra("id_admin", IdAdmin);
                resultIntent.putExtra("action", "notification");
                resultIntent.putExtra("position", 3);
                resultIntent.putExtra("id_post", itemNotification.getIdData());
                startActivity(resultIntent);
            }else{
                if(itemNotification.getTypeNotify()==2) {
                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("id_user", IdUser);
                    resultIntent.putExtra("id_admin", IdAdmin);
                    resultIntent.putExtra("action", "main");
                    resultIntent.putExtra("id_data", itemNotification.getIdData());
                    resultIntent.putExtra("position", 1);
                    startActivity(resultIntent);
                }else{
                    if(itemNotification.getTypeNotify()==3) {
                        Intent resultIntent=new Intent(this, (IdUser==IdAdmin)? ManagerFeedBackActivity.class: FeedBackActivity.class);
                        resultIntent.putExtra("id_user",IdUser);
                        resultIntent.putExtra("id_admin",IdAdmin);
                        resultIntent.putExtra("action", "notification");
                        startActivity(resultIntent);
                    }else{
                        if(itemNotification.getTypeNotify()==4) {
                            Intent resultIntent = new Intent(this, ChatActivity.class);
                            resultIntent.putExtra("id_user",IdUser);
                            resultIntent.putExtra("id_comment",itemNotification.getIdData());
                            resultIntent.putExtra("action", "notification");
                            resultIntent.putExtra("position",3);
                            resultIntent.putExtra("id_admin",IdAdmin);
                            startActivity(resultIntent);
                        }else{
                            Intent resultIntent = new Intent(this, MainActivity.class);
                            resultIntent.putExtra("id_user", IdUser);
                            resultIntent.putExtra("id_admin", IdAdmin);
                            resultIntent.putExtra("action", "notification");
                            resultIntent.putExtra("id_data", itemNotification.getIdData());
                            resultIntent.putExtra("position", 2);
                            startActivity(resultIntent);
                        }
                    }
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this);
        binding.rcvListNotification.setLayoutManager(linearLayoutManager);
        notificationAdapter.setData(listNotifications);
        binding.rcvListNotification.setAdapter(notificationAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListNotification.addItemDecoration(itemDecoration);
        binding.rcvListNotification.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                if (showMore) {
                    mIsLoading = true;
                    binding.pbLoadShowMore.setVisibility(View.VISIBLE);
                    loadNextPage();
                }
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public void onScrolledUp() {

            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemNotificationTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvListNotification);
        clickCallApiGetNotification();

        binding.txtReadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallApiReadAllNotification();
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
            Support.showNotification(NotificationActivity.this, IdUser, IdAdmin, notificationData);
        }
        notificationAdapter.addNotificationFirst(notificationData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.closeWebSocket();
        }
        EventBus.getDefault().unregister(this);
    }

    public void clickCallApiReadNotification(int IdNotification, int position) {
        ApiService.apiService.readNotification(Support.getAuthorization(this),IdNotification).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse notificationResponse = response.body();
                if (notificationResponse != null) {
                    if(notificationResponse.getCode()==200){
                        notificationAdapter.readNotification(position);
                    }else{
                        if(notificationResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NotificationActivity.this);
                        }else{
                            Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickCallApiGetNotification() {
        ApiService.apiService.getNotificationAllDetail(Support.getAuthorization(this),IdUser,notificationAdapter.getListNotification().size(),10).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(Call<ListNotificationResponse> call, Response<ListNotificationResponse> response) {
                ListNotificationResponse notificationResponse = response.body();
                if (notificationResponse != null) {
                    if(notificationResponse.getCode()==200){
                        List<NotificationData>list = notificationResponse.getListNotificationPosts();
                        if (list.size() == 0) showMore = false;
                        if (notificationAdapter.getListNotification().size() > 0) {
                            notificationAdapter.addAll(list);
                        } else {
                            notificationAdapter.setData(list);
                        }
                        if (notificationAdapter.getListNotification().size() > 0) {
                            binding.imgNoData.setVisibility(View.GONE);
                            binding.txtNoData.setVisibility(View.GONE);
                        } else {
                            binding.imgNoData.setVisibility(View.VISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(notificationResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NotificationActivity.this);
                        }else{
                            Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListNotificationResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickCallApiReadAllNotification() {
        ApiService.apiService.readAllNotification(Support.getAuthorization(this),IdUser).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse notificationResponse = response.body();
                if (notificationResponse != null) {
                    if(notificationResponse.getCode()==200){
                        notificationAdapter.readAllNotification(notificationAdapter.getListNotification());
                    }else{
                        if(notificationResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NotificationActivity.this);
                        }else{
                            Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void clickCallApiDeleteNotification(NotificationData notificationDataDelete) {
        ApiService.apiService.deleteNotification(Support.getAuthorization(this),notificationDataDelete.getIdNotify()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        notificationAdapter.removeData(notificationDataDelete);
                        if (notificationAdapter.getListNotification().size() > 0) {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(NotificationActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    } else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NotificationActivity.this);
                        }else{
                            notificationAdapter.notifyDataSetChanged();
                            Toast.makeText(NotificationActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    notificationAdapter.notifyDataSetChanged();
                    Toast.makeText(NotificationActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                clickCallApiGetNotification();
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof NotificationAdapter.NotificationViewHolder) {

            int indexDelete = viewHolder.getAdapterPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Bạn có muốn xoá thông báo này không?");
            NotificationData notificationDataDelete = notificationAdapter.getListNotification().get(indexDelete);
            alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clickCallApiDeleteNotification(notificationDataDelete);
                }
            });
            alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    notificationAdapter.notifyDataSetChanged();
                }
            });
            alertDialog.show();

        }
    }
}