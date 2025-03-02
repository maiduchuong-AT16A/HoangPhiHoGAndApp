package com.example.managerstaff.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.FeedBackActivity;
import com.example.managerstaff.activities.InfomationAppActivity;
import com.example.managerstaff.activities.ListChatActivity;
import com.example.managerstaff.activities.ManagerFeedBackActivity;
import com.example.managerstaff.activities.ManagerUserActivity;
import com.example.managerstaff.activities.NewsActivity;
import com.example.managerstaff.activities.NotificationActivity;
import com.example.managerstaff.activities.ShowImageActivity;
import com.example.managerstaff.activities.StatisticalTimekeepingActivity;
import com.example.managerstaff.adapter.SlidePostAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentHomeBinding;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListPostResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{

    FragmentHomeBinding binding;
    private List<Post> listPosts;
    private boolean isFragmentActive = true;
    private OnFragmentInteractionListener mListener;
    private User user;
    private int REQUEST_CODE=100;
    private Timer timer;
    private int IdUser,IdAdmin;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        IdUser=getArguments().getInt("id_user");
        IdAdmin=getArguments().getInt("id_admin");
        listPosts=new ArrayList<>();
        binding.layoutCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFragment1ButtonClicked(2);
                }
            }
        });


        binding.layoutTimekeeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFragment1ButtonClicked(1);
                }
            }
        });

        binding.imgNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), NotificationActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                startActivity(intent,bndlanimation);
            }
        });

        binding.cardViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireContext(), ShowImageActivity.class);
                intent.putExtra("position",0);
                intent.putExtra("action","show");
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("uri_avatar",(user.getAvatar()!=null)?user.getAvatar():"");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        binding.layoutNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), NewsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("position",0);
                startActivity(intent,bndlanimation);
            }
        });

        binding.layoutQuestionAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), (user.getIsAdmin()==1)? StatisticalTimekeepingActivity.class:ListChatActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("position",0);
                startActivity(intent,bndlanimation);
            }
        });

        binding.layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireActivity(), (user.getIsAdmin()==1)? ManagerFeedBackActivity.class: FeedBackActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user",IdUser);
                intent.putExtra("id_admin",IdAdmin);
                intent.putExtra("action", "list_feedback");
                startActivity(intent,bndlanimation);
            }
        });

        binding.layoutManagerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getIsAdmin()==1) {
                    Intent intent = new Intent(requireActivity(), ManagerUserActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                    intent.putExtra("id_user", IdUser);
                    intent.putExtra("id_admin",IdAdmin);
                    intent.putExtra("action","manager");
                    startActivity(intent, bndlanimation);
                }else{
                    Intent intent = new Intent(requireActivity(), InfomationAppActivity.class);
                    intent.putExtra("id_user", IdUser);
                    intent.putExtra("id_admin",IdAdmin);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                    startActivity(intent, bndlanimation);
                }
            }
        });
        clickCallApiGetUserDetail();
        clickCallApiGetListPosts();
        return binding.getRoot();
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
                        binding.txtNameUser.setText(user.getFullName());
                        binding.txtPosition.setText((user.getIsAdmin()==1)?"Quản trị viên":user.getPosition().getNamePosition());
                        if(user.getAvatar().length()>0){
                            Glide.with(getContext()).load(user.getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
                        }

                        if(Support.checkNewNotify(user.getListNotifications())){
                            binding.imgCircle.setVisibility(View.VISIBLE);
                        }else{
                            binding.imgCircle.setVisibility(View.GONE);
                        }

                        if(user.getIsAdmin()==1){
                            binding.imgFeature4.setImageDrawable(requireContext().getDrawable(R.drawable.icon_statistical));
                            binding.txtNameFeature4.setText("Thống kê");
                            binding.imgFeature5.setImageDrawable(requireContext().getDrawable(R.drawable.icon_email));
                            binding.txtFeature5.setText(requireContext().getString(R.string.list_feedback));
                            binding.imgFeature6.setImageDrawable(requireContext().getDrawable(R.drawable.icon_manager_user));
                            binding.txtNameFeature6.setText(requireContext().getString(R.string.manager_user));
                        }else{
                            binding.imgFeature4.setImageDrawable(requireContext().getDrawable(R.drawable.icon_comment));
                            binding.txtNameFeature4.setText("Hỏi & Đáp");
                            binding.imgFeature5.setImageDrawable(requireContext().getDrawable(R.drawable.icon_reply));
                            binding.txtFeature5.setText(requireContext().getString(R.string.feedback));
                            binding.imgFeature6.setImageDrawable(requireContext().getDrawable(R.drawable.icon_question));
                            binding.txtNameFeature6.setText(requireContext().getString(R.string.infomation_app));
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
                Toast.makeText(getContext(), requireContext().getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragment1ButtonClicked(int position);
    }

    private void clickCallApiGetListPosts() {
        ApiService.apiService.getAllPost(Support.getAuthorization(requireActivity()),0,6,"","","",0).enqueue(new Callback<ListPostResponse>() {
            @Override
            public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                ListPostResponse listPostResponse = response.body();
                if (listPostResponse != null) {
                    if(listPostResponse.getCode()==200){
                        listPosts=listPostResponse.getListPosts();
                        SlidePostAdapter postAdapter = new SlidePostAdapter(getContext(), listPosts,IdUser);
                        binding.myPager.setAdapter(postAdapter);
                        binding.myTablayout.setupWithViewPager(binding.myPager,true);
                        if(listPosts.size()>0){
                            binding.imgNoData.setVisibility(View.GONE);
                            binding.txtNoData.setVisibility(View.GONE);
                        }else{
                            binding.imgNoData.setVisibility(View.VISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(listPostResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(requireActivity());
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
                binding.pbLoadData.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ListPostResponse> call, Throwable t) {
                binding.pbLoadData.setVisibility(View.GONE);
                Toast.makeText(getContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class The_slide_timer extends TimerTask {
        @Override
        public void run() {

            if (isFragmentActive && getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.myPager.getCurrentItem() < listPosts.size() - 1) {
                            binding.myPager.setCurrentItem(binding.myPager.getCurrentItem() + 1);
                        } else
                            binding.myPager.setCurrentItem(0);
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentActive = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clickCallApiGetUserDetail();
        clickCallApiGetListPosts();
        isFragmentActive = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new The_slide_timer(),2000,3000);
    }
}