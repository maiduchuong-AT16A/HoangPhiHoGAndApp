package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.FeedbackAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityManagerFeedBackBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.StaffFeedBack;
import com.example.managerstaff.models.responses.ListFeedbackResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.PaginationScrollListener;
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

public class ManagerFeedBackActivity extends AppCompatActivity {

    ActivityManagerFeedBackBinding binding;
    private List<StaffFeedBack> listFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private String action;
    private boolean mIsLoading;
    private boolean mIsLastPage,showMore;
    private int IdUser,IdAdmin;
    private WebSocketClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerFeedBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        action = getIntent().getStringExtra("action");
        client = new WebSocketClient();
        listFeedbacks = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(this);
        showMore=true;
        feedbackAdapter.setData(listFeedbacks);
        feedbackAdapter.setOnClickListener(position -> {
            StaffFeedBack staffFeedBackDelete = feedbackAdapter.getListFeedbacks().get(position);
            showDialogDeleteFeedback(staffFeedBackDelete);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvListFeedback.setLayoutManager(linearLayoutManager);
        binding.rcvListFeedback.setAdapter(feedbackAdapter);
        binding.rcvListFeedback.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        clickCallApiGetAllFeedback();
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action.equals("notification")){
                    Intent intent = new Intent(ManagerFeedBackActivity.this, MainActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ManagerFeedBackActivity.this, R.anim.slide_in_left,R.anim.slide_out_right).toBundle();
                    intent.putExtra("id_user", IdUser);
                    intent.putExtra("id_admin", IdAdmin);
                    intent.putExtra("action", "main");
                    intent.putExtra("position", 3);
                    startActivity(intent,bndlanimation);
                }else {
                    finish();
                }
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
            Support.showNotification(ManagerFeedBackActivity.this, IdUser, IdAdmin, notificationData);
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

    public void showDialogDeleteFeedback(StaffFeedBack staffFeedBackDelete) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Bạn có muốn xoá bản tin này không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickCallApiDeleteFeedback(staffFeedBackDelete);
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    private void clickCallApiGetAllFeedback() {
        if(feedbackAdapter.getListFeedbacks().size()==0)
            binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllFeedBack(Support.getAuthorization(this),feedbackAdapter.getListFeedbacks().size(), 16).enqueue(new Callback<ListFeedbackResponse>() {
            @Override
            public void onResponse(Call<ListFeedbackResponse> call, Response<ListFeedbackResponse> response) {
                ListFeedbackResponse listFeedbackResponse = response.body();
                if (listFeedbackResponse != null) {
                    if(listFeedbackResponse.getCode()==200) {
                        List<StaffFeedBack> list = listFeedbackResponse.getListFeedbacks();
                        if (list.size() == 0) showMore = false;
                        if (feedbackAdapter.getListFeedbacks().size() > 0) {
                            feedbackAdapter.addAllData(list);
                        } else {
                            feedbackAdapter.setData(list);
                        }

                        if (feedbackAdapter.getListFeedbacks().size() > 0) {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(listFeedbackResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ManagerFeedBackActivity.this);
                        }else{
                            Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListFeedbackResponse> call, Throwable t) {
                Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiDeleteFeedback(StaffFeedBack staffFeedBackDelete) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deleteFeedback(Support.getAuthorization(this), staffFeedBackDelete.getIdFeedback()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        feedbackAdapter.removeData(staffFeedBackDelete);
                        if (feedbackAdapter.getListFeedbacks().size() > 0) {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ManagerFeedBackActivity.this);
                        }else{
                            Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(ManagerFeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
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
                clickCallApiGetAllFeedback();
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}