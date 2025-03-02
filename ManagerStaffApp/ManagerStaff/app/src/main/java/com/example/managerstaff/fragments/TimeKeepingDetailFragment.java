package com.example.managerstaff.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.TimeAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentTimeKeepingDetailBinding;
import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.CheckIn;
import com.example.managerstaff.models.CheckOut;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.Workday;
import com.example.managerstaff.models.responses.CheckInResponse;
import com.example.managerstaff.models.responses.CheckOutResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.SettingResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.models.responses.WorkdayResponse;
import com.example.managerstaff.supports.Support;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeKeepingDetailFragment extends Fragment {

    FragmentTimeKeepingDetailBinding binding;
    private TextView txtTimeIn,txtTimeOut;
    private TimeAdapter adapter;
    private int idWorkday,idUser,idAdmin,idUserWatch;
    private Workday workday;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentTimeKeepingDetailBinding.inflate(inflater, container, false);
        idWorkday=getArguments().getInt("id_workday");
        idUser=getArguments().getInt("id_user");
        idAdmin=getArguments().getInt("id_admin");
        idUserWatch=getArguments().getInt("id_user_watch");
        workday=new Workday();
        adapter=new TimeAdapter(requireActivity());
        adapter.setData(new ArrayList<>(),new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListTime.setLayoutManager(linearLayoutManager);
        binding.rcvListTime.setAdapter(adapter);
        clickCallApiGetWorkdayDetail();
        if(idAdmin==idUser){
            binding.txtAdd.setVisibility(View.VISIBLE);
        }else{
            binding.txtAdd.setVisibility(View.GONE);
        }
        adapter.setOnClickListener(position -> {
            if(idAdmin==idUser){
                showDialogTimeDetail(false,adapter.getTimeInList().get(position).getTimeIn().substring(0,adapter.getTimeInList().get(position).getTimeIn().length()-3),
                        (position<adapter.getTimeOutList().size())?adapter.getTimeOutList().get(position).getTimeOut().substring(0,adapter.getTimeOutList().get(position).getTimeOut().length()-3):"00:00",
                        adapter.getTimeInList().get(position).getIdTimeIn(),
                        (position<adapter.getTimeOutList().size())?adapter.getTimeOutList().get(position).getIdTimeOut():0);
            }
        });
        binding.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogTimeDetail(true,"00:00","00:00",0,0);
            }
        });


        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return binding.getRoot();
    }

    private void clickCallApiGetWorkdayDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getWorkdayDetail(Support.getAuthorization(requireActivity()),idWorkday).enqueue(new Callback<WorkdayResponse>() {
            @Override
            public void onResponse(Call<WorkdayResponse> call, Response<WorkdayResponse> response) {
                WorkdayResponse workdayResponse = response.body();
                if (workdayResponse != null) {
                    if(workdayResponse.getCode()==200){
                        workday=workdayResponse.getWorkday();
                        binding.txtDay.setText(workday.getDay());
                        binding.txtRank.setText(workday.getNameDay());
                        adapter.setData(workday.getListCheckIns(),workday.getListCheckOuts());
                    }else{
                        if(workdayResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkdayResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiUpdateTimeUser(int IdTimeIn,int IdTimeOut,String timeIn,String timeOut) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.updateTime(Support.getAuthorization(requireActivity()),IdTimeIn,IdTimeOut,timeIn, timeOut).enqueue(new Callback<ObjectResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        if(!objectResponse.getData().equals("attended")) {
                            adapter.updateData(IdTimeIn, IdTimeOut, timeIn, timeOut);
                        }else{
                            Toast.makeText(requireContext(), "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiDeleteTimeUser(int IdTimeIn,int IdTimeOut) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deleteTime(Support.getAuthorization(requireActivity()),IdTimeIn,IdTimeOut).enqueue(new Callback<ObjectResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        adapter.removeData(IdTimeIn,IdTimeOut);
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiAddTimeInUser(String time,String timeEnd) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.addTimeIn(Support.getAuthorization(requireActivity()),idUserWatch,Support.changeReverDateTime(workday.getDay(),true),time+":00").enqueue(new Callback<CheckInResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CheckInResponse> call, Response<CheckInResponse> response) {
                CheckInResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        adapter.addTimeIn(objectResponse.getCheckIn());
                        clickCallApiAddTimeOutUser(timeEnd);
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckInResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiAddTimeOutUser(String time) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.addTimeOut(Support.getAuthorization(requireActivity()),idUserWatch,Support.changeReverDateTime(workday.getDay(),true),time+":00").enqueue(new Callback<CheckOutResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CheckOutResponse> call, Response<CheckOutResponse> response) {
                CheckOutResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        adapter.addTimeOut(objectResponse.getCheckOut());
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckOutResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void showDialogTimeDetail(boolean isAdd,String timeStart, String timeEnd,int IdTimeIn,int IdTimeOut) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_time_detail);
        txtTimeIn = dialog.findViewById(R.id.txt_time_in);
        txtTimeOut = dialog.findViewById(R.id.txt_time_out);
        TextView txtTimeDetail = dialog.findViewById(R.id.txt_title_time_detail);
        ImageView imgClockIn = dialog.findViewById(R.id.ic_get_time_in);
        ImageView imgClockOut = dialog.findViewById(R.id.ic_get_time_out);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        if(isAdd){
            txtTimeDetail.setText("Thêm thời gian");
            btnDelete.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }else{
            txtTimeDetail.setText("Thời gian chi tiết");
            btnDelete.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        }
        txtTimeIn.setText(timeStart);
        txtTimeOut.setText((timeEnd.length()>0)?timeEnd:"00:00");
        imgClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(true);
            }
        });
        imgClockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(false);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallApiDeleteTimeUser(IdTimeIn,IdTimeOut);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallApiAddTimeInUser(txtTimeIn.getText().toString(),txtTimeOut.getText().toString());
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallApiUpdateTimeUser((txtTimeIn.getText().toString().equals("00:00"))?0:IdTimeIn,(txtTimeOut.getText().toString().equals("00:00"))?0:IdTimeOut,txtTimeIn.getText().toString()+":00",txtTimeOut.getText().toString()+":00");
                dialog.dismiss();
            }
        });



        dialog.show();

    }

    private void showTimePickerDialog(boolean isStart) {
        final Calendar calendar = Calendar.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_time_picker, null);
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
                    if(Support.compareToTime(selectedTime,txtTimeOut.getText().toString())){
                        txtTimeIn.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(requireActivity());
                    }
                }else{
                    if(Support.compareToTime(txtTimeIn.getText().toString(),selectedTime)){
                        txtTimeOut.setText(selectedTime);
                    }else{
                        Support.showDialogWarningSetTimeDay(requireActivity());
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
            customTimePicker.setHour(Integer.parseInt(txtTimeIn.getText().toString().substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(txtTimeIn.getText().toString().substring(3)));
        }else{
            customTimePicker.setHour(Integer.parseInt(txtTimeOut.getText().toString().substring(0,2)));
            customTimePicker.setMinute(Integer.parseInt(txtTimeOut.getText().toString().substring(3)));
        }
    }
}