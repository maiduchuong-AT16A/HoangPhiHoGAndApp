package com.example.managerstaff.supports;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ReverseLayoutManager extends LinearLayoutManager {
    public ReverseLayoutManager(Context context) {
        super(context);
        setReverseLayout(true);
        setStackFromEnd(true);
    }
}
