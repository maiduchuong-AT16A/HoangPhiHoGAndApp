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

public class StatisticalTimeKeepingUserAdapter extends RecyclerView.Adapter<StatisticalTimeKeepingUserAdapter.TimeKeepingViewHolder> {
    private FragmentActivity mActivity;
    private List<Workday> listWorkdays;
    private int idUser=0,month=0,year=0;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }
    public StatisticalTimeKeepingUserAdapter(FragmentActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void setData(List<Workday> list){
        this.listWorkdays=list;
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
    public StatisticalTimeKeepingUserAdapter.TimeKeepingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical_detail,parent,false);
        return new StatisticalTimeKeepingUserAdapter.TimeKeepingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeKeepingViewHolder holder, int position) {
        int p=position;
        Workday workday =listWorkdays.get(position);
        if(workday!=null){

            holder.txtDayOfWeek.setText(workday.getDay());
            holder.txtNameDay.setText(workday.getNameDay());
            holder.txtCheckIn.setText((workday.getListCheckIns().size()>0)?workday.getListCheckIns().get(0).getTimeIn().substring(0,5):"");
            holder.txtCheckOut.setText((workday.getListCheckOuts().size()>0)?workday.getListCheckOuts().get(workday.getListCheckOuts().size()-1).getTimeOut().substring(0,5):"");
            holder.txtTimeEarly.setText((workday.getListCheckOuts().size()>0)?workday.getListCheckOuts().get(workday.getListCheckOuts().size()-1).getTimeDifference():"0 phút");
            holder.txtTimeLate.setText((workday.getListCheckIns().size()>0)?workday.getListCheckIns().get(0).getTimeDifference():"0 phút");
            double moneyFine=Math.abs(((workday.getListCheckIns().size()>0)?workday.getListCheckIns().get(0).getMoney():0)+
                    ((workday.getListCheckOuts().size()>0)?((workday.getListCheckOuts().get(0).getMoney()<0)?workday.getListCheckOuts().get(0).getMoney():0):0));
            holder.txtMoneyFine.setText((int)moneyFine+"");

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
        if(listWorkdays!=null) return listWorkdays.size();
        return 0;
    }
    public class  TimeKeepingViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layout_item;
        private TextView txtDayOfWeek,txtNameDay,txtCheckIn,txtCheckOut, txtTimeLate,txtTimeEarly,txtMoneyFine;

        public TimeKeepingViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_item=itemView.findViewById(R.id.layout_item_statistical);
            txtDayOfWeek=itemView.findViewById(R.id.txt_day_of_week);
            txtNameDay=itemView.findViewById(R.id.txt_day_of_week_name);
            txtCheckIn=itemView.findViewById(R.id.txt_checkin);
            txtCheckOut=itemView.findViewById(R.id.txt_checkout);
            txtTimeLate=itemView.findViewById(R.id.txt_time_late);
            txtTimeEarly=itemView.findViewById(R.id.txt_time_early);
            txtMoneyFine=itemView.findViewById(R.id.txt_money_fine);

        }
    }
}