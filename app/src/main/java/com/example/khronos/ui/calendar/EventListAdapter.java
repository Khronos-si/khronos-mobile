package com.example.khronos.ui.calendar;

import android.content.Context;
import android.graphics.Color;
import android.media.metrics.Event;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.R;
import com.example.khronos.structures.CalendarEvent;
import com.example.khronos.ui.tasks.TaskListAdapter;

import java.util.LinkedList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private static final String TAG = "EventListAdapter";

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView eventNameItemView;
        public final TextView eventDescriptionItemView;
        public final TextView colorItemView;
        public final TextView startTimeItemView;
        public final TextView timeSubtextItemView;

        public EventViewHolder(@NonNull View itemView, EventListAdapter mAdapter) {
            super(itemView);

            // bind fields
            this.eventNameItemView = itemView.findViewById(R.id.event_name);
            this.eventDescriptionItemView = itemView.findViewById(R.id.event_description);
            this.colorItemView = itemView.findViewById(R.id.event_color);
            this.startTimeItemView = itemView.findViewById(R.id.start_time);
            this.timeSubtextItemView = itemView.findViewById(R.id.time_subtext);

            // set click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: clicked ");
        }
    }

    private final LinkedList<CalendarEvent> mevents;
    private final LinkedList<String> mcolorevents;
    private final String mSelectedDate;
    private LayoutInflater mInflater;

    public EventListAdapter(Context context, LinkedList<CalendarEvent> events, LinkedList<String> colorEvents, String selectedDate) {
        this.mevents = events;
        this.mcolorevents = colorEvents;
        this.mSelectedDate = selectedDate;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        // get current event
        CalendarEvent mevent = mevents.get(position);
        String name = mevent.getName();
        holder.eventNameItemView.setText(name);
        Log.d(TAG, "onBindViewHolder: event name: " +name);

        String description = mevent.getDescription();
        holder.eventDescriptionItemView.setText(description);

        String color = mcolorevents.get(position);
        Log.d(TAG, "onBindViewHolder: color: " + color);
        holder.colorItemView.setBackgroundColor(Color.parseColor(color));

        String startdatetime = mevent.getStart();
        String starttime = startdatetime.split("T")[1].substring(0, 5);
        String startdate = startdatetime.split("T")[0];

        String enddatetime = mevent.getEnd();
        String endtime = enddatetime.split("T")[1].substring(0, 5);
        String enddate = enddatetime.split("T")[0];

        if (startdate.equals(mSelectedDate))
            holder.startTimeItemView.setText(starttime);
        else if (enddate.equals(mSelectedDate)) {
            holder.timeSubtextItemView.setText("until");
            holder.startTimeItemView.setText(endtime);
        } else {
            holder.startTimeItemView.setText("All day");
            holder.startTimeItemView.setTextSize(20);
        }

    }

    @Override
    public int getItemCount() {
        return mevents.size();
    }

}
