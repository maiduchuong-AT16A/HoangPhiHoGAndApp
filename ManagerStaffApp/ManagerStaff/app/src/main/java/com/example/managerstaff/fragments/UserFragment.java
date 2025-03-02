package com.example.managerstaff.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.ChangePasswordActivity;
import com.example.managerstaff.activities.FeedBackActivity;
import com.example.managerstaff.activities.InfoUserActivity;
import com.example.managerstaff.activities.InfomationAppActivity;
import com.example.managerstaff.activities.LoginActivity;
import com.example.managerstaff.activities.RegulatorySettingsActivity;
import com.example.managerstaff.activities.ShowImageActivity;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentUserBinding;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.Support;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    FragmentUserBinding binding;
    private int REQUEST_CODE=100;
    private Database database;
    private int IdUser,IdAdmin;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        IdUser=getArguments().getInt("id_user");
        IdAdmin=getArguments().getInt("id_admin");
        database=new Database(requireContext());
        if(IdAdmin==IdUser){
            binding.cvReplyApp.setVisibility(View.GONE);
            binding.cvSetting.setVisibility(View.VISIBLE);
        }else{
            binding.cvReplyApp.setVisibility(View.VISIBLE);
            binding.cvSetting.setVisibility(View.GONE);
        }
        user=new User();
        eventClick();
        clickCallApiGetUserDetail();
        return binding.getRoot();
    }

    private void eventClick(){
        binding.cvFeatureInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), InfoUserActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_user_watch",IdUser);
                intent.putExtra("action","edit");
                intent.putExtra("id_admin",IdAdmin);
                startActivityForResult(intent,REQUEST_CODE,bndlanimation);
            }
        });

        binding.cvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), RegulatorySettingsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                startActivity(intent,bndlanimation);
            }
        });

        binding.cvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireContext(), ShowImageActivity.class);
                intent.putExtra("position",0);
                intent.putExtra("action","show");
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("uri_avatar",user.getAvatar());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        binding.cvFeatureLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.deleteSession(database.getSessionAuthorization());
                Intent intent=new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.cvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), ChangePasswordActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin", IdAdmin);
                intent.putExtra("action","change");
                startActivityForResult(intent,REQUEST_CODE,bndlanimation);
            }
        });


        binding.cvReplyApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), FeedBackActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin", IdAdmin);
                startActivity(intent,bndlanimation);
            }
        });

        binding.cvFeatureInfoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), InfomationAppActivity.class);
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin",IdAdmin);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                startActivity(intent,bndlanimation);
            }
        });

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
                        binding.txtFullName.setText(user.getFullName());
                        binding.txtPosition.setText((user.getIsAdmin()==1)?"Quản trị viên":user.getPosition().getNamePosition());
                        if(user.getAvatar().length()>0 && isAdded()){
                            Glide.with(requireContext()).load(user.getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
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
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            clickCallApiGetUserDetail();
        }
    }
}