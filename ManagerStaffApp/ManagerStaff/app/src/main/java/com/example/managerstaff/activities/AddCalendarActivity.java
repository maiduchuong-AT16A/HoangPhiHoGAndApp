package com.example.managerstaff.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityAddCalendarBinding;
import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.TypeCalendar;
import com.example.managerstaff.models.responses.CalendarResponse;
import com.example.managerstaff.models.responses.ListPartResponse;
import com.example.managerstaff.models.responses.ListTypeCalendarResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PartResponse;
import com.example.managerstaff.supports.Database;
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

public class AddCalendarActivity extends AppCompatActivity {

    ActivityAddCalendarBinding binding;
    private int IdUser,IdAdmin,IdCalendar,IdUserChoose;
    private Part part;
    private CalendarA calendar;
    private WebSocketClient client;
    private List<TypeCalendar> listTypeCalendars;
    private String action, day,timeStart,timeEnd,NameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdUserChoose = getIntent().getIntExtra("id_user_choose", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        IdCalendar = getIntent().getIntExtra("id_calendar", 0);
        action = getIntent().getStringExtra("action");
        day = getIntent().getStringExtra("day");
        calendar=new CalendarA();
        part=new Part();
        listTypeCalendars=new ArrayList<>();
        timeStart=Support.getTimeNow2();
        timeEnd=Support.getTimeNow2();
        binding.txtTimeStart.setText(Support.getTimeNow2());
        binding.txtTimeEnd.setText(Support.getTimeNow2());
        binding.txtTimeDay.setText(Support.defineTimeDetail(Support.changeReverDateTime(day,false)));
        binding.txtTitleAddCalendar.setText(getString(R.string.add_calendar));
        NameType="";
        if(action.equals("add")){
            binding.txtEdit.setText(getString(R.string.save));
            binding.txtTitleAddCalendar.setText(getString(R.string.add_calendar));
            setUpDate();
        }else{
            binding.txtTitleAddCalendar.setText(getString(R.string.calendar));
            binding.txtEdit.setText(getString(R.string.edit));
            setDefault();
            clickCallApiGetCalendar();
        }
        binding.edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtAddress.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtAddress.setTextColor(getColor(R.color.black));
            }
        });
        binding.edtBodyCalendar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtBodyCalendar.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtBodyCalendar.setTextColor(getColor(R.color.black));
            }
        });
        binding.edtHeaderCalender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtHeaderCalender.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtHeaderCalendar.setTextColor(getColor(R.color.black));
            }
        });

        binding.spTypeCalendar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                binding.spTypeCalendar.setSelection(position);
                NameType=selectedType;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.txtEdit.getText().toString().equals(getString(R.string.edit))){
                    binding.txtEdit.setText(getString(R.string.save));
                    setUpDate();
                }else {
                    if (checkNoData()) {
                        if(action.equals("add")) {
                            clickCallApiAddCalendar();
                        }else{
                            clickCallApiUpdateCalendar();
                        }
                    }
                }
            }
        });

        binding.icGetTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(true);
            }
        });

        binding.icGetTimeEnd.setOnClickListener(new View.OnClickListener() {
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
        clickCallApiGetAllTypeCalendar(0);
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
            Support.showNotification(AddCalendarActivity.this, IdUser, IdAdmin, notificationData);
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

    private void setDefault(){
        binding.edtBodyCalendar.setEnabled(false);
        binding.edtHeaderCalender.setEnabled(false);
        binding.edtAddress.setEnabled(false);
        binding.edtBodyCalendar.setTextColor(getColor(R.color.gray));
        binding.edtHeaderCalender.setTextColor(getColor(R.color.gray));
        binding.edtAddress.setTextColor(getColor(R.color.gray));
        binding.spTypeCalendar.setEnabled(false);
        binding.icGetTimeEnd.setEnabled(false);
        binding.icGetTimeStart.setEnabled(false);
        binding.txtTimeStart.setTextColor(getColor(R.color.gray));
        binding.txtTimeEnd.setTextColor(getColor(R.color.gray));
    }

    private void setUpDate(){
        binding.edtBodyCalendar.setEnabled(true);
        binding.edtHeaderCalender.setEnabled(true);
        binding.edtAddress.setEnabled(true);
        binding.edtBodyCalendar.setTextColor(getColor(R.color.black));
        binding.edtHeaderCalender.setTextColor(getColor(R.color.black));
        binding.edtAddress.setTextColor(getColor(R.color.black));
        binding.spTypeCalendar.setEnabled(true);
        binding.icGetTimeEnd.setEnabled(true);
        binding.icGetTimeStart.setEnabled(true);
        binding.txtTimeStart.setTextColor(getColor(R.color.black));
        binding.txtTimeEnd.setTextColor(getColor(R.color.black));
    }



    private void ResetData(){
        timeStart=Support.getTimeNow2();
        timeEnd=Support.getTimeNow2();
        binding.txtTimeStart.setText(Support.getTimeNow2());
        binding.txtTimeEnd.setText(Support.getTimeNow2());
        setDataTypeCalendar(listTypeCalendars,(action.equals("edit"))?calendar.getTypeCalendar().getIdType():0);
        binding.edtHeaderCalender.setText("");
        binding.edtBodyCalendar.setText("");
        binding.edtAddress.setText("");
    }

    public boolean checkNoData(){
        boolean check=false;
        if(binding.edtAddress.getText().toString().length()==0){
            check=true;
            binding.edtAddress.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtAddress.setTextColor(getColor(R.color.red));
        }
        if(binding.edtHeaderCalender.getText().toString().length()==0){
            check=true;
            binding.edtHeaderCalender.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtHeaderCalendar.setTextColor(getColor(R.color.red));
        }
        if(binding.edtBodyCalendar.getText().toString().length()==0){
            check=true;
            binding.edtBodyCalendar.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtBodyCalendar.setTextColor(getColor(R.color.red));
        }

        if(check) return false;
        return true;
    }

    private void setDataTypeCalendar(List<TypeCalendar> list,int IdType){
        int index=0;
        if(IdType>0){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getIdType()==IdType){
                    index=i;
                    break;
                }
            }
        }
        List<String> listName= Support.getNameTypeCalendar(list);
        ArrayAdapter<String> adapterPart=new ArrayAdapter<>(this,R.layout.item_spinner,listName);
        adapterPart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTypeCalendar.setAdapter(adapterPart);
        NameType=listName.get(index);
        binding.spTypeCalendar.setSelection(index);
    }

    private void showTimePickerDialog(boolean isStart) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

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
                    if(Support.compareToTime(selectedTime,binding.txtTimeEnd.getText().toString())) {
                        timeStart=selectedTime;
                        binding.txtTimeStart.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(AddCalendarActivity.this);
                    }
                }else{
                    if(Support.compareToTime(binding.txtTimeStart.getText().toString(),selectedTime)) {
                        timeEnd=selectedTime;
                        binding.txtTimeEnd.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(AddCalendarActivity.this);
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
            customTimePicker.setHour(Integer.parseInt(timeStart.substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(timeStart.substring(3)));
        }else{
            customTimePicker.setHour(Integer.parseInt(timeEnd.substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(timeEnd.substring(3)));
        }
    }

    private void clickCallApiAddCalendar() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        int index=Support.getIndexOfName(Support.getNameTypeCalendar(listTypeCalendars),NameType);
        ApiService.apiService.addCalendar(Support.getAuthorization(this),IdUserChoose,listTypeCalendars.get(index).getIdType(),binding.edtHeaderCalender.getText().toString(),
                binding.edtBodyCalendar.getText().toString(),binding.edtAddress.getText().toString(),day,timeStart,timeEnd).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){
                        Support.showDialogNotifySaveSuccess(AddCalendarActivity.this);
                        ResetData();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddCalendarActivity.this);
                        }else{
                            if(objectResponse.getCode()==400 && objectResponse.getMessage().equals("Exist")){
                                Support.showDialogWarningSaveCalender(AddCalendarActivity.this);
                            }else{
                                Support.showDialogNotifySaveFalse(AddCalendarActivity.this);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(AddCalendarActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }


    private void clickCallApiUpdateCalendar() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        int index=Support.getIndexOfName(Support.getNameTypeCalendar(listTypeCalendars),NameType);
        ApiService.apiService.updateCalendar(Support.getAuthorization(this),calendar.getIdCalendar(),IdUserChoose,listTypeCalendars.get(index).getIdType(),binding.edtHeaderCalender.getText().toString(),
                binding.edtBodyCalendar.getText().toString(),binding.edtAddress.getText().toString(),day,timeStart,timeEnd).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        Support.showDialogNotifyUpdateSuccess(AddCalendarActivity.this);
                        binding.txtEdit.setText(getString(R.string.edit));
                        setDefault();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddCalendarActivity.this);
                        }else{
                            if(objectResponse.getCode()==400 && objectResponse.getMessage().equals("Exist")){
                                Support.showDialogWarningSaveCalender(AddCalendarActivity.this);
                            }else{
                                Support.showDialogNotifyUpdateFalse(AddCalendarActivity.this);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(AddCalendarActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }
    private void clickCallApiGetCalendar() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getCalendar(Support.getAuthorization(this),IdCalendar).enqueue(new Callback<CalendarResponse>() {
            @Override
            public void onResponse(Call<CalendarResponse> call, Response<CalendarResponse> response) {
                CalendarResponse calendarResponse = response.body();
                if (calendarResponse != null) {
                    if(calendarResponse.getCode()==200){
                        calendar=calendarResponse.getCalendar();
                        binding.edtAddress.setText(calendar.getAddress());
                        binding.edtBodyCalendar.setText(calendar.getBodyCalendar());
                        binding.edtHeaderCalender.setText(calendar.getHeaderCalendar());
                        timeStart=calendar.getTimeStart().substring(0,5);
                        timeEnd=calendar.getTimeEnd().substring(0,5);
                        binding.txtTimeStart.setText(timeStart);
                        binding.txtTimeEnd.setText(timeEnd);
                        clickCallApiGetAllTypeCalendar(calendar.getTypeCalendar().getIdType());
                    }else{
                        if(calendarResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddCalendarActivity.this);
                        }else{
                            Toast.makeText(AddCalendarActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddCalendarActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CalendarResponse> call, Throwable t) {
                Toast.makeText(AddCalendarActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllTypeCalendar(int IdType) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllTypeCalendar(Support.getAuthorization(this)).enqueue(new Callback<ListTypeCalendarResponse>() {
            @Override
            public void onResponse(Call<ListTypeCalendarResponse> call, Response<ListTypeCalendarResponse> response) {
                ListTypeCalendarResponse typeCalendarResponse = response.body();
                if (typeCalendarResponse != null) {
                    if(typeCalendarResponse.getCode()==200){
                        listTypeCalendars=typeCalendarResponse.getListTypeCalendars();
                        setDataTypeCalendar(listTypeCalendars,IdType);
                    }else{
                        if(typeCalendarResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddCalendarActivity.this);
                        }else{
                            Toast.makeText(AddCalendarActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddCalendarActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListTypeCalendarResponse> call, Throwable t) {
                Toast.makeText(AddCalendarActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}