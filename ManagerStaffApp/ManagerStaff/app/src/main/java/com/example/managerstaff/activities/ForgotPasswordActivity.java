package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityForgotPasswordBinding;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.Support;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;

    private boolean isFindAccount,checkDataUsername,checkDataPassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkDataUsername=false;
        checkDataPassword=false;
        isFindAccount=true;
        user=new User();
        changeLayout();

        binding.edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtWarning.setVisibility(View.GONE);
                if(binding.edtUsername.getText().toString().length()>0){
                    checkDataUsername=true;
                    setButtonNextLight();
                }else{
                    checkDataUsername=false;
                    setButtonNextShadow();
                }
            }
        });

        binding.txtSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallFindAccount();
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.edtConfirmCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtWarningCode.setVisibility(View.GONE);
                if(binding.edtConfirmCode.getText().toString().length()>0){
                    checkDataPassword=true;
                    setButtonConfirmLight();
                }else{
                    checkDataPassword=false;
                    setButtonConfirmShadow();
                }
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkDataUsername){
                    clickCallFindAccount();
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFindAccount=true;
                changeLayout();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataPassword) {
                    clickCallConfirmCode();
                }
            }
        });

    }

    private void changeLayout(){
        if(isFindAccount){
            binding.edtConfirmCode.setText("");
            binding.layoutFindEmail.setVisibility(View.VISIBLE);
            binding.layoutConfirmPassword.setVisibility(View.GONE);
        }else{
            binding.layoutFindEmail.setVisibility(View.GONE);
            binding.layoutConfirmPassword.setVisibility(View.VISIBLE);
        }
    }

    private void clickCallFindAccount() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.findUser(Support.getAuthorization(this),binding.edtUsername.getText().toString()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==201){
                        user=userResponse.getUser();
                        isFindAccount=false;
                        changeLayout();
                    }else{
                        binding.txtWarning.setVisibility(View.VISIBLE);
                    }
                }else{
                    binding.txtWarning.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallConfirmCode() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.confirmCode(Support.getAuthorization(this),user.getIdUser(),binding.edtConfirmCode.getText().toString()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        Intent intent=new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ForgotPasswordActivity.this, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                        intent.putExtra("id_user",user.getIdUser());
                        intent.putExtra("action","forgot");
                        startActivity(intent,bndlanimation);
                    }else{
                        binding.txtWarningCode.setVisibility(View.VISIBLE);
                    }
                }else{
                    binding.txtWarningCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void setButtonNextShadow(){
        binding.btnNext.setTextColor(getColor(R.color.gray));
        binding.btnNext.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonNextLight(){
        binding.btnNext.setTextColor(getColor(R.color.white));
        binding.btnNext.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
    }

    private void setButtonConfirmShadow(){
        binding.btnConfirm.setTextColor(getColor(R.color.gray));
        binding.btnConfirm.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonConfirmLight(){
        binding.btnConfirm.setTextColor(getColor(R.color.white));
        binding.btnConfirm.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}