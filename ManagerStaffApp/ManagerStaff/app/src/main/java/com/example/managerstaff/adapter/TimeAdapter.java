package com.example.managerstaff.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerstaff.R;
import com.example.managerstaff.models.CheckIn;
import com.example.managerstaff.models.CheckOut;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeKeepingViewHolder> {
    private Activity mActivity;

    private List<CheckIn> timeInList;
    private List<CheckOut> timeOutList;
    private TimeAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(TimeAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public TimeAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public List<CheckIn> getTimeInList() {
        return timeInList;
    }

    public List<CheckOut> getTimeOutList() {
        return timeOutList;
    }

    public void setData(List<CheckIn> timeInList, List<CheckOut> timeOutList){
        this.timeInList=timeInList;
        this.timeOutList=timeOutList;
        notifyDataSetChanged();
    }

    public void addTimeIn(CheckIn time){
        this.timeInList.add(time);
        notifyDataSetChanged();
    }

    public void addTimeOut(CheckOut time){
        this.timeOutList.add(time);
        notifyDataSetChanged();
    }

    public void removeData(int IdTimeIn, int IdTimeOut){
        for(int i=0;i<timeInList.size();i++){
            if(timeInList.get(i).getIdTimeIn()==IdTimeIn){
                timeInList.remove(i);
                break;
            }
        }
        for(int i=0;i<timeOutList.size();i++){
            if(timeOutList.get(i).getIdTimeOut()==IdTimeOut){
                timeOutList.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(int IdTimeIn, int IdTimeOut,String timeIn,String timeOut){
        for(int i=0;i<timeInList.size();i++){
            if(timeInList.get(i).getIdTimeIn()==IdTimeIn){
                timeInList.get(i).setTimeIn(timeIn);
                break;
            }
        }
        for(int i=0;i<timeOutList.size();i++){
            if(timeOutList.get(i).getIdTimeOut()==IdTimeOut){
                timeOutList.get(i).setTimeOut(timeOut);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeAdapter.TimeKeepingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timekeeping_detail,parent,false);
        return new TimeAdapter.TimeKeepingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeKeepingViewHolder holder, int position) {

        int p=position;

       if(position<timeInList.size()){
           CheckIn timeIn=timeInList.get(position);
           holder.txtTimeIn.setText(timeIn.getTimeIn().substring(0,timeIn.getTimeIn().length()-3));
       }

        if(position<timeOutList.size()){
            CheckOut timeOut=timeOutList.get(position);
            holder.txtTimeOut.setText(timeOut.getTimeOut().substring(0,timeOut.getTimeOut().length()-3));
        }

        if(onClickListener!=null) {
            holder.layoutItemTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClick(p);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if(timeInList!=null) return timeInList.size();
        return 0;
    }
    public class  TimeKeepingViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTimeIn,txtTimeOut;
        private ConstraintLayout layoutItemTime;

        public TimeKeepingViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemTime=itemView.findViewById(R.id.layout_item_timekeeping);
            txtTimeIn=itemView.findViewById(R.id.txt_time_in);
            txtTimeOut=itemView.findViewById(R.id.txt_time_out);

        }
    }
}