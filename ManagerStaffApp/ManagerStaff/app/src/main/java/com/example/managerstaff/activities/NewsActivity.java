package com.example.managerstaff.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerstaff.R;
import com.example.managerstaff.adapter.PostAdapter;
import com.example.managerstaff.adapter.UserAdapter;
import com.example.managerstaff.api.ApiService;
import com.example.managerstaff.databinding.ActivityNewsBinding;
import com.example.managerstaff.interfaces.ItemTouchHelperListener;
import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.TypePost;
import com.example.managerstaff.models.User;
import com.example.managerstaff.models.responses.ListPostResponse;
import com.example.managerstaff.models.responses.ListTypePostResponse;
import com.example.managerstaff.models.responses.ObjectResponse;
import com.example.managerstaff.supports.Database;
import com.example.managerstaff.supports.MessageEvent;
import com.example.managerstaff.supports.PaginationScrollListener;
import com.example.managerstaff.supports.RecyclerViewItemPostTouchHelper;
import com.example.managerstaff.supports.RecyclerViewItemUserTouchHelper;
import com.example.managerstaff.supports.Support;
import com.example.managerstaff.supports.WebSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity implements ItemTouchHelperListener {

    ActivityNewsBinding binding;
    private List<Post> listPosts;
    private List<String> listNameTypePosts;
    private List<TypePost> listTypePosts;
    private User user;
    private PostAdapter adapter;
    private int p;
    private boolean mIsLoading, showMore;
    private String dataSearch;
    private WebSocketClient client;
    private String timeStartFilter, timeEndFilter, typeFilter;
    private int REQUEST_CODE=1;
    private boolean mIsLastPage;
    private int IdUser, IdAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listPosts = new ArrayList<>();
        listNameTypePosts = new ArrayList<>();
        listTypePosts = new ArrayList<>();
        client = new WebSocketClient();
        timeStartFilter = "";
        timeEndFilter = "";
        typeFilter = "";
        dataSearch = "";
        user = new User();
        adapter = new PostAdapter(NewsActivity.this);
        IdUser = getIntent().getIntExtra("id_user", 0);
        p = getIntent().getIntExtra("position", -1);
        IdAdmin = getIntent().getIntExtra("id_admin", 0);
        adapter.setIdUser(IdUser);
        adapter.setIdAdmin(IdAdmin);
        adapter.setData(listPosts);
        adapter.setOnClickListener(position -> {
            Intent intent=new Intent(this, PostDetailActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
            intent.putExtra("id_user",IdUser);
            intent.putExtra("id_admin",IdAdmin);
            intent.putExtra("position",3);
            intent.putExtra("action","list_news");
            intent.putExtra("id_post",adapter.getListPosts().get(position).getIdPost());
            startActivityForResult(intent,REQUEST_CODE,bndlanimation);
        });
        showMore = true;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NewsActivity.this);
        binding.rcvListNews.setLayoutManager(linearLayoutManager);
        binding.rcvListNews.setAdapter(adapter);
        clickCallApiGetListPosts();
        clickCallApiGetAllTypePost();
        binding.rcvListNews.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        if (IdUser == IdAdmin) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            binding.rcvListNews.addItemDecoration(itemDecoration);

            ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemPostTouchHelper(0, ItemTouchHelper.LEFT, this, (IdUser == IdAdmin) ? true : false);
            new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rcvListNews);
        }

        if (IdUser == IdAdmin) {
            binding.layoutAddNews.setVisibility(View.VISIBLE);
        } else {
            binding.layoutAddNews.setVisibility(View.GONE);
        }

        binding.layoutAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, AddPostActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(NewsActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin", IdAdmin);
                intent.putExtra("id_post", 0);
                intent.putExtra("position", p);
                intent.putExtra("action", "add");
                startActivity(intent, bndlanimation);
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                intent.putExtra("id_user", IdUser);
                intent.putExtra("id_admin", IdAdmin);
                intent.putExtra("action", "main");
                intent.putExtra("position", p);
                startActivity(intent);
                finish();
            }
        });

        binding.imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFilterNews();
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
                    showMore = true;
                    adapter.resetData();
                    dataSearch=binding.edtSearch.getText().toString();
                    clickCallApiGetListPosts();
                }
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
            Support.showNotification(NewsActivity.this, IdUser, IdAdmin, notificationData);
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

    private void showDialogFilterNews() {
        final Dialog dialog = new Dialog(NewsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_filter_post);
        String type = "Tất cả";
        String time = Support.changeReverDateTime(Support.getDayNow(), false);
        TextView txtTimeStart = dialog.findViewById(R.id.txt_time_start_post);
        TextView txtTimeEnd = dialog.findViewById(R.id.txt_time_end_post);
        TextView txtTypePost = dialog.findViewById(R.id.txt_type_post);
        ImageView imgClockStart = dialog.findViewById(R.id.img_show_clock_time_start_post);
        ImageView imgClockEnd = dialog.findViewById(R.id.img_show_clock_time_end_post);
        Spinner spTypePost = dialog.findViewById(R.id.sp_type_post);
        ImageView imgCancel = dialog.findViewById(R.id.img_cancel);
        Button btnFilter = dialog.findViewById(R.id.btn_filter);
        Button btnUnFilter = dialog.findViewById(R.id.btn_unfilter);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(NewsActivity.this,  R.layout.item_spinner, listNameTypePosts);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypePost.setAdapter(typeAdapter);
        spTypePost.setSelection(0);
        txtTimeStart.setText((timeStartFilter.length() > 0) ? timeStartFilter : time);
        txtTimeEnd.setText((timeEndFilter.length() > 0) ? timeEndFilter : time);
        txtTypePost.setText((typeFilter.length() > 0) ? typeFilter : type);

        spTypePost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                spTypePost.setSelection(position);
                txtTypePost.setText(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        txtTypePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spTypePost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selectedType = parentView.getItemAtPosition(position).toString();
                        spTypePost.setSelection(position);
                        txtTypePost.setText(selectedType);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }
        });

        btnUnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.pbLoadData.setVisibility(View.VISIBLE);
                timeStartFilter = "";
                timeEndFilter = "";
                typeFilter = "";
                showMore = true;
                adapter.resetData();
                clickCallApiGetListPosts();
                dialog.dismiss();
                binding.pbLoadData.setVisibility(View.GONE);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.pbLoadData.setVisibility(View.VISIBLE);
                timeStartFilter = txtTimeStart.getText().toString();
                timeEndFilter = txtTimeEnd.getText().toString();
                typeFilter = txtTypePost.getText().toString();
                showMore = true;
                adapter.resetData();
                clickCallApiGetListPosts();
                dialog.dismiss();
                binding.pbLoadData.setVisibility(View.GONE);
            }
        });

        imgClockStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                String timeS = Support.getFormatString(day, month + 1, year);

                                if (Support.compareToDate(timeS, txtTimeEnd.getText().toString()) <= 0) {
                                    selectedDate.set(Calendar.YEAR, year);
                                    selectedDate.set(Calendar.MONTH, month);
                                    selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                    txtTimeStart.setText(timeS);
                                } else {
                                    Support.showDialogWarningSetTimeDay(NewsActivity.this);
                                }


                            }
                        },
                        Integer.parseInt(txtTimeStart.getText().toString().substring(6)),
                        Integer.parseInt(txtTimeStart.getText().toString().substring(3, 5)) - 1,
                        Integer.parseInt(txtTimeStart.getText().toString().substring(0, 2))
                );
                datePickerDialog.show();
            }
        });

        imgClockEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String timeE = Support.getFormatString(day, month + 1, year);

                                if (Support.compareToDate(timeE, txtTimeStart.getText().toString()) >= 0) {
                                    selectedDate.set(Calendar.YEAR, year);
                                    selectedDate.set(Calendar.MONTH, month);
                                    selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                    txtTimeEnd.setText(timeE);
                                } else {
                                    Toast.makeText(NewsActivity.this, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        Integer.parseInt(txtTimeEnd.getText().toString().substring(6)),
                        Integer.parseInt(txtTimeEnd.getText().toString().substring(3, 5)) - 1,
                        Integer.parseInt(txtTimeEnd.getText().toString().substring(0, 2))
                );
                datePickerDialog.show();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int idPostRepair=data.getIntExtra("id_post",0);
                String image = data.getStringExtra("image");
                String headerPost = data.getStringExtra("header_post");
                int idTypePost=data.getIntExtra("id_type_post",0);
                for(int i=0;i<adapter.getListPosts().size();i++){
                    if(adapter.getListPosts().get(i).getIdPost()==idPostRepair){
                        if(adapter.getListPosts().get(i).getListImages().size()>0)
                            adapter.getListPosts().get(i).getListImages().get(0).setImage(image);
                        adapter.getListPosts().get(i).setHeaderPost(headerPost);
                        for(int j=0;j<listTypePosts.size();j++){
                            if(listTypePosts.get(j).getIdType()==idTypePost){
                                adapter.getListPosts().get(i).setTypePost(listTypePosts.get(j));
                                break;
                            }
                        }
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void clickCallApiGetListPosts() {
        if (adapter.getListPosts().size() == 0)
            binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllPost(Support.getAuthorization(this),adapter.getListPosts().size(), 16, binding.edtSearch.getText().toString(), Support.changeReverDateTime(timeStartFilter, true), Support.changeReverDateTime(timeEndFilter, true), Support.getIdTypePost(listTypePosts, typeFilter)).enqueue(new Callback<ListPostResponse>() {
            @Override
            public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                ListPostResponse listPostResponse = response.body();
                if (listPostResponse != null) {
                    if(listPostResponse.getCode()==200) {
                        List<Post> list = listPostResponse.getListPosts();
                        if (list.size() == 0) showMore = false;
                        if (adapter.getListPosts().size() > 0) {
                            adapter.addAllData(list);
                        } else {
                            adapter.setData(list);
                        }
                        if (adapter.getListPosts().size() > 0) {
                            binding.imgNoData.setVisibility(View.GONE);
                            binding.txtNoData.setVisibility(View.GONE);
                        } else {
                            binding.imgNoData.setVisibility(View.VISIBLE);
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }
                        binding.txtNumberNews.setText(adapter.getListPosts().size() + " tin tức");
                    }else{
                        if(listPostResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NewsActivity.this);
                        }else{
                            Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListPostResponse> call, Throwable t) {
                Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PostAdapter.PostViewHolder && IdUser == IdAdmin) {

            int indexDelete = viewHolder.getAdapterPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Bạn có muốn xoá bản tin này không?");
            Post postDelete = adapter.getListPosts().get(indexDelete);
            alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clickCallApiDeletePost(postDelete);
                }
            });
            alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.notifyDataSetChanged();
                }
            });
            alertDialog.show();

        }
    }

    private void clickCallApiDeletePost(Post postDelete) {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.deletePost(Support.getAuthorization(this),postDelete.getIdPost()).enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                ObjectResponse objectResponse = response.body();
                if (objectResponse != null) {
                    if (objectResponse.getCode() == 200) {
                        adapter.removeData(postDelete);
                        binding.txtNumberNews.setText(adapter.getListPosts().size() + " tin tức");
                        Toast.makeText(NewsActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }else{
                        if(objectResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NewsActivity.this);
                        }else{
                            adapter.notifyDataSetChanged();
                            Toast.makeText(NewsActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(NewsActivity.this, getString(R.string.delete_false), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                adapter.setData(listPosts);
                Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void clickCallApiGetAllTypePost() {
        binding.pbLoadData.setVisibility(View.VISIBLE);
        ApiService.apiService.getAllTypePost(Support.getAuthorization(this)).enqueue(new Callback<ListTypePostResponse>() {
            @Override
            public void onResponse(Call<ListTypePostResponse> call, Response<ListTypePostResponse> response) {
                ListTypePostResponse listTypePostResponse = response.body();
                if (listTypePostResponse != null) {
                    if(listTypePostResponse.getCode()==200) {
                        listTypePosts = listTypePostResponse.getListTypePosts();
                        listNameTypePosts = Support.getListTypePost(listTypePosts, true);
                    }else{
                        if(listTypePostResponse.getCode()==401){
                            Support.showDialogWarningExpiredAu(NewsActivity.this);
                        }else{
                            Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListTypePostResponse> call, Throwable t) {
                Toast.makeText(NewsActivity.this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.pbLoadData.setVisibility(View.GONE);
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                clickCallApiGetListPosts();
                binding.pbLoadShowMore.setVisibility(View.GONE);
            }
        }, 1000);
    }

}