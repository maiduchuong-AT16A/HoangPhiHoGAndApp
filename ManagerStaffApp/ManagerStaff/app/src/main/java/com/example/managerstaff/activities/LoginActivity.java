package com.example.managerstaff.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityLoginBinding;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.Support;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private boolean isShowPass=false,checkUsername=false,checkPassword=false;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=new Database(this);
        clickCallApiCheckToken();
        if(binding.edtPassword.getText().toString().length()>0){
            checkPassword=true;
        }

        if(binding.edtUsername.getText().toString().length()>0){
            checkUsername=true;
        }

        if(checkUsername && checkPassword){
            setButtonLight();
        }else{
            setButtonShadow();
        }

        binding.icSeePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPass==false) {
                    binding.icSeePassword.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPass=true;
                }else{
                    binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPass=false;
                }
            }
        });

        binding.edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtUsername.setTextColor(getColor(R.color.black));
                binding.edtUsername.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtWarning.setVisibility(View.GONE);
                if(binding.edtUsername.getText().toString().length()>0){
                    checkUsername=true;
                    if(checkUsername && checkPassword){
                        setButtonLight();
                    }
                }else{
                    checkUsername=false;
                    setButtonShadow();
                }
            }
        });

        binding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtPassword.setTextColor(getColor(R.color.black));
                binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtWarning1.setVisibility(View.GONE);
                if(binding.edtPassword.getText().toString().length()>0){
                    checkPassword=true;
                    if(checkPassword && checkUsername){
                        setButtonLight();
                    }
                }else{
                    checkPassword=false;
                    setButtonShadow();
                }
            }
        });

        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                startActivity(intent,bndlanimation);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(checkPassword && checkUsername) {
                    setDetailEditText();
                    if (!binding.edtUsername.getText().toString().equals("") && !binding.edtPassword.getText().toString().equals("")) {
                        clickCallApiLogin(binding.edtUsername.getText().toString(), binding.edtPassword.getText().toString());
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.username_or_password_not_empty, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void setDetailEditText(){
        binding.edtUsername.setTextColor(getColor(R.color.black));
        binding.edtUsername.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
        binding.txtUsername.setTextColor(getColor(R.color.black));
        binding.edtPassword.setTextColor(getColor(R.color.black));
        binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
        binding.txtPassword.setTextColor(getColor(R.color.black));
    }

    private void setButtonShadow(){
        binding.btnLogin.setTextColor(getColor(R.color.gray));
        binding.btnLogin.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonLight(){
        binding.btnLogin.setTextColor(getColor(R.color.white));
        binding.btnLogin.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
    }

    private void clickCallApiCheckToken() {
        String data= database.checkSessionAuthorization();
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.checkToken(data).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        if(objectResponse.getMessage().equals("Success")) {
                            int IdAdmin=(Integer.parseInt(objectResponse.getData().split(" ")[1])==1)?Integer.parseInt(objectResponse.getData().split(" ")[0]):0;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id_user", Integer.parseInt(objectResponse.getData().split(" ")[0]));
                            intent.putExtra("id_admin", IdAdmin);
                            intent.putExtra("action", "main");
                            intent.putExtra("position", 0);
                            startActivity(intent);
                            finish();
                        }else{
                            database.deleteSession(data);
                        }
                    }else{
                        database.deleteSession(data);
                    }
                }else{
                    database.deleteSession(data);
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clickCallApiLogin(String username, String password) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.loginUser(username, password).enqueue(new Callback<ObjectResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        Log.d("REQUEST_OK","OK_RESPONE");
                        int IdAdmin=Integer.parseInt(objectResponse.getData().split(" ")[1]);
                        database.insertAuthorization(objectResponse.getData().split(" ")[2],(binding.cbRememberMe.isChecked()==true)?1:0);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id_user", Integer.parseInt(objectResponse.getData().split(" ")[0]));
                        intent.putExtra("id_admin", IdAdmin);
                        intent.putExtra("action", "main");
                        intent.putExtra("position", 0);
                        binding.pbLoadData.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    } else {
                        if (objectResponse.getMessage().equals(getString(R.string.not_exist))) {
                            binding.txtUsername.setTextColor(getColor(R.color.red));
                            binding.edtUsername.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                            binding.txtWarning.setVisibility(View.VISIBLE);
                            binding.txtWarning.setText(getString(R.string.not_exist_account));
                        } else {
                            binding.txtPassword.setTextColor(getColor(R.color.red));
                            binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                            binding.txtWarning1.setVisibility(View.VISIBLE);
                            binding.txtWarning1.setText(getString(R.string.wrong_password));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }
}