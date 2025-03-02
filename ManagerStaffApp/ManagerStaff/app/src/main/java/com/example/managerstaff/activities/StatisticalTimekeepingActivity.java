package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.StatisticalTimeKeepingAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityStatisticalTimekeepingBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.StatisticalTimeKeeping;
import com.example.managerstaff.models.responses.ListStatisticalTimekeepingResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticalTimekeepingActivity extends AppCompatActivity {

    ActivityStatisticalTimekeepingBinding binding;
    private List<StatisticalTimeKeeping> listStatisticals;

    private StatisticalTimeKeepingAdapter adapter;
    private int IdUser, IdAdmin;
    private WebSocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStatisticalTimekeepingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listStatisticals=new ArrayList<>();
        adapter=new StatisticalTimeKeepingAdapter(this);
        adapter.setData(new ArrayList<>());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatisticalTimekeepingActivity.this);
        binding.rcvListStatistical.setLayoutManager(linearLayoutManager);
        binding.rcvListStatistical.setAdapter(adapter);
        binding.txtTimeStart.setText(Support.changeReverDateTime(Support.getDayNow(),false));
        binding.txtTimeEnd.setText(Support.changeReverDateTime(Support.getDayNow(),false));

        clickCallApiGetListStatistical();

        binding.icGetTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StatisticalTimekeepingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                String timeS = Support.getFormatString(day, month + 1, year);

                                if (Support.compareToDate(timeS, binding.txtTimeEnd.getText().toString()) <= 0) {
                                    selectedDate.set(Calendar.YEAR, year);
                                    selectedDate.set(Calendar.MONTH, month);
                                    selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                    binding.txtTimeStart.setText(timeS);
                                    clickCallApiGetListStatistical();
                                } else {
                                    Support.showDialogWarningSetTimeDay(StatisticalTimekeepingActivity.this);
                                }


                            }
                        },
                        Integer.parseInt(binding.txtTimeStart.getText().toString().substring(6)),
                        Integer.parseInt(binding.txtTimeStart.getText().toString().substring(3, 5)) - 1,
                        Integer.parseInt(binding.txtTimeStart.getText().toString().substring(0, 2))
                );
                datePickerDialog.show();
            }
        });

        binding.icGetTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StatisticalTimekeepingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String timeE = Support.getFormatString(day, month + 1, year);

                                if (Support.compareToDate(timeE, binding.txtTimeStart.getText().toString()) >= 0 && Support.compareToDate(timeE,Support.changeReverDateTime(Support.getDayNow(),false))<=0) {

                                    selectedDate.set(Calendar.YEAR, year);
                                    selectedDate.set(Calendar.MONTH, month);
                                    selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                    binding.txtTimeEnd.setText(timeE);
                                    clickCallApiGetListStatistical();
                                } else {
                                    Support.showDialogWarningSetTimeDay(StatisticalTimekeepingActivity.this);
                                }
                            }
                        },
                        Integer.parseInt(binding.txtTimeEnd.getText().toString().substring(6)),
                        Integer.parseInt(binding.txtTimeEnd.getText().toString().substring(3, 5)) - 1,
                        Integer.parseInt(binding.txtTimeEnd.getText().toString().substring(0, 2))
                );
                datePickerDialog.show();
            }
        });

        adapter.setOnClickListener(position -> {
            Intent intent = new Intent(this, StatisticalUserDetailActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(StatisticalTimekeepingActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            intent.putExtra("id_user", listStatisticals.get(position).getUser().getIdUser());
            intent.putExtra("id_admin", IdAdmin);
            intent.putExtra("id_user_now", IdUser);
            intent.putExtra("time_start", binding.txtTimeStart.getText().toString());
            intent.putExtra("time_end", binding.txtTimeEnd.getText().toString());
            startActivity(intent, bndlanimation);
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
            Support.showNotification(StatisticalTimekeepingActivity.this, IdUser, IdAdmin, notificationData);
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


    private void clickCallApiGetListStatistical() {
        ApiService.apiService.getListStatistic(Support.getAuthorization(this),binding.txtTimeStart.getText().toString(),binding.txtTimeEnd.getText().toString()).enqueue(new Callback<ListStatisticalTimekeepingResponse>() {
            @Override
            public void onResponse(Call<ListStatisticalTimekeepingResponse> call, Response<ListStatisticalTimekeepingResponse> response) {
                ListStatisticalTimekeepingResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        listStatisticals=objectResponse.getListStatistic();
                        adapter.setData(listStatisticals);
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(StatisticalTimekeepingActivity.this);
                        }
                    }
                } else {
                    Toast.makeText(StatisticalTimekeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListStatisticalTimekeepingResponse> call, Throwable t) {
                Toast.makeText(StatisticalTimekeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}