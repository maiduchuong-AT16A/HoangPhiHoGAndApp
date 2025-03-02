package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.UserAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityManagerUserBinding;
import com.example.managerstaff.interfaces.ItemTouchHelperListener;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListUserResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.PaginationScrollListener;
import com.example.managerstaff.supports.RecyclerViewItemUserTouchHelper;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerUserActivity extends AppCompatActivity implements ItemTouchHelperListener {

    ActivityManagerUserBinding binding;
    private int IdUser, IdAdmin;
    private boolean mIsLoading,showMore;
    private List<User> listUsers;
    private UserAdapter userAdapter;
    private int REQUEST_CODE = 1;
    private boolean mIsLastPage;
    private String dataSearch;
    private WebSocketClient client;
    private String action;
    private User user;
    private boolean checkClickChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        action = getIntent().getStringExtra("action");
        listUsers = new ArrayList<>();
        client = new WebSocketClient();
        showMore=true;
        checkClickChoose=false;
        mIsLoading = false;
        user = new User();
        dataSearch="";
        userAdapter = new UserAdapter(ManagerUserActivity.this);
        userAdapter.setIdUser(IdUser);
        userAdapter.setShowChooseBox(false);
        userAdapter.setChooseAll(false);
        userAdapter.setIdAdmin(IdAdmin);
        userAdapter.setAction("edit");
        if(!action.isEmpty() && action.equals("search")){
            binding.layoutAddUser.setVisibility(View.GONE);
            binding.txtTypePost.setText("Danh sách nhân viên");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManagerUserActivity.this);
        binding.rcvListUser.setLayoutManager(linearLayoutManager);
        userAdapter.setData(listUsers);
        binding.rcvListUser.setAdapter(userAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListUser.addItemDecoration(itemDecoration);
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
        if(!action.equals("search")) {
            ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemUserTouchHelper(0, ItemTouchHelper.LEFT, this, (action.equals("search")) ? false : true);
            new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvListUser);
        }
        binding.layoutAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerUserActivity.this, InfoUserActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ManagerUserActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_user_watch", 0);
                intent.putExtra("action", "add");
                intent.putExtra("id_admin", IdAdmin);
                startActivity(intent, bndlanimation);
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action.equals("search")){
                    finish();
                }else {
                    Intent intent = new Intent(ManagerUserActivity.this, MainActivity.class);
                    intent.putExtra("id_user", IdUser);
                    intent.putExtra("id_admin", IdAdmin);
                    intent.putExtra("action", "main");
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }
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
        clickCallApiGetListUser();
        userAdapter.setOnClickListener(position -> {
            if(action.equals("search")){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id_user", userAdapter.getListUsers().get(position).getIdUser());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }else {
                Intent intent = new Intent(this, InfoUserActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ManagerUserActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_user_watch", userAdapter.getListUsers().get(position).getIdUser());
                intent.putExtra("action", "edit");
                intent.putExtra("id_admin", IdAdmin);
                startActivityForResult(intent, REQUEST_CODE, bndlanimation);
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
            Support.showNotification(ManagerUserActivity.this, IdUser, IdAdmin, notificationData);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int idUserRepair=data.getIntExtra("id_user",0);
                String avatar = data.getStringExtra("avatar");
                String fullName = data.getStringExtra("full_name");
                String edit = data.getStringExtra("edit");
                if(edit.equals("Yes")){
                    for(int i=0;i<userAdapter.getListUsers().size();i++){
                        if(userAdapter.getListUsers().get(i).getIdUser()==idUserRepair){
                            userAdapter.getListUsers().get(i).setAvatar(avatar);
                            userAdapter.getListUsers().get(i).setFullName(fullName);
                            break;
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void clickCallApiGetListUser() {
        if (userAdapter.getListUsers().size() == 0)
            binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getListUser(Support.getAuthorization(this),userAdapter.getListUsers().size(), 16, binding.edtSearch.getText().toString()).enqueue(new Callback<ListUserResponse>() {
            @Override
            public void onResponse(Call<ListUserResponse> call, Response<ListUserResponse> response) {
                ListUserResponse listUserResponse = response.body();
                if (listUserResponse != null) {
                    if (listUserResponse.getCode() == 200) {
                        if(listUserResponse.getMessage().equals("Success")) {
                            List<User> list = listUserResponse.getListUsers();
                            if (list.size() == 0) showMore = false;
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
                            Support.logOutExpiredToken(ManagerUserActivity.this);
                        }
                    }else{
                        if(listUserResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ManagerUserActivity.this);
                        }else{
                            Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
                binding.pbLoadData.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ListUserResponse> call, Throwable t) {
                binding.pbLoadData.setVisibility(View.GONE);
                Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickCallApiDeleteUser(User userDelete) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deleteUser(Support.getAuthorization(this),userDelete.getIdUser()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        userAdapter.removeData(userDelete);
                        if (userAdapter.getListUsers().size() > 0) {
                            binding.txtNoData.setVisibility(View.GONE);
                            binding.imgNoData.setVisibility(View.GONE);
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                            binding.imgNoData.setVisibility(View.VISIBLE);
                        }
                        binding.txtNumberUser.setText(userAdapter.getListUsers().size() + " nhân viên");
                        Toast.makeText(ManagerUserActivity.this, "Xoá thành công!", Toast.LENGTH_SHORT).show();
                    } else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(ManagerUserActivity.this);
                        }else{
                            userAdapter.notifyDataSetChanged();
                            Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
                binding.pbLoadData.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                binding.pbLoadData.setVisibility(View.GONE);
                Toast.makeText(ManagerUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(!action.equals("search")) {
            if (viewHolder instanceof UserAdapter.UserViewHolder) {

                int indexDelete = viewHolder.getAdapterPosition();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Bạn có muốn xoá tài khoản nhân viên không?");
                User userDelete = userAdapter.getListUsers().get(indexDelete);
                alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clickCallApiDeleteUser(userDelete);
                    }
                });
                alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userAdapter.setData(listUsers);
                    }
                });
                alertDialog.show();

            }
        }
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                clickCallApiGetListUser();
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }
}