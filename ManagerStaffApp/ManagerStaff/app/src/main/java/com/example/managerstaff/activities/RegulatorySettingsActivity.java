package com.example.managerstaff.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityRegulatorySettingsBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.responses.CheckOutResponse;
import com.example.managerstaff.models.responses.SettingResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegulatorySettingsActivity extends AppCompatActivity {

    ActivityRegulatorySettingsBinding binding;

    private Setting setting;

    WebSocketClient client;
    private int IdUser,IdAdmin;
    private boolean checkHoliday=true,checkDayOff=true,CheckOverTime=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegulatorySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        clickCallApiGetSetting();

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.edtOvertime.getText().toString().length()>0 && binding.edtHoliday.getText().toString().length()>0 && binding.edtDayOff.getText().toString().length()>0) {
                    clickCallApiUpdateSetting();
                }else{
                    Toast.makeText(RegulatorySettingsActivity.this, "Yêu cầu nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.edtDayOff.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtDayOff.getText().toString().length()>0){
                    checkDayOff=true;
                    if(checkDayOff && checkHoliday && CheckOverTime){
                        setButtonLight();
                    }
                }else{
                    checkDayOff=false;
                    setButtonShadow();
                }
            }
        });

        binding.edtHoliday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtHoliday.getText().toString().length()>0){
                    checkHoliday=true;
                    if(checkDayOff && checkHoliday && CheckOverTime){
                        setButtonLight();
                    }
                }else{
                    checkHoliday=false;
                    setButtonShadow();
                }
            }
        });

        binding.edtOvertime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtOvertime.getText().toString().length()>0){
                    CheckOverTime=true;
                    if(checkDayOff && checkHoliday && CheckOverTime){
                        setButtonLight();
                    }
                }else{
                    CheckOverTime=false;
                    setButtonShadow();
                }
            }
        });

        binding.icGetTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(true);
            }
        });

        binding.icGetTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(false);
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
            Support.showNotification(RegulatorySettingsActivity.this, IdUser, IdAdmin, notificationData);
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

    private void showTimePickerDialog(boolean isStart) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_time_picker, null);
        builder.setView(dialogView);

        final TimePicker customTimePicker = dialogView.findViewById(R.id.customTimePicker);
        customTimePicker.setIs24HourView(true);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hourOfDay = customTimePicker.getHour();
                int minute = customTimePicker.getMinute();
                String selectedTime = ((hourOfDay<10)?"0":"")+hourOfDay + ":" +((minute<10)?"0":"")+ minute;
                if(isStart){
                    if(Support.compareToTime(selectedTime,binding.txtTimeOut.getText().toString())) {
                        binding.txtTimeIn.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(RegulatorySettingsActivity.this);
                    }
                }else{
                    if(Support.compareToTime(binding.txtTimeIn.getText().toString(),selectedTime)) {
                        binding.txtTimeOut.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(RegulatorySettingsActivity.this);
                    }
                }
            }
        });

        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        if(isStart) {
            customTimePicker.setHour(Integer.parseInt(setting.getTimeStart().substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(setting.getTimeStart().substring(3)));
        }else{
            customTimePicker.setHour(Integer.parseInt(setting.getTimeEnd().substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(setting.getTimeEnd().substring(3)));
        }
    }


    private void clickCallApiGetSetting() {
        ApiService.apiService.getSetting(Support.getAuthorization(RegulatorySettingsActivity.this)).enqueue(new Callback<SettingResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<SettingResponse> call, Response<SettingResponse> response) {
                SettingResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        setting=objectResponse.getSetting();
                        setting.setTimeStart(setting.getTimeStart().substring(0,5));
                        setting.setTimeEnd(setting.getTimeEnd().substring(0,5));
                        binding.edtDayOff.setText(setting.getDayOff()+"");
                        binding.edtHoliday.setText(setting.getHoliday()+"");
                        binding.edtOvertime.setText(setting.getOvertime()+"");
                        binding.txtTimeIn.setText(setting.getTimeStart());
                        binding.txtTimeOut.setText(setting.getTimeEnd());
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(RegulatorySettingsActivity.this);
                        }else{
                            Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonShadow(){
        binding.btnConfirm.setTextColor(getColor(R.color.gray));
        binding.btnConfirm.setBackgroundDrawable(getDrawable(R.drawable.button_cus_dark_shadow));
    }

    private void setButtonLight(){
        binding.btnConfirm.setTextColor(getColor(R.color.white));
        binding.btnConfirm.setBackgroundDrawable(getDrawable(R.drawable.button_cus));
    }


    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void clickCallApiUpdateSetting() {
        ApiService.apiService.updateSetting(Support.getAuthorization(RegulatorySettingsActivity.this),
                binding.txtTimeIn.getText().toString(),binding.txtTimeOut.getText().toString(),Double.parseDouble(binding.edtOvertime.getText().toString()),
                Double.parseDouble(binding.edtHoliday.getText().toString()),Double.parseDouble(binding.edtDayOff.getText().toString())).enqueue(new Callback<SettingResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<SettingResponse> call, Response<SettingResponse> response) {
                SettingResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        setting=objectResponse.getSetting();
                        Toast.makeText(RegulatorySettingsActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(RegulatorySettingsActivity.this);
                        }else{
                            Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                Toast.makeText(RegulatorySettingsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

}