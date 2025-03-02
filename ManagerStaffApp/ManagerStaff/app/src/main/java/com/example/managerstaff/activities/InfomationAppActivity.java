package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.managerstaff.R;
import com.example.managerstaff.databinding.ActivityInfomationAppBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class InfomationAppActivity extends AppCompatActivity {

    ActivityInfomationAppBinding binding;
    private WebSocketClient client;
    private int IdUser,IdAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfomationAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);

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
            Support.showNotification(InfomationAppActivity.this, IdUser, IdAdmin, notificationData);
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

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}