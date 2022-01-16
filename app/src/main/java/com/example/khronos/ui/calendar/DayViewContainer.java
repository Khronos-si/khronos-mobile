package com.example.khronos.ui.calendar;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.R;
import com.example.khronos.structures.CalendarEvent;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import java.util.LinkedList;


public class DayViewContainer extends ViewContainer {

    public TextView textView;
    public ConstraintLayout ozadje;
    public RecyclerView eventDecorationsRecyclerView;

    public CalendarDay day;
    public LinkedList<String> colorevents;
    public LinkedList<CalendarEvent> events;

    public DayViewContainer(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.calendarDayText);
        ozadje = view.findViewById(R.id.ozadje);
        eventDecorationsRecyclerView = view.findViewById(R.id.eventDecorationsRecyclerView);
    }
}
