package com.example.managerstaff.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.managerstaff.fragments.ADTimeKeepingFragment;
import com.example.managerstaff.fragments.CalendarFragment;
import com.example.managerstaff.fragments.HomeFragment;
import com.example.managerstaff.fragments.TimekeepingFragment;
import com.example.managerstaff.fragments.UserFragment;
import com.example.managerstaff.fragments.UtilitiesFragment;
import com.example.managerstaff.models.User;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int IdUser,IdAdmin,IdData;
    private String action;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setIdData(int idData) {
        IdData = idData;
    }

    public void setIdUser(int idUser) {
        IdUser = idUser;
    }

    public void setIdAdmin(int idAdmin) {
        IdAdmin = idAdmin;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("id_user", IdUser);
        bundle.putInt("id_admin", IdAdmin);
        bundle.putInt("id_data", IdData);
        bundle.putString("action", action);
        HomeFragment fragHome = new HomeFragment();
        ADTimeKeepingFragment fragTimeKeepingAD=new ADTimeKeepingFragment();
        fragTimeKeepingAD.setArguments(bundle);
        fragHome.setArguments(bundle);
        CalendarFragment fragCalendar = new CalendarFragment();
        fragCalendar.setArguments(bundle);
        TimekeepingFragment fragTimekeeping = new TimekeepingFragment();
        fragTimekeeping.setArguments(bundle);
        UtilitiesFragment fragutili = new UtilitiesFragment();
        fragutili.setArguments(bundle);
        UserFragment fragUser = new UserFragment();
        fragUser.setArguments(bundle);


        switch (position) {
            case 0:
                return fragHome;
            case 1:
                return (IdUser!=IdAdmin)?fragTimekeeping:fragTimeKeepingAD;
            case 2:
                return fragCalendar;
            case 3:
                return fragutili;
            case 4:
                return fragUser;
        }
        return fragHome;
    }

    @Override
    public int getCount() {
        return 5;
    }

}
