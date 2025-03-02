package com.example.managerstaff.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.adapter.ImageAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityAddPostBinding;
import com.example.managerstaff.models.Image;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.TypePost;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListTypePostResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PostResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

    ActivityAddPostBinding binding;
    private List<TypePost> listTypePosts;
    private List<String> listNameTypePosts;
    private ImageAdapter adapter;
    private Post post;
    private int IdUser,IdPost,p,IdAdmin;
    private int REQUEST_CODE=100;
    private String action,listLinkImage;
    private int IdTypePostSelected;
    private User user;
    private List<Image> listImages,listImageStart;
    private WebSocketClient client;
    private int REQUEST_CODE_FOLDER=456;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        IdPost = getIntent().getIntExtra("id_post", 0);
        p = getIntent().getIntExtra("position", -1);
        action = getIntent().getStringExtra("action");
        client = new WebSocketClient();
        post=new Post();
        listTypePosts=new ArrayList<>();
        listNameTypePosts=new ArrayList<>();
        listImageStart=new ArrayList<>();
        listLinkImage="";
        listImages=new ArrayList<>();
        adapter=new ImageAdapter(this);
        user=new User();
        adapter.setAction("delete");
        adapter.setIdUser(IdUser);
        adapter.setData(listImages);
        adapter.setOnClickListener(position -> {
            Intent intent=new Intent(this, ShowImageActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("id_user",IdUser);
            intent.putExtra("id_admin",IdAdmin);
            intent.putExtra("action","delete");
            intent.putExtra("uri_avatar",listImages.get(position).getImage());
            startActivityForResult(intent,REQUEST_CODE);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvListImage.setLayoutManager(linearLayoutManager);
        binding.rcvListImage.setAdapter(adapter);
        if(action.equals("add")){
            binding.txtTitle123.setText("Bản tin mới");
            clickCallApiGetAllTypePost(0);
            clickCallApiGetUserDetail(IdUser);
            binding.txtTitleFuncionPost.setText(getString(R.string.add_post));
        }else{
            binding.txtTitle123.setText("Sửa bản tin");
            clickCallApiGetPostDetail();
            binding.txtTitleFuncionPost.setText(getString(R.string.post));
        }
        binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNoData()){
                    if(action.equals("add")) {
                        clickCallApiAddPost();
                    }else{
                        clickCallApiUpdatePost();
                    }
                }
            }
        });

        binding.spTypePost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                int index=Support.getIndexOfName(listNameTypePosts,selectedType);
                IdTypePostSelected=listTypePosts.get(index).getIdType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.cvAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listImages.size()<=10) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_FOLDER);
                }else{
                    Toast.makeText(AddPostActivity.this, " Chỉ được thêm tối đa 10 hình ảnh.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.edtHeaderPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtHeaderPost.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.appCompatTextView9.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtContentPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtContentPost.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.appCompatTextView10.setTextColor(getColor(R.color.black));
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public boolean checkNoData(){
        boolean check=false;
        if(binding.edtHeaderPost.getText().toString().length()==0){
            check=true;
            binding.edtHeaderPost.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.appCompatTextView9.setTextColor(getColor(R.color.red));
        }

        if(binding.edtContentPost.getText().toString().length()==0){
            check=true;
            binding.edtContentPost.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.appCompatTextView10.setTextColor(getColor(R.color.red));
        }

        if(check) return false;
        return true;
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
            Support.showNotification(AddPostActivity.this, IdUser, IdAdmin, notificationData);
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK&&data!=null){
            if(requestCode==REQUEST_CODE_FOLDER) {
                Uri uri = data.getData();
                Image image = new Image();
                image.setImage(uri.toString());
                listImages.add(image);
                adapter.setData(listImages);
            }else{
                int position=data.getIntExtra("position",-1);
                if(position!=-1) {
                    adapter.removeData(listImages.get(position));
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void clickCallApiGetUserDetail(int ID) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetail(Support.getAuthorization(this),ID).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                        if(user.getAvatar()!=null && user.getAvatar().length()>0){
                            Glide.with(AddPostActivity.this).load(user.getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
                        }

                        binding.txtNameUser.setText(user.getFullName());

                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddPostActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetPostDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getPostDetail(Support.getAuthorization(this),IdPost).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                if (postResponse != null) {
                    if(postResponse.getCode()==200){
                        post=postResponse.getPost();
                        listImages=post.getListImages();
                        for(int i=0;i<listImages.size();i++) listImageStart.add(listImages.get(i));
                        binding.edtHeaderPost.setText(post.getHeaderPost());
                        binding.edtContentPost.setText(post.getContent());
                        adapter.setData(post.getListImages());
                        binding.rcvListImage.setAdapter(adapter);
                        if(post.getUser().getAvatar()!=null && post.getUser().getAvatar().length()>0){
                            Glide.with(AddPostActivity.this).load(post.getUser().getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
                        }

                        binding.txtNameUser.setText(post.getUser().getFullName());
                        clickCallApiGetAllTypePost(post.getTypePost().getIdType());
                    }else{
                        if(postResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddPostActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this,getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiAddPost() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.addPost(Support.getAuthorization(this),IdUser,IdTypePostSelected, binding.edtHeaderPost.getText().toString(),Support.getTimeNow(),binding.edtContentPost.getText().toString(),
                0,0).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==201){
                        post=objectResponse.getPost();
                        for(int i=0;i<listImages.size();i++){
                            try {
                                clickCallApiUploadImage(Support.getBytesFromInputStream(Uri.parse(listImages.get(i).getImage()),AddPostActivity.this),post.getIdPost());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Toast.makeText(AddPostActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddPostActivity.this, NewsActivity.class);
                        intent.putExtra("id_user",IdUser);
                        intent.putExtra("position",p);
                        intent.putExtra("id_admin",IdUser);
                        startActivity(intent);
                        finish();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiUpdatePost() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.updatePost(Support.getAuthorization(this),IdPost,IdTypePostSelected, binding.edtHeaderPost.getText().toString(),binding.edtContentPost.getText().toString()
                ,Support.convertListImageToString(adapter.getListImages())).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){
                        Toast.makeText(AddPostActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        for(int i=0;i<listImages.size();i++){
                            try {
                                clickCallApiUploadImage(Support.getBytesFromInputStream(Uri.parse(listImages.get(i).getImage()),AddPostActivity.this),post.getIdPost());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        finish();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiUploadImage(MultipartBody.Part body,int ID) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.uploadImage(Support.getAuthorization(this),ID,body).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if(objectResponse.getCode()==200){

                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllTypePost(int idTypePost) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllTypePost(Support.getAuthorization(this)).enqueue(new Callback<ListTypePostResponse>() {
            @Override
            public void onResponse(Call<ListTypePostResponse> call, Response<ListTypePostResponse> response) {
                ListTypePostResponse listTypePostResponse = response.body();
                if (listTypePostResponse != null) {
                    if(listTypePostResponse.getCode()==200) {
                        listTypePosts = listTypePostResponse.getListTypePosts();
                        listNameTypePosts = Support.getListTypePost(listTypePosts, false);
                        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(AddPostActivity.this, R.layout.item_spinner, listNameTypePosts);
                        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spTypePost.setAdapter(typeAdapter);
                        int index = 0;
                        for (int i = 0; i < listTypePosts.size(); i++) {
                            if (listTypePosts.get(i).getIdType() == idTypePost) {
                                index = i;
                                break;
                            }
                        }
                        binding.spTypePost.setSelection(index);
                        IdTypePostSelected = listTypePosts.get(index).getIdType();
                    }else{
                        if(listTypePostResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(AddPostActivity.this);
                        }else{
                            Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListTypePostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}