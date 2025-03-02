package com.example.managerstaff.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.TimeKeepingAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityTimeKeepingBinding;
import com.example.managerstaff.fragments.TimeKeepingDetailFragment;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.PaySlip;
import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.StatisticalTimeUser;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PaySlipResponse;
import com.example.managerstaff.models.responses.SettingResponse;
import com.example.managerstaff.models.responses.UserResponse;
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

public class TimeKeepingActivity extends AppCompatActivity {

    ActivityTimeKeepingBinding binding;
    private int month = 0;
    private int year = 0;
    private int IdUser,IdAdmin,IdUserWatch;
    private PaySlip paySlip;
    private WebSocketClient client;
    private TimeKeepingAdapter adapter;
    private List<StatisticalTimeUser> statisticalTimeUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeKeepingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        IdUserWatch = getIntent().getIntExtra("id_user_watch", 0);
        client = new WebSocketClient();
        paySlip=new PaySlip();
        statisticalTimeUserList=new ArrayList<>();
        adapter = new TimeKeepingAdapter(this);
        adapter.setOnClickListener(position -> {
            Fragment belowFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (belowFragment == null || !belowFragment.isVisible()) {
                binding.fragmentContainer.setVisibility(View.VISIBLE);
                TimeKeepingDetailFragment fragment = new TimeKeepingDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id_user", this.IdUser);
                bundle.putInt("id_user_watch", this.IdUserWatch);
                bundle.putInt("id_admin", this.IdAdmin);
                bundle.putInt("id_workday", paySlip.getListWorkdays().get(position).getIdWorkday());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        adapter.setIdUser(IdUser);
        adapter.setData(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvListMonth.setLayoutManager(linearLayoutManager);
        binding.rcvListMonth.setAdapter(adapter);
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        setDataMonthYear();
        clickCallApiGetTimeUser();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<String> months = new ArrayList<>();
        int positionYear = -1, positionMonth = -1;
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
            if (i <= month) positionMonth++;
        }

        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= currentYear; i++) {
            years.add(String.valueOf(i));
            if (i <= year) positionYear++;
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,  R.layout.item_spinner, years);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spMonth.setAdapter(monthAdapter);
        binding.spYear.setAdapter(yearAdapter);
        binding.spMonth.setSelection(positionMonth);
        binding.spYear.setSelection(positionYear);

        binding.txtPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSalaryPayment();
            }
        });

        binding.spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMonth = parentView.getItemAtPosition(position).toString();
                binding.spMonth.setSelection(position);
                month = Integer.parseInt(selectedMonth);
                statisticalTimeUserList=Support.getDatesInMonth(year,month);
                String listTime="",listNameTime="";
                for(int i=0;i<statisticalTimeUserList.size();i++){
                    listTime+=Support.changeReverDateTime(statisticalTimeUserList.get(i).getDayOfWeek(),true)+" ";
                    listNameTime+=statisticalTimeUserList.get(i).getDayOfWeekName()+"-";
                }
                clickCallApiAddPaySlipDetail(Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[1])
                        ,Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[2])
                        ,listTime,listNameTime);
                setDataMonthYear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Xử lý khi một mục được chọn trong Spinner
                String selectedYear = parentView.getItemAtPosition(position).toString();
                binding.spYear.setSelection(position);
                year = Integer.parseInt(selectedYear);
                statisticalTimeUserList=Support.getDatesInMonth(year,month);
                String listTime="",listNameTime="";
                for(int i=0;i<statisticalTimeUserList.size();i++){
                    listTime+=Support.changeReverDateTime(statisticalTimeUserList.get(i).getDayOfWeek(),true)+" ";
                    listNameTime+=statisticalTimeUserList.get(i).getDayOfWeekName()+"-";
                }
                clickCallApiAddPaySlipDetail(Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[1])
                        ,Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[2])
                        ,listTime,listNameTime);
                setDataMonthYear();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void clickCallApiAddPaySlipDetail(int day,int year,String listTime,String listNameTime) {
        ApiService.apiService.addPaySlipDetail(Support.getAuthorization(this),day,year,listTime,listNameTime).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){
                        clickCallApiGetTimeUser();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(TimeKeepingActivity.this);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(TimeKeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
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
            Support.showNotification(TimeKeepingActivity.this, IdUser, IdAdmin, notificationData);
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

    private void setDataMonthYear() {
        binding.txtNumberMonth.setText(String.valueOf(month));
        binding.txtNumberYear.setText(String.valueOf(year));
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment belowFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (belowFragment != null && belowFragment.isVisible()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
        }

        binding.fragmentContainer.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        int positionYear = -1, positionMonth = -1;
        for (int i = 1; i <= 12; i++) {
            if (i <= month) positionMonth++;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= currentYear; i++) {
            if (i <= year) positionYear++;
        }
        binding.spMonth.setSelection(positionMonth);
        binding.spYear.setSelection(positionYear);
        setDataMonthYear();
        clickCallApiGetTimeUser();
    }

    private void clickCallApiGetTimeUser() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getPaySlip(Support.getAuthorization(this),IdUserWatch, month, year).enqueue(new Callback<PaySlipResponse>() {
            @Override
            public void onResponse(Call<PaySlipResponse> call, Response<PaySlipResponse> response) {
                PaySlipResponse paySlipResponse = response.body();
                if (paySlipResponse != null) {
                    if (paySlipResponse.getCode() == 200) {
                        paySlip=paySlipResponse.getPaySlip();
                        binding.txtNameUser.setText(paySlip.getUser().getFullName());
                        binding.txtPositionUser.setText(paySlip.getUser().getPosition().getNamePosition());
                        adapter.setData(paySlip.getListWorkdays());
                        binding.txtNumberOfWorkingDays.setText(paySlip.getNumWorkDay()+"");
                        binding.txtWageOfMonth.setText((int)paySlip.getPrice()+" VND");
                        binding.txtNumLeaveEarly.setText(paySlip.getNumLeaveEarly()+"");
                        binding.txtNumberOfTimesLate.setText(paySlip.getNumLateDay()+"");
                        binding.txtSomeHolidays.setText(paySlip.getNumDayOff()+"");
                    } else{
                        if(paySlipResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(TimeKeepingActivity.this);
                        }else{
                            Toast.makeText(TimeKeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(TimeKeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaySlipResponse> call, Throwable t) {
                Toast.makeText(TimeKeepingActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void showDialogSalaryPayment() {
        final Dialog dialog = new Dialog(TimeKeepingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_salary_payment);
        TextView txtReceiver = dialog.findViewById(R.id.txt_receiver);
        EditText edtWage = dialog.findViewById(R.id.edt_wage);
        EditText edtContent = dialog.findViewById(R.id.edt_content);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        txtReceiver.setText(paySlip.getUser().getFullName());
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtWage.getText().toString().length()==0 || edtContent.getText().toString().length()==0){
                    Toast.makeText(TimeKeepingActivity.this, "Bạn cần nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
                    Toast.makeText(TimeKeepingActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}