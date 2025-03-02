package com.example.managerstaff.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerstaff.R;
import com.example.managerstaff.models.StatisticalTimeKeeping;
import com.example.managerstaff.models.Workday;

import java.util.List;

public class StatisticalTimeKeepingAdapter extends RecyclerView.Adapter<StatisticalTimeKeepingAdapter.TimeKeepingViewHolder> {
    private FragmentActivity mActivity;
    private List<StatisticalTimeKeeping> listStatisticals;
    private int idUser=0,month=0,year=0;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }
    public StatisticalTimeKeepingAdapter(FragmentActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void setData(List<StatisticalTimeKeeping> list){
        this.listStatisticals=list;
        notifyDataSetChanged();
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setIdUser(int idUser){
        this.idUser=idUser;
    }

    @NonNull
    @Override
    public StatisticalTimeKeepingAdapter.TimeKeepingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical,parent,false);
        return new StatisticalTimeKeepingAdapter.TimeKeepingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeKeepingViewHolder holder, int position) {
        int p=position;
        StatisticalTimeKeeping statisticalTimeKeeping =listStatisticals.get(position);
        if(statisticalTimeKeeping!=null){

            holder.txtNameUser.setText(statisticalTimeKeeping.getUser().getFullName());
            holder.txtPosition.setText(statisticalTimeKeeping.getUser().getPosition().getNamePosition());
            holder.txtWorkLate.setText(statisticalTimeKeeping.getNumLateDay()+"");
            holder.txtLeaveEarly.setText(statisticalTimeKeeping.getNumLeaveEarly()+"");
            holder.txtWorkOff.setText(statisticalTimeKeeping.getNumDayOff()+"");
            holder.txtSumError.setText(statisticalTimeKeeping.getTotalErrors()+"");

            if(onClickListener!=null) {
                holder.layout_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onItemClick(p);
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        if(listStatisticals!=null) return listStatisticals.size();
        return 0;
    }
    public class  TimeKeepingViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layout_item;
        private TextView txtNameUser,txtPosition,txtWorkLate,txtLeaveEarly, txtWorkOff,txtSumError;

        public TimeKeepingViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_item=itemView.findViewById(R.id.layout_item_statistical);
            txtNameUser=itemView.findViewById(R.id.txt_name_user);
            txtPosition=itemView.findViewById(R.id.txt_position);
            txtWorkLate=itemView.findViewById(R.id.txt_work_late);
            txtLeaveEarly=itemView.findViewById(R.id.txt_leave_early);
            txtWorkOff=itemView.findViewById(R.id.txt_work_off);
            txtSumError=itemView.findViewById(R.id.txt_sum_error);

        }
    }
}