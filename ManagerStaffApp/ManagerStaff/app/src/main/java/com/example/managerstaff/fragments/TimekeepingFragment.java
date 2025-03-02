package com.example.managerstaff.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.activities.TimeKeepingActivity;
import com.example.managerstaff.adapter.SlidePostAdapter;
import com.example.managerstaff.adapter.TimeKeepingAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentTimekeepingBinding;
import com.example.managerstaff.models.PaySlip;
import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.StatisticalTimeUser;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListPostResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PaySlipResponse;
import com.example.managerstaff.models.responses.SettingResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimekeepingFragment extends Fragment {

    FragmentTimekeepingBinding binding;
    private int month=0;
    private int year=0;
    private int IdUser,IdAdmin;
    private TimeKeepingAdapter adapter;
    private PaySlip paySlip;
    private List<StatisticalTimeUser> statisticalTimeUserList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentTimekeepingBinding.inflate(inflater, container, false);
        IdUser=getArguments().getInt("id_user");
        IdAdmin=getArguments().getInt("id_admin");
        paySlip=new PaySlip();
        statisticalTimeUserList=new ArrayList<>();
        FragmentActivity fragmentActivity = getActivity();
        adapter=new TimeKeepingAdapter(fragmentActivity);
        adapter.setOnClickListener(position -> {
            Fragment belowFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (belowFragment == null || !belowFragment.isVisible()) {
                binding.fragmentContainer.setVisibility(View.VISIBLE);
                TimeKeepingDetailFragment fragment = new TimeKeepingDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id_user", this.IdUser);
                bundle.putInt("id_admin", this.IdAdmin);
                bundle.putInt("id_workday", paySlip.getListWorkdays().get(position).getIdWorkday());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        adapter.setIdUser(IdUser);
        adapter.setData(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListMonth.setLayoutManager(linearLayoutManager);
        binding.rcvListMonth.setAdapter(adapter);
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        setDataMonthYear();
        clickCallApiGetTimeUser();

        List<String> months = new ArrayList<>();
        int positionYear=-1,positionMonth=-1;
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
            if(i<=month) positionMonth++;
        }

        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= currentYear; i++) {
            years.add(String.valueOf(i));
            if(i<=year) positionYear++;
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),  R.layout.item_spinner, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),  R.layout.item_spinner, years);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.spMonth.setAdapter(monthAdapter);
        binding.spYear.setAdapter(yearAdapter);
        binding.spMonth.setSelection(positionMonth);
        binding.spYear.setSelection(positionYear);

        binding.spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMonth = parentView.getItemAtPosition(position).toString();
                binding.spMonth.setSelection(position);
                month=Integer.parseInt(selectedMonth);
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
                String selectedYear = parentView.getItemAtPosition(position).toString();
                binding.spYear.setSelection(position);
                year=Integer.parseInt(selectedYear);
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

        return binding.getRoot();
    }

    private void clickCallApiAddPaySlipDetail(int day,int year,String listTime,String listNameTime) {
        ApiService.apiService.addPaySlipDetail(Support.getAuthorization(requireActivity()),day,year,listTime,listNameTime).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){
                        clickCallApiGetTimeUser();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataMonthYear(){
        binding.txtNumberMonth.setText(String.valueOf(month));
        binding.txtNumberYear.setText(String.valueOf(year));
    }


    private void clickCallApiGetTimeUser() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getPaySlip(Support.getAuthorization(requireActivity()),IdUser,month,year).enqueue(new Callback<PaySlipResponse>() {
            @Override
            public void onResponse(Call<PaySlipResponse> call, Response<PaySlipResponse> response) {
                PaySlipResponse paySlipResponse = response.body();
                if (paySlipResponse != null) {
                    if(paySlipResponse.getCode()==200){
                        paySlip=paySlipResponse.getPaySlip();
                        adapter.setData(paySlip.getListWorkdays());
                        binding.txtNumberOfWorkingDays.setText(paySlip.getNumWorkDay()+"");
                        binding.txtWageOfMonth.setText((int)paySlip.getPrice()+" VND");
                        binding.txtNumLeaveEarly.setText(paySlip.getNumLeaveEarly()+"");
                        binding.txtNumberOfTimesLate.setText(paySlip.getNumLateDay()+"");
                        binding.txtSomeHolidays.setText(paySlip.getNumDayOff()+"");
                    }else{
                        if(paySlipResponse.getCode()==401){
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
            public void onFailure(Call<PaySlipResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

}