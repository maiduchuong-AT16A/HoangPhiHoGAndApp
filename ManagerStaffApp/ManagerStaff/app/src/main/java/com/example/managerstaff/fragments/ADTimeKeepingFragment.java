package com.example.managerstaff.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.activities.InfoUserActivity;
import com.example.managerstaff.activities.ManagerUserActivity;
import com.example.managerstaff.activities.TimeKeepingActivity;
import com.example.managerstaff.adapter.UserAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.FragmentADTimeKeepingBinding;
import com.example.managerstaff.databinding.FragmentHomeBinding;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListUserResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.PaginationScrollListener;
import com.example.managerstaff.supports.Support;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ADTimeKeepingFragment extends Fragment {

    FragmentADTimeKeepingBinding binding;
    private int IdUser, IdAdmin;
    private List<User> listUsers;
    private UserAdapter userAdapter;
    private boolean mIsLastPage;
    private String dataSearch;
    private boolean mIsLoading,showMore;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentADTimeKeepingBinding.inflate(inflater, container, false);
        binding.pbLoadData.setVisibility(View.VISIBLE);
        IdUser = getArguments().getInt("id_user");
        IdAdmin = getArguments().getInt("id_admin");
        listUsers = new ArrayList<>();
        user = new User();
        showMore=true;
        dataSearch="";
        userAdapter = new UserAdapter(requireActivity());
        userAdapter.setAction("time");
        userAdapter.setIdUser(IdUser);
        userAdapter.setOnClickListener(position -> {
            Intent intent = new Intent(requireContext(), TimeKeepingActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            intent.putExtra("id_user", IdUser);
            intent.putExtra("id_user_watch", userAdapter.getListUsers().get(position).getIdUser());
            intent.putExtra("id_admin", IdAdmin);
            requireActivity().startActivity(intent, bndlanimation);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListUser.setLayoutManager(linearLayoutManager);
        userAdapter.setData(listUsers);
        binding.rcvListUser.setAdapter(userAdapter);
        clickCallApiGetListUser();

        binding.rcvListUser.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                if (showMore) {
                    mIsLoading = true;
                    binding.pbLoadShowMore.setVisibility(View.VISIBLE);
                    loadNextPage();
                }
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public void onScrolledUp() {

            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!dataSearch.equals(binding.edtSearch.getText().toString())) {
                    dataSearch=binding.edtSearch.getText().toString();
                    userAdapter.resetData();
                    showMore = true;
                    clickCallApiGetListUser();
                }
            }
        });

        return binding.getRoot();
    }

    private void clickCallApiGetListUser() {
        if (userAdapter.getListUsers().size() == 0) {
            binding.pbLoadData.setVisibility(View.VISIBLE);
        }
        ApiService.apiService.getListUser(Support.getAuthorization(requireActivity()),userAdapter.getListUsers().size(), 16, binding.edtSearch.getText().toString()).enqueue(new Callback<ListUserResponse>() {
            @Override
            public void onResponse(Call<ListUserResponse> call, Response<ListUserResponse> response) {
                ListUserResponse listUserResponse = response.body();
                if (listUserResponse != null) {
                    if (listUserResponse.getCode() == 200) {
                        List<User> list = listUserResponse.getListUsers();
                        if(list.size()==0) showMore=false;
                        if (userAdapter.getListUsers().size() > 0) {
                            userAdapter.addAllData(list);
                        } else {
                            userAdapter.setData(list);
                        }
                        if (userAdapter.getListUsers().size() > 0) {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        }
                        binding.txtNumberUser.setText(userAdapter.getListUsers().size() + " nhân viên");
                    }else{
                        if(listUserResponse.getCode()==401){
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
            public void onFailure(Call<ListUserResponse> call, Throwable t) {
                binding.pbLoadData.setVisibility(View.GONE);
                Toast.makeText(requireContext(), getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                if(isAdded()) {
                    clickCallApiGetListUser();
                }
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }
}