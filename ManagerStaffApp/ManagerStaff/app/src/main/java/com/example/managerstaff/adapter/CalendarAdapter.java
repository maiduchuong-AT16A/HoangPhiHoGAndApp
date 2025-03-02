package com.example.managerstaff.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerstaff.R;
import com.example.managerstaff.models.CalendarA;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Activity mActivity;
    private List<CalendarA> listCalendarAS;
    private int idUser=0;
    private String action;

    private CalendarAdapter.OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(CalendarAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public CalendarAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setData(List<CalendarA> listCalendarAS){
        this.listCalendarAS = listCalendarAS;
        notifyDataSetChanged();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void removeData(CalendarA calendarA){
        this.listCalendarAS.remove(calendarA);
        notifyDataSetChanged();
    }

    public void setIdUser(int idUser){
        this.idUser=idUser;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar,parent,false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int p =position;
        CalendarA calendarA = listCalendarAS.get(position);
        if(calendarA !=null){
            holder.txtTimeStart.setText(calendarA.getTimeStart().substring(0, calendarA.getTimeStart().length()-3));
            holder.txtTimeEnd.setText(calendarA.getTimeEnd().substring(0, calendarA.getTimeEnd().length()-3));
            holder.txtHeader.setText(calendarA.getHeaderCalendar());
            holder.txtTypeCalendar.setText(calendarA.getTypeCalendar().getTypeName());

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
        if(listCalendarAS !=null) return listCalendarAS.size();
        return 0;
    }
    public class  CalendarViewHolder extends RecyclerView.ViewHolder{
        private CardView layout_item;
        private TextView txtTimeStart,txtTimeEnd,txtHeader,txtTypeCalendar;
        public View layoutData;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_item=itemView.findViewById(R.id.layout_item_calendar);
            layoutData=itemView.findViewById(R.id.layout_calender);
            txtTimeStart=itemView.findViewById(R.id.txt_time_start);
            txtTimeEnd=itemView.findViewById(R.id.txt_time_end);
            txtHeader=itemView.findViewById(R.id.txt_header_calendar);
            txtTypeCalendar=itemView.findViewById(R.id.txt_type_calendar);

        }
    }
}