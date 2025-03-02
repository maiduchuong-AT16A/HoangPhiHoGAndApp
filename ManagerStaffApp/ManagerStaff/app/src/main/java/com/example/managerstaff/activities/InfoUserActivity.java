package com.example.managerstaff.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerstaff.R;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityInfoUserBinding;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.Position;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListPartResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.models.responses.PositionResponse;
import com.example.managerstaff.models.responses.UserResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InfoUserActivity extends AppCompatActivity {

    ActivityInfoUserBinding binding;
    private List<String> listGenders;
    private int IdUser,IdAdmin,IdUserWatch;
    private User user;
    private WebSocketClient client;
    private int REQUEST_CODE_FOLDER=456;
    private List<Part> listParts;
    private List<Position> listPositions;
    private List<String> listNameParts,listNamePositions;
    private String action;
    private boolean validPass,validConfirmPass, isShowPass=false,isShowPassConfirm=false;
    private String AvatarNew,AvatarOld;
    private boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IdUser = getIntent().getIntExtra("id_user", 0);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        action = getIntent().getStringExtra("action");
        IdUserWatch = getIntent().getIntExtra("id_user_watch", 0);
        client = new WebSocketClient();
        user=new User();
        validPass=false;
        validConfirmPass=false;
        listGenders=new ArrayList<>();
        listPositions=new ArrayList<>();
        listParts=new ArrayList<>();
        listNameParts=new ArrayList<>();
        listNamePositions=new ArrayList<>();
        listGenders.add(getString(R.string.Male));
        listGenders.add(getString(R.string.Female));
        AvatarNew="";
        AvatarOld="";
        ArrayAdapter<String> adapterGender=new ArrayAdapter<>(this,R.layout.item_spinner,listGenders);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(adapterGender);
        setEditTextDefault();
        onEventClick();
        edit=false;
        if(action.equals("edit")) {
            binding.txtTitleFuntionUser.setText(getString(R.string.infomation_user));
            if(IdUser!=IdAdmin || IdUserWatch==IdAdmin) {
                binding.edtPassword.setVisibility(View.GONE);
                binding.star10.setVisibility(View.GONE);
                binding.txtTitle20.setVisibility(View.GONE);
                binding.icSeePassword1.setVisibility(View.GONE);
            }
            if(IdUserWatch==IdAdmin){
                binding.imgShowMonth1.setVisibility(View.GONE);
                binding.imgShowMonth2.setVisibility(View.GONE);
                binding.txtTitle8.setVisibility(View.GONE);
                binding.spPart.setVisibility(View.GONE);
                binding.txtTitle9.setVisibility(View.GONE);
                binding.spPosition.setVisibility(View.GONE);
                binding.txtTitle10.setVisibility(View.GONE);
                binding.txtStar20.setVisibility(View.GONE);
                binding.edtWage.setVisibility(View.GONE);
                binding.txtTitle25.setVisibility(View.GONE);
            }
            binding.txtTitle20.setVisibility(View.GONE);
            binding.star10.setVisibility(View.GONE);
            binding.edtPassword.setVisibility(View.GONE);
            binding.icSeePassword1.setVisibility(View.GONE);
            binding.icSeePassword2.setVisibility(View.GONE);
            binding.edtConfirmPassword.setVisibility(View.GONE);
            binding.star11.setVisibility(View.GONE);
            binding.txtTitle21.setVisibility(View.GONE);
            binding.txtStar20.setVisibility(View.GONE);
            clickCallApiGetUserDetail();
        }else{
            binding.txtTitleFuntionUser.setText(getString(R.string.add_user));
            binding.icSeePassword2.setVisibility(View.VISIBLE);
            binding.icSeePassword1.setVisibility(View.VISIBLE);
            binding.txtTitle21.setVisibility(View.VISIBLE);
            binding.edtPassword.setVisibility(View.VISIBLE);
            binding.star10.setVisibility(View.VISIBLE);
            binding.txtStar20.setVisibility(View.VISIBLE);
            binding.txtTitle20.setVisibility(View.VISIBLE);
            binding.edtConfirmPassword.setVisibility(View.VISIBLE);
            binding.star11.setVisibility(View.VISIBLE);
            binding.txtEdit.setText("Lưu");
            binding.txtBirthday.setText(Support.changeReverDateTime(Support.getTimeNow(),false));
            clickCallApiGetAllPart("","");
            setEditTextUpdate();
        }
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
            Support.showNotification(InfoUserActivity.this, IdUser, IdAdmin, notificationData);
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

    private void setDataPart(String namePart,List<Part> list,String namePosition){
        List<String> listName=Support.getNamePart(list);
        listNameParts.clear();
        listNameParts=listName;
        ArrayAdapter<String> adapterPart=new ArrayAdapter<>(this,R.layout.item_spinner,listName);
        adapterPart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPart.setAdapter(adapterPart);
        int index=Support.getIndexOfName(listName,namePart);
        binding.spPart.setSelection(index);
        binding.txtNamePart.setText(listName.get(index));
        clickCallApiGetAllPositionByPart(list.get(index).getIdPart(),namePosition);
    }
    private void setDataPosition(List<Position> list,String namePosition){
        List<String> listName=Support.getNamePosition(list);
        listNamePositions.clear();
        listNamePositions=listName;
        ArrayAdapter<String> adapterPosition=new ArrayAdapter<>(this,R.layout.item_spinner,listName);
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPosition.setAdapter(adapterPosition);
        int index=Support.getIndexOfName(listName,namePosition);
        binding.txtNamePosition.setText(listName.get(index));
        binding.spPosition.setSelection(index);
    }

    private void setEditTextDefault(){
        binding.imgCamera.setVisibility(View.GONE);
        binding.edtUsername.setEnabled(false);
        binding.edtAddress.setEnabled(false);
        binding.edtEmail.setEnabled(false);
        binding.edtWage.setEnabled(false);
        binding.txtTitle25.setTextColor(getColor(R.color.gray));
        binding.edtFullname.setEnabled(false);
        binding.edtPassword.setEnabled(false);
        binding.edtConfirmPassword.setEnabled(false);
        binding.edtPhone.setEnabled(false);
        binding.edtUsername.setTextColor(getColor(R.color.gray));
        binding.edtAddress.setTextColor(getColor(R.color.gray));
        binding.edtEmail.setTextColor(getColor(R.color.gray));
        binding.edtFullname.setTextColor(getColor(R.color.gray));
        binding.edtPhone.setTextColor(getColor(R.color.gray));
        binding.edtWage.setTextColor(getColor(R.color.gray));
        binding.spGender.setEnabled(false);
        binding.spPart.setEnabled(false);
        binding.spPosition.setEnabled(false);
    }

    private void setEditTextUpdate(){
        binding.imgCamera.setVisibility(View.VISIBLE);
        if(IdUserWatch!=IdAdmin) {
            binding.edtUsername.setEnabled(true);
            binding.edtUsername.setTextColor(getColor(R.color.black));
        }
        binding.edtAddress.setEnabled(true);
        binding.edtEmail.setEnabled(true);
        binding.edtFullname.setEnabled(true);
        binding.edtPhone.setEnabled(true);
        binding.edtPassword.setEnabled(true);
        binding.edtConfirmPassword.setEnabled(true);
        binding.edtAddress.setTextColor(getColor(R.color.black));
        binding.edtEmail.setTextColor(getColor(R.color.black));
        binding.edtFullname.setTextColor(getColor(R.color.black));
        binding.edtPhone.setTextColor(getColor(R.color.black));
        binding.spGender.setEnabled(true);
        if(IdUser==IdAdmin) {
            binding.txtTitle25.setTextColor(getColor(R.color.black));
            binding.edtWage.setEnabled(true);
            binding.edtWage.setTextColor(getColor(R.color.black));
            binding.spPart.setEnabled(true);
            binding.spPosition.setEnabled(true);
        }
    }

    private void onEventClick(){
        binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(binding.txtEdit.getText().toString().equals(getString(R.string.edit))) {
                    setEditTextUpdate();
                    binding.txtEdit.setText(getString(R.string.save));
                }else{
                    if(checkNoData()) {
                        if (action.equals("add")) {
                            if (validPass && validConfirmPass) {
                                try {
                                    clickCallApiRegisterUser(Support.getBytesFromInputStream(Uri.parse(AvatarNew),InfoUserActivity.this));
                                } catch (IOException e) {
                                }
                            }else{
                                Toast.makeText(InfoUserActivity.this, "Thông tin không hợp lệ.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if((validPass && validConfirmPass)||(binding.edtPassword.getText().toString().length()==0 && binding.edtConfirmPassword.getText().toString().length()==0)) {
                                try {
                                    clickCallApiUpdateUser(Support.getBytesFromInputStream(Uri.parse(AvatarNew), InfoUserActivity.this));
                                } catch (IOException e) {
                                }
                                setEditTextDefault();
                                binding.txtEdit.setText(getString(R.string.edit));
                            }else{
                                Toast.makeText(InfoUserActivity.this, "Thông tin không hợp lệ.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        Toast.makeText(InfoUserActivity.this, getString(R.string.no_empty), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.cvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.txtEdit.getText().toString().equals(getString(R.string.edit))) {
                    Intent intent = new Intent(InfoUserActivity.this, ShowImageActivity.class);
                    intent.putExtra("position", 0);
                    intent.putExtra("id_user",IdUser);
                    intent.putExtra("id_admin",IdAdmin);
                    intent.putExtra("action", "show");
                    intent.putExtra("uri_avatar", user.getAvatar());
                    startActivity(intent);
                }
            }
        });

        binding.icSeePassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPass==false) {
                    binding.icSeePassword1.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPass=true;
                }else{
                    binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword1.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPass=false;
                }
            }
        });

        binding.icSeePassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPassConfirm==false) {
                    binding.icSeePassword2.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                    binding.edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassConfirm=true;
                }else{
                    binding.edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.icSeePassword2.setImageDrawable(getResources().getDrawable(R.drawable.icon_hide));
                    isShowPassConfirm=false;
                }
            }
        });

        binding.edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtUsername.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtTitle1.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtFullname.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtTitle2.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtPhone.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtTitle4.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtWage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtWage.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                binding.txtTitle10.setTextColor(getColor(R.color.black));
            }
        });

        binding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.edtPassword.getText().toString().trim().length() > 0) {
                    if (Support.checkPassValidate(binding.edtPassword.getText().toString().trim())) {
                        validPass = true;
                        binding.txtWarning2.setVisibility(View.GONE);
                        binding.txtTitle20.setTextColor(getColor(R.color.black));
                        binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    } else {
                        binding.txtWarning2.setVisibility(View.VISIBLE);
                        binding.txtWarning2.setText(getString(R.string.warning_password_new));
                        binding.txtTitle20.setTextColor(getColor(R.color.red));
                        binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                        validPass = false;
                    }
                }else{
                    binding.txtWarning2.setVisibility(View.GONE);
                    binding.txtTitle20.setTextColor(getColor(R.color.black));
                    binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    validPass = false;
                }
            }
        });

        binding.edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtConfirmPassword.getText().toString().length()>0){
                    if(binding.edtConfirmPassword.getText().toString().equals(binding.edtPassword.getText().toString())) {
                        validConfirmPass = true;
                        binding.txtWarning3.setVisibility(View.GONE);
                        binding.txtTitle21.setTextColor(getColor(R.color.black));
                        binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    }else{
                        binding.txtWarning3.setVisibility(View.VISIBLE);
                        binding.txtWarning3.setText(getString(R.string.warning_password_confirm));
                        binding.txtTitle21.setTextColor(getColor(R.color.red));
                        binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                        validConfirmPass = false;
                    }
                }else{
                    binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round));
                    binding.txtWarning3.setVisibility(View.GONE);
                    binding.txtTitle21.setTextColor(getColor(R.color.black));
                    validConfirmPass=false;
                }
            }
        });

        binding.spPart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String namePart = parentView.getItemAtPosition(position).toString();
                binding.txtNamePart.setText(namePart);
                clickCallApiGetAllPositionByPart(listParts.get(position).getIdPart(),(action.equals("edit"))?user.getPosition().getNamePosition():"");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.spPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String namePosition = parentView.getItemAtPosition(position).toString();
                binding.txtNamePosition.setText(namePosition);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        binding.icGetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.txtEdit.getText().toString().equals(getString(R.string.save))){
                    Calendar selectedDate = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            InfoUserActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    selectedDate.set(Calendar.YEAR, year);
                                    selectedDate.set(Calendar.MONTH, month);
                                    selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    String dateformat1 = sdf.format(selectedDate.getTime());
                                    String[] parts = dateformat1.split("/");
                                    if (parts.length == 3) {
                                        String formattedDate = parts[0] + "-" + parts[1] + "-" + parts[2];
                                        binding.txtBirthday.setText(formattedDate);
                                    }
                                }
                            },
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();

                }
            }
        });

        binding.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id_user", user.getIdUser());
                returnIntent.putExtra("avatar", user.getAvatar());
                returnIntent.putExtra("full_name", user.getFullName());
                returnIntent.putExtra("edit", (edit) ? "Yes" : "No");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean checkNoData(){
        boolean check=false;
        if(binding.edtUsername.getText().toString().length()==0){
            check=true;
            binding.edtUsername.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtTitle1.setTextColor(getColor(R.color.red));
        }
        if(action.equals("add")) {
            if (binding.edtPassword.getText().toString().length() == 0) {
                check = true;
                binding.edtPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                binding.txtTitle20.setTextColor(getColor(R.color.red));
            }
            if (binding.edtConfirmPassword.getText().toString().length() == 0) {
                check = true;
                binding.edtConfirmPassword.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
                binding.txtTitle21.setTextColor(getColor(R.color.red));
            }
        }
        if(binding.edtFullname.getText().toString().length()==0){
            check=true;
            binding.edtFullname.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtTitle2.setTextColor(getColor(R.color.red));
        }
        if(binding.edtPhone.getText().toString().length()==0){
            check=true;
            binding.edtPhone.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtTitle4.setTextColor(getColor(R.color.red));
        }
        if(binding.edtWage.getText().toString().length()==0){
            check=true;
            binding.edtWage.setBackgroundDrawable(getDrawable(R.drawable.border_outline_round_red));
            binding.txtTitle10.setTextColor(getColor(R.color.red));
        }

        if(check) return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER&& resultCode==RESULT_OK&&data!=null){
            Uri uri =data.getData();
            AvatarNew = uri.toString();
            Glide.with(InfoUserActivity.this).load(AvatarNew)
                    .error(R.drawable.icon_user_gray)
                    .placeholder(R.drawable.icon_user_gray)
                    .into(binding.imgAvatarUser);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clickCallApiGetUserDetail() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getUserDetail(Support.getAuthorization(this),(IdUser==IdAdmin)?IdUserWatch:IdUser).enqueue(new Callback<UserResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        user=userResponse.getUser();
                        AvatarNew=user.getAvatar();
                        AvatarOld=user.getAvatar();
                        if(user.getAvatar()!=null && user.getAvatar().length()>0){
                            Glide.with(InfoUserActivity.this).load(user.getAvatar())
                                    .error(R.drawable.icon_user_gray)
                                    .placeholder(R.drawable.icon_user_gray)
                                    .into(binding.imgAvatarUser);
                        }
                        binding.edtUsername.setText(user.getAccount().getUsername());
                        binding.edtFullname.setText(user.getFullName());
                        binding.edtPhone.setText(user.getPhone());
                        binding.edtEmail.setText(user.getEmail());
                        binding.txtBirthday.setText(user.getBirthday());
                        binding.edtAddress.setText(user.getAddress());
                        if(user.getGender().equals(getString(R.string.Male))){
                            binding.spGender.setSelection(0);
                        }else{
                            binding.spGender.setSelection(1);
                        }
                        binding.edtWage.setText(Support.formatWage(String.valueOf((int) user.getWage())));
                        clickCallApiGetAllPart(user.getPart().getNamePart(),user.getPosition().getNamePosition());
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(InfoUserActivity.this);
                        }else{
                            Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllPart(String namePart,String namePosition) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllPart(Support.getAuthorization(this)).enqueue(new Callback<ListPartResponse>() {
            @Override
            public void onResponse(Call<ListPartResponse> call, Response<ListPartResponse> response) {
                ListPartResponse partResponse = response.body();
                if (partResponse != null) {
                    if(partResponse.getCode()==200){
                        listParts=partResponse.getListParts();
                        setDataPart(namePart,listParts,namePosition);
                    }else{
                        if(partResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(InfoUserActivity.this);
                        }else{
                            Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListPartResponse> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllPositionByPart(int IdPart,String namePosition) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllPositionByPart(Support.getAuthorization(this),IdPart).enqueue(new Callback<PositionResponse>() {
            @Override
            public void onResponse(Call<PositionResponse> call, Response<PositionResponse> response) {
                PositionResponse positionResponse = response.body();
                if (positionResponse != null) {
                    if(positionResponse.getCode()==200){
                        listPositions=positionResponse.getListPositions();
                        setDataPosition(listPositions,namePosition);
                    }else{
                        if(positionResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(InfoUserActivity.this);
                        }else{
                            Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(InfoUserActivity.this, getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PositionResponse> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clickCallApiRegisterUser(MultipartBody.Part body) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        int indexPart=Support.getIndexOfName(listNameParts,binding.txtNamePart.getText().toString());
        int indexPosition=Support.getIndexOfName(listNamePositions,binding.txtNamePosition.getText().toString());

        ApiService.apiService.registerUser(Support.getAuthorization(this),body,AvatarNew,binding.edtFullname.getText().toString(),
                binding.txtBirthday.getText().toString(),binding.spGender.getSelectedItem().toString(),
                binding.edtAddress.getText().toString(),binding.edtEmail.getText().toString(),binding.edtPhone.getText().toString(),
                Double.parseDouble(binding.edtWage.getText().toString()),
                binding.edtUsername.getText().toString(),binding.edtPassword.getText().toString(),
                listParts.get(indexPart).getIdPart(),listPositions.get(indexPosition).getIdPosition()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==201){
                        user=userResponse.getUser();
                        Intent intent = new Intent(InfoUserActivity.this, ManagerUserActivity.class);
                        intent.putExtra("id_user", IdUser);
                        intent.putExtra("id_admin", IdAdmin);
                        startActivity(intent);
                        finish();
                        Toast.makeText(InfoUserActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(InfoUserActivity.this);
                        }else{
                            if(userResponse.getCode()==400 && userResponse.getMessage().equals("Exist")){
                                Toast.makeText(InfoUserActivity.this, getString(R.string.exist), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(InfoUserActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(InfoUserActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clickCallApiUpdateUser(MultipartBody.Part body) {
        binding.pbLoadData.setVisibility(View.VISIBLE);

        int indexPart=Support.getIndexOfName(listNameParts,binding.txtNamePart.getText().toString());
        int indexPosition=Support.getIndexOfName(listNamePositions,binding.txtNamePosition.getText().toString());

        ApiService.apiService.updateUser(Support.getAuthorization(this),(IdUser==IdAdmin)?IdUserWatch:IdUser,body,AvatarNew,binding.edtFullname.getText().toString(),
                binding.txtBirthday.getText().toString(),binding.spGender.getSelectedItem().toString(),
                binding.edtAddress.getText().toString(),binding.edtEmail.getText().toString(),binding.edtPhone.getText().toString(),
               Support.convertStringToDouble(binding.edtWage.getText().toString()),
                listParts.get(indexPart).getIdPart(),listPositions.get(indexPosition).getIdPosition(),binding.edtUsername.getText().toString(),
                binding.edtPassword.getText().toString()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if(userResponse.getCode()==200){
                        edit=true;
                        user=userResponse.getUser();
                        binding.edtPassword.setText("");
                        binding.edtConfirmPassword.setText("");
                        Toast.makeText(InfoUserActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(userResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(InfoUserActivity.this);
                        }else{
                            ResetDataUser();
                        }
                    }
                } else {
                    ResetDataUser();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(InfoUserActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.edtConfirmPassword.setVisibility(View.GONE);
        binding.star11.setVisibility(View.GONE);
        binding.txtTitle21.setVisibility(View.GONE);
        binding.icSeePassword2.setVisibility(View.GONE);
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void ResetDataUser(){
        if(user.getAvatar().length()>0){
            Glide.with(InfoUserActivity.this).load(user.getAvatar())
                    .error(R.drawable.icon_user_gray)
                    .placeholder(R.drawable.icon_user_gray)
                    .into(binding.imgAvatarUser);
        }

        binding.edtUsername.setText(user.getAccount().getUsername());
        binding.edtFullname.setText(user.getFullName());
        binding.edtPhone.setText(user.getPhone());
        binding.edtEmail.setText(user.getEmail());
        binding.txtBirthday.setText(user.getBirthday());
        binding.edtAddress.setText(user.getAddress());
        binding.edtWage.setText(Support.formatWage(String.valueOf((int) user.getWage())));
        Toast.makeText(InfoUserActivity.this, getString(R.string.update_false), Toast.LENGTH_SHORT).show();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}