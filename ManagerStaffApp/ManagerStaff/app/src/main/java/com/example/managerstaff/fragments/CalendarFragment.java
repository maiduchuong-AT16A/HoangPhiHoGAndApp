package com.example.managerstaff.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.activities.AddCalendarActivity;
import com.example.managerstaff.activities.ManagerUserActivity;
import com.example.managerstaff.adapter.CalendarAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentCalendarBinding;
import com.example.managerstaff.interfaces.ItemTouchHelperListener;
import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.CalendarResponse;
import com.example.managerstaff.models.responses.ListCalendarResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.ListPartResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.RecyclerViewItemCalendarTouchHelper;
import com.example.managerstaff.supports.Support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements ItemTouchHelperListener {

    FragmentCalendarBinding binding;
    private int IdUser,IdAdmin,IdData;
    private User user,userChoose;
    private CalendarAdapter adapter;
    private List<CalendarA> listCalendarAS;
    private int REQUEST_CODE=100;
    private List<Part> listParts;
    private List<User> listUsers;
    private String day,action;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        user=new User();
        IdUser=getArguments().getInt("id_user");
        IdAdmin=getArguments().getInt("id_admin");
        IdData=getArguments().getInt("id_data");
        action=getArguments().getString("action");
        listCalendarAS =new ArrayList<>();
        userChoose=new User();
        listParts=new ArrayList<>();
        day=Support.getDayNow();
        binding.txtDayCalendar.setText(Support.defineTime(Support.changeReverDateTime(day,false)));
        adapter=new CalendarAdapter(requireActivity());
        adapter.setAction("edit");
        if(action!=null && action.equals("notification")){
            clickCallApiGetCalendar();
        }else{
            if(IdUser==IdAdmin){
                clickCallApiGetUserDetailFirst(0);
                binding.layoutAddCalendar.setVisibility(View.VISIBLE);
            }else {
                clickCallApiGetUserDetail();
                binding.layoutAddCalendar.setVisibility(View.GONE);
            }
        }
        adapter.setOnClickListener(position -> {
            if(IdUser!=IdAdmin) {
                showDialogCalendarDetail(listCalendarAS.get(position));
            }else{
                int index=Support.getIndexOfName(Support.getNamePart(listParts),binding.txtNamePart.getText().toString());
                Intent intent=new Intent(requireActivity(), AddCalendarActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user_choose",userChoose.getIdUser());
                intent.putExtra("id_user",user.getIdUser());
                intent.putExtra("day",day);
                intent.putExtra("id_calendar",listCalendarAS.get(position).getIdCalendar());
                intent.putExtra("action","edit");
                intent.putExtra("id_admin",IdAdmin);
                requireActivity().startActivity(intent, bndlanimation);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListCalendar.setLayoutManager(linearLayoutManager);
        binding.layoutChooseUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ManagerUserActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("action","search");
                startActivityForResult(intent,REQUEST_CODE,bndlanimation);
            }
        });
        if (IdUser == IdAdmin) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
            binding.rcvListCalendar.addItemDecoration(itemDecoration);

            ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemCalendarTouchHelper(0, ItemTouchHelper.LEFT, this, (IdUser == IdAdmin) ? true : false);
            new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvListCalendar);
        }

        if(IdUser!=IdAdmin){
            binding.cvInfoUser.setVisibility(View.GONE);
        }


        binding.layoutAddCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), AddCalendarActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",user.getIdUser());
                intent.putExtra("id_user_choose",userChoose.getIdUser());
                intent.putExtra("day",day);
                intent.putExtra("action","add");
                intent.putExtra("id_calendar",-1);
                intent.putExtra("id_admin",IdAdmin);
                requireActivity().startActivity(intent, bndlanimation);
            }
        });

        binding.spNamePart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String namePart = parentView.getItemAtPosition(position).toString();
                binding.txtNamePart.setText(namePart);
                int index=Support.getIndexOfName(Support.getNamePart(listParts),namePart);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(day.split("-")[0]), Integer.parseInt(day.split("-")[1]), Integer.parseInt(day.split("-")[2]));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                //clickCallApiGetListCalendar();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.datePicker1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                day = year+"-"+month+"-"+dayOfMonth;
                binding.txtDayCalendar.setText("Ngày "+((dayOfMonth<10)?"0":"")+dayOfMonth+" Tháng "+((month<10)?"0":"")+month);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int index=Support.getIndexOfName(Support.getNamePart(listParts),binding.txtNamePart.getText().toString());
                clickCallApiGetListCalendar((IdUser==IdAdmin)?userChoose.getIdUser():IdUser);
            }
        });


        return binding.getRoot();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int idUser=data.getIntExtra("id_user",0);
                clickCallApiGetUserDetailFirst(idUser);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        day = Support.getDayNow();
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Integer.parseInt(day.split("-")[0]), Integer.parseInt(day.split("-")[1]) - 1, Integer.parseInt(day.split("-")[2]));
//        long selectedDateInMillis = calendar.getTimeInMillis();
//        binding.datePicker1.setDate(selectedDateInMillis, false, true);
        if(IdUser==IdAdmin){
            if(userChoose.getIdUser()==0){
                clickCallApiGetUserDetailFirst(0);
            }else {
                clickCallApiGetListCalendar(userChoose.getIdUser());
            }
        }else{
            if(user.getIdUser()==0){
                clickCallApiGetUserDetail();
            }else {
                clickCallApiGetListCalendar(user.getIdUser());
            }
        }
    }

    private void setDataPart(List<Part> list){
        List<String> listName=Support.getNamePart(list);
        if (isAdded()) {
            ArrayAdapter<String> adapterPart = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, listName);
            adapterPart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spNamePart.setAdapter(adapterPart);
            binding.txtNamePart.setText(listName.get(0));
            binding.spNamePart.setSelection(0);
        }
    }

    private void clickCallApiGetCalendar() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getCalendar(Support.getAuthorization(requireActivity()),IdData).enqueue(new Callback<CalendarResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CalendarResponse> call, Response<CalendarResponse> response) {
                CalendarResponse calendarResponse = response.body();
                if (calendarResponse != null) {
                    if(calendarResponse.getCode()==200){
//                        day=calendarResponse.getCalendar().getDayCalendar();
//                        showDialogCalendarDetail(calendarResponse.getCalendar());
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Integer.parseInt(day.split("-")[0]), Integer.parseInt(day.split("-")[1])-1, Integer.parseInt(day.split("-")[2]));
//                        long selectedDateInMillis = calendar.getTimeInMillis();
//                        binding.datePicker1.setDate(selectedDateInMillis, false, true);
                        clickCallApiGetUserDetail();
                    }else{
                        if(calendarResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireActivity(), getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(requireActivity(), getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CalendarResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void showDialogCalendarDetail(CalendarA calendarA) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_calendar_detail);

        TextView txtHeaderCalendar=dialog.findViewById(R.id.txt_header);
        TextView txtTypeCalendar=dialog.findViewById(R.id.txt_type);
        TextView txtAddress=dialog.findViewById(R.id.txt_address);
        TextView txtTimeStart=dialog.findViewById(R.id.txt_time_start);
        TextView txtTimeEnd=dialog.findViewById(R.id.txt_time_end);
        TextView txtBodyCalendar=dialog.findViewById(R.id.txt_body_calendar);
        ImageView imgCancel=dialog.findViewById(R.id.img_cancel);

        txtHeaderCalendar.setText(calendarA.getHeaderCalendar());
        txtTypeCalendar.setText(calendarA.getTypeCalendar().getTypeName());
        txtAddress.setText(calendarA.getAddress());
        txtTimeStart.setText(calendarA.getTimeStart().substring(0, calendarA.getTimeStart().length()-3));
        txtTimeEnd.setText(calendarA.getTimeEnd().substring(0, calendarA.getTimeEnd().length()-3));
        txtBodyCalendar.setText(calendarA.getBodyCalendar());

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void clickCallApiGetAllPart() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllPart(Support.getAuthorization(requireActivity())).enqueue(new Callback<ListPartResponse>() {
            @Override
            public void onResponse(Call<ListPartResponse> call, Response<ListPartResponse> response) {
                ListPartResponse partResponse = response.body();
                if (partResponse != null) {
                    if(partResponse.getCode()==200){
                        listParts=partResponse.getListParts();
                        setDataPart(listParts);
                    }else{
                        if(partResponse.getCode()==401){
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
            public void onFailure(Call<ListPartResponse> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetUserDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetail(Support.getAuthorization(requireActivity()),IdUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                        if(isAdded()) {
                            clickCallApiGetListCalendar(user.getIdUser());
                        }
                    }else{
                        if(userResponse.getCode()==401){
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
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetUserDetailFirst(int IdU) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetailFirst(Support.getAuthorization(requireActivity()),IdU).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        userChoose=userResponse.getUser();
                        binding.txtFullName.setText(userChoose.getFullName());
                        binding.txtPosition.setText(userChoose.getPosition().getNamePosition());
                        if(isAdded()) {
                            clickCallApiGetListCalendar(userChoose.getIdUser());
                        }
                    }else{
                        if(userResponse.getCode()==401){
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
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }


    private void clickCallApiGetListCalendar(int idUser) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getCalendar(Support.getAuthorization(requireActivity()),idUser,day).enqueue(new Callback<ListCalendarResponse>() {
            @Override
            public void onResponse(Call<ListCalendarResponse> call, Response<ListCalendarResponse> response) {
                ListCalendarResponse listCalendarResponse = response.body();
                if (listCalendarResponse != null) {
                    if(listCalendarResponse.getCode()==200){
                        listCalendarAS =listCalendarResponse.getListCalendars();
                        adapter.setData(listCalendarAS);
                        if(listCalendarAS.size()>0){
                            binding.imgNoData.setVisibility(View.GONE);
                            binding.txtNoData.setVisibility(View.GONE);
                        }
                        else{
                            binding.imgNoData.setVisibility(View.VISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }
                        binding.rcvListCalendar.setAdapter(adapter);
                    }else{
                        if(listCalendarResponse.getCode()==401){
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
            public void onFailure(Call<ListCalendarResponse> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CalendarAdapter.CalendarViewHolder && IdUser == IdAdmin) {

            int indexDelete = viewHolder.getAdapterPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
            alertDialog.setMessage("Bạn có muốn xoá sự kiện này không?");
            CalendarA calendarDelete = listCalendarAS.get(indexDelete);
            alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clickCallApiDeleteCalendar(calendarDelete);
                }
            });
            alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.setData(listCalendarAS);
                }
            });
            alertDialog.show();

        }
    }

    private void clickCallApiDeleteCalendar(CalendarA calendarDelete) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deleteCalendar(Support.getAuthorization(requireActivity()),calendarDelete.getIdCalendar()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        adapter.removeData(calendarDelete);
                        if(listCalendarAS.size()>0){
                            binding.imgNoData.setVisibility(View.GONE);
                            binding.txtNoData.setVisibility(View.GONE);
                        }
                        else{
                            binding.imgNoData.setVisibility(View.VISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(requireContext(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            adapter.setData(listCalendarAS);
                            Toast.makeText(requireContext(), getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    adapter.setData(listCalendarAS);
                    Toast.makeText(requireContext(), getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }
}