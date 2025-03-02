package com.example.managerstaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.ViewPagerAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityMainBinding;
import com.example.managerstaff.fragments.HomeFragment;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.StatisticalTimeUser;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

    ActivityMainBinding binding;
    private int IdUser,p,IdAdmin;
    private List<StatisticalTimeUser> statisticalTimeUserList;
    private String action;
    private int IdData;
    private WebSocketClient client;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new WebSocketClient();
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        IdData = getIntent().getIntExtra("id_data", 0);
        action = getIntent().getStringExtra("action");
        p = getIntent().getIntExtra("position", -1);
        statisticalTimeUserList=new ArrayList<>();
        statisticalTimeUserList.addAll(Support.getDatesInMonth(Integer.parseInt(Support.getDayNow().split("-")[0]),Integer.parseInt(Support.getDayNow().split("-")[1])));
        String day="",listTime="",listNameTime="";
        for(int i=0;i<statisticalTimeUserList.size();i++){
            if(!statisticalTimeUserList.get(i).getDayOfWeekName().equals("Thứ Bảy") && !statisticalTimeUserList.get(i).getDayOfWeekName().equals("Chủ Nhật")) {
                day+=Support.changeReverDateTime(statisticalTimeUserList.get(i).getDayOfWeek(),true)+" ";
            }
            listTime+=Support.changeReverDateTime(statisticalTimeUserList.get(i).getDayOfWeek(),true)+" ";
            listNameTime+=statisticalTimeUserList.get(i).getDayOfWeekName()+"-";
        }
        clickCallApiAddPaySlipDetail(Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[1])
                ,Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[2])
                ,listTime,listNameTime);
        clickCallApiAddAllCalendar(day,Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[1])
                ,Integer.parseInt(statisticalTimeUserList.get(0).getDayOfWeek().split("-")[2]));
        Support.clickCallApiGetNotification(MainActivity.this, IdUser,IdAdmin);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.setIdUser(IdUser);
        viewPagerAdapter.setIdData(IdData);
        viewPagerAdapter.setAction(action);
        viewPagerAdapter.setIdAdmin(IdAdmin);
        binding.viewPager.setAdapter(viewPagerAdapter);
        setFinish();
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.bottomMenuApp.getMenu().findItem(R.id.item_home).setChecked(true);
                        break;
                    case 1:
                        binding.bottomMenuApp.getMenu().findItem(R.id.item_timekeeping).setChecked(true);
                        break;
                    case 2:
                        binding.bottomMenuApp.getMenu().findItem(R.id.item_calendar).setChecked(true);
                        break;
                    case 3:
                        binding.bottomMenuApp.getMenu().findItem(R.id.item_news).setChecked(true);
                        break;
                    case 4:
                        binding.bottomMenuApp.getMenu().findItem(R.id.item_user).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.bottomMenuApp.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.item_home){
                    action="main";
                    viewPagerAdapter.setAction(action);
                    binding.viewPager.setCurrentItem(0);
                }
                if(item.getItemId()==R.id.item_timekeeping){
                    action="main";
                    viewPagerAdapter.setAction(action);
                    binding.viewPager.setCurrentItem(1);
                }
                if(item.getItemId()==R.id.item_calendar){
                    binding.viewPager.setCurrentItem(2);
                }
                if(item.getItemId()==R.id.item_news){
                    action="main";
                    viewPagerAdapter.setAction(action);
                    binding.viewPager.setCurrentItem(3);
                }
                if(item.getItemId()==R.id.item_user){
                    action="main";
                    viewPagerAdapter.setAction(action);
                    binding.viewPager.setCurrentItem(4);
                }
                return true;
            }
        });

        viewPagerAdapter.notifyDataSetChanged();
    }

    public void onStart() {
        super.onStart();
        client.startWebSocket();
        EventBus.getDefault().register(this);
    }

    private void clickCallApiAddAllCalendar(String day,int month,int year) {
        ApiService.apiService.addAllCalendar(Support.getAuthorization(this),day,month,year).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){

                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(MainActivity.this);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
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

                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(MainActivity.this);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        NotificationData notificationData = event.notificationData;
        if(notificationData.getIdUser()==IdUser) {
            Support.showNotification(MainActivity.this, IdUser, IdAdmin, notificationData);
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

    @Override
    public void onFragment1ButtonClicked(int position) {
        binding.viewPager.setCurrentItem(position);
        switch (position){
            case 0:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_home).setChecked(true);
                break;
            case 1:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_timekeeping).setChecked(true);
                break;
            case 2:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_calendar).setChecked(true);
                break;
            case 3:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_news).setChecked(true);
                break;
            case 4:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_user).setChecked(true);
                break;
        }
    }

    private void setFinish(){
        binding.viewPager.setCurrentItem(p);
        switch (p){
            case 0:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_home).setChecked(true);
                break;
            case 1:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_timekeeping).setChecked(true);
                break;
            case 2:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_calendar).setChecked(true);
                break;
            case 3:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_news).setChecked(true);
                break;
            case 4:
                binding.bottomMenuApp.getMenu().findItem(R.id.item_user).setChecked(true);
                break;
        }
    }
}