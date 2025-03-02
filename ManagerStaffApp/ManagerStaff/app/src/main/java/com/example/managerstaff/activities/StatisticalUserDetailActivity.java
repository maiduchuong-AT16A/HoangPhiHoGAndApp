package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.StatisticalTimeKeepingUserAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityStatisticalUserDetailBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.StatisticalTimeKeeping;
import com.example.managerstaff.models.responses.ListStatisticalTimekeepingResponse;
import com.example.managerstaff.models.responses.StatisticalTimeKeepingResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticalUserDetailActivity extends AppCompatActivity {

    ActivityStatisticalUserDetailBinding binding;
    private int idUser,idAdmin,IdUser;
    private String timeStart,timeEnd;
    private StatisticalTimeKeeping statisticalTimeKeeping;
    private StatisticalTimeKeepingUserAdapter adapter;
    private WebSocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStatisticalUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        idUser = getIntent().getIntExtra("id_user", 0);
        IdUser = getIntent().getIntExtra("id_user_now", 0);
        idAdmin = getIntent().getIntExtra("id_admin", 0);
        timeStart = getIntent().getStringExtra("time_start");
        timeEnd = getIntent().getStringExtra("time_end");
        client = new WebSocketClient();
        statisticalTimeKeeping=new StatisticalTimeKeeping();
        adapter=new StatisticalTimeKeepingUserAdapter(this);
        adapter.setData(new ArrayList<>());;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatisticalUserDetailActivity.this);
        binding.rcvListStatistical.setLayoutManager(linearLayoutManager);
        binding.rcvListStatistical.setAdapter(adapter);
        clickCallApiGetStatistical();
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void clickCallApiGetStatistical() {
        ApiService.apiService.getListStatisticOfUser(Support.getAuthorization(this),idUser,timeStart,timeEnd).enqueue(new Callback<StatisticalTimeKeepingResponse>() {
            @Override
            public void onResponse(Call<StatisticalTimeKeepingResponse> call, Response<StatisticalTimeKeepingResponse> response) {
                StatisticalTimeKeepingResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        statisticalTimeKeeping=objectResponse.getStatisticalTimeKeeping();
                        adapter.setData(statisticalTimeKeeping.getListWorkdays());
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(StatisticalUserDetailActivity.this);
                        }
                    }
                } else {
                    Toast.makeText(StatisticalUserDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatisticalTimeKeepingResponse> call, Throwable t) {
                Toast.makeText(StatisticalUserDetailActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
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
            Support.showNotification(StatisticalUserDetailActivity.this, IdUser, idAdmin, notificationData);
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
}