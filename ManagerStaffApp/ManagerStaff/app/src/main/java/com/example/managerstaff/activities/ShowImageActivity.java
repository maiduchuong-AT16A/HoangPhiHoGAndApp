package com.example.managerstaff.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.databinding.ActivityShowImageBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ShowImageActivity extends AppCompatActivity {

    ActivityShowImageBinding binding;
    private String action,UriAvatar;
    private boolean isHide;
    private int IdUser, IdAdmin;
    private WebSocketClient client;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityShowImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        position = getIntent().getIntExtra("position", 0);
        action = getIntent().getStringExtra("action");
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        client = new WebSocketClient();
        UriAvatar = getIntent().getStringExtra("uri_avatar");
        isHide=false;

        if(isHide){
            binding.layoutFeature.setVisibility(View.GONE);
        }else{
            binding.layoutFeature.setVisibility(View.VISIBLE);

        }

        if(action.equals("delete")){
            binding.imgDelete.setVisibility(View.VISIBLE);
        }else{
            binding.imgDelete.setVisibility(View.GONE);
        }
        if(UriAvatar!=null){
            Glide.with(this).load(UriAvatar)
                    .error(R.drawable.image_error)
                    .placeholder(R.drawable.image_error)
                    .into(binding.imgHinhAnh);
        }

        binding.imgHinhAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHide){
                    isHide=false;
                    binding.layoutFeature.setVisibility(View.VISIBLE);
                }else{
                    isHide=true;
                    binding.layoutFeature.setVisibility(View.GONE);

                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
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
            Support.showNotification(ShowImageActivity.this, IdUser, IdAdmin, notificationData);
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

    private void showDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Bạn có muốn xoá ảnh này không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

}