package com.example.managerstaff.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityChangePasswordBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.User;
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

public class ChangePasswordActivity extends AppCompatActivity {

    ActivityChangePasswordBinding binding;
    private int IdUser,IdAdmin;
    private String action;
    private WebSocketClient client;
    private User user;
    private boolean isShowPass=false,isShowPassNew=false,isShowPassConfirm=false,checkEmptyPassOld=false,checkEmptyPassNew=false,checkEmptyConfirm=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        action=getIntent().getStringExtra("action");
        user=new User();
        if(action.equals("forgot")){
            binding.layoutPasswordOld.setVisibility(View.GONE);
        }
        clickCallApiGetUserDetail();
        onEventClick();

    }

    private void setButtonShadow(){
        binding.btnChangePassword.setTextColor(getColor(R.color.gray));
        binding.btnChangePassword.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonLight(){
        binding.btnChangePassword.setTextColor(getColor(R.color.white));
        binding.btnChangePassword.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
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
            Support.showNotification(ChangePasswordActivity.this, IdUser, IdAdmin, notificationData);
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

        binding.icSeePassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPass==false) {
                    binding.icSeePassword1.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtPasswordOld.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPass=true;
                }else{
                    binding.edtPasswordOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword1.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPass=false;
                }
            }
        });

        binding.icSeePassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPassNew==false) {
                    binding.icSeePassword2.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtPasswordNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassNew=true;
                }else{
                    binding.edtPasswordNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword2.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPassNew=false;
                }
            }
        });

        binding.icSeePassword3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPassConfirm==false) {
                    binding.icSeePassword3.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassConfirm=true;
                }else{
                    binding.edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword3.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPassConfirm=false;
                }
            }
        });

        binding.edtPasswordOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtPasswordOld.getText().toString().length()>0){
                    checkEmptyPassOld=true;
                    if(checkEmptyConfirm && checkEmptyPassOld && checkEmptyPassNew){
                        setButtonLight();
                    }
                }else{
                    checkEmptyPassOld=false;
                    setButtonShadow();
                }
                binding.txtWarning1.setVisibility(View.GONE);
                binding.edtPasswordOld.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtTitle1.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtPasswordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.edtPasswordNew.getText().toString().trim().length() > 0) {
                    if (Support.checkPassValidate(binding.edtPasswordNew.getText().toString().trim())) {
                        checkEmptyPassNew = true;
                        if (checkEmptyConfirm &&  (checkEmptyPassOld || action.equals("forgot")) && checkEmptyPassNew) {
                            setButtonLight();
                        }
                        binding.txtWarning2.setVisibility(View.GONE);
                        binding.txtTitle2.setTextColor(getColor(R.color.black));
                        binding.edtPasswordNew.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    } else {
                        binding.txtWarning2.setVisibility(View.VISIBLE);
                        binding.txtWarning2.setText(getString(R.string.warning_password_new));
                        binding.txtTitle2.setTextColor(getColor(R.color.red));
                        binding.edtPasswordNew.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                        checkEmptyPassNew = false;
                        setButtonShadow();
                    }
                }else{
                    binding.txtWarning2.setVisibility(View.GONE);
                    binding.txtTitle2.setTextColor(getColor(R.color.black));
                    binding.edtPasswordNew.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    checkEmptyPassNew = false;
                    setButtonShadow();
                }
            }
        });

        binding.edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtConfirmPassword.getText().toString().length()>0){
                    if(binding.edtConfirmPassword.getText().toString().equals(binding.edtPasswordNew.getText().toString())) {
                        checkEmptyConfirm = true;
                        if (checkEmptyConfirm &&  (checkEmptyPassOld || action.equals("forgot")) && checkEmptyPassNew) {
                            setButtonLight();
                        }
                        binding.txtWarning3.setVisibility(View.GONE);
                        binding.txtTitle8.setTextColor(getColor(R.color.black));
                        binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    }else{
                        binding.txtWarning3.setVisibility(View.VISIBLE);
                        binding.txtWarning3.setText(getString(R.string.warning_password_confirm));
                        binding.txtTitle8.setTextColor(getColor(R.color.red));
                        binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                        checkEmptyConfirm = false;
                        setButtonShadow();
                    }
                }else{
                    binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    binding.txtWarning3.setVisibility(View.GONE);
                    binding.txtTitle8.setTextColor(getColor(R.color.black));
                    checkEmptyConfirm=false;
                    setButtonShadow();
                }
            }
        });

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(checkEmptyConfirm && (checkEmptyPassOld || action.equals("forgot")) && checkEmptyPassNew){
                    clickCallApiChangePassword();
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void clickCallApiGetUserDetail() {
        ApiService.apiService.getUserDetail(Support.getAuthorization(this),IdUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                        binding.txtFullName.setText(user.getFullName());
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChangePasswordActivity.this);
                        }else{
                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clickCallApiChangePassword() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.changePassword(Support.getAuthorization(this),IdUser,binding.edtPasswordOld.getText().toString(),binding.edtPasswordNew.getText().toString()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        if(action.equals("forgot")){
                            Intent intent=new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else {
                            finish();
                        }
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ChangePasswordActivity.this);
                        }else{
                            if(userResponse.getCode()==400 && userResponse.getMessage().equals("Wrong")) {
                                binding.txtWarning1.setVisibility(View.VISIBLE);
                                binding.edtPasswordOld.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                                binding.txtTitle1.setTextColor(getColor(R.color.red));
                            }else {
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}