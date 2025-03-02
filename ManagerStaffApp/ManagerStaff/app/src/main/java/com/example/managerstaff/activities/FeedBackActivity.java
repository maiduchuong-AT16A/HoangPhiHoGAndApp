package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityFeedBackBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackActivity extends AppCompatActivity {

    ActivityFeedBackBinding binding;
    private boolean isEnterContent;
    private WebSocketClient client;
    private int IdUser,IdAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFeedBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isEnterContent=false;
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        onEventClick();

        if(!isEnterContent){
            setButtonShadow();
        }

        binding.edtContentFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtContentFeedback.getText().toString().length()>0){
                    setButtonLight();
                    isEnterContent=true;
                }else{
                    isEnterContent=false;
                    setButtonShadow();
                }
                binding.txtCountNumberChar.setText(binding.edtContentFeedback.getText().toString().length()+"/500");
            }
        });



    }

    private void setButtonShadow(){
        binding.btnFeedback.setTextColor(getColor(R.color.gray));
        binding.btnFeedback.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonLight(){
        binding.btnFeedback.setTextColor(getColor(R.color.white));
        binding.btnFeedback.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
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
            Support.showNotification(FeedBackActivity.this, IdUser, IdAdmin, notificationData);
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

    private void onEventClick(){
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnterContent) {
                    clickCallApiSendFeedback();
                }
            }
        });
    }


    private void clickCallApiSendFeedback() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.sendFeedBack(Support.getAuthorization(this),IdUser, Support.getTimeNow(),binding.edtContentFeedback.getText().toString()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){
                        binding.edtContentFeedback.setText("");
                        isEnterContent=false;
                        setButtonShadow();
                        Toast.makeText(FeedBackActivity.this, getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(FeedBackActivity.this);
                        }else{
                            Toast.makeText(FeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(FeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(FeedBackActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}