package com.example.khronos.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.khronos.R;
import com.kizitonwose.calendarview.ui.ViewContainer;

public class MonthViewContainer extends ViewContainer {

    public TextView textView;

    public MonthViewContainer(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.headerTextView);
    }
}
