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

        public EventViewHolder(@NonNull View itemView, EventListAdapter mAdapter) {
            super(itemView);

            // bind fields
            this.eventNameItemView = itemView.findViewById(R.id.event_name);
            this.eventDescriptionItemView = itemView.findViewById(R.id.event_description);
            this.colorItemView = itemView.findViewById(R.id.event_color);

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
    private LayoutInflater mInflater;

    public EventListAdapter(Context context, LinkedList<CalendarEvent> events, LinkedList<String> colorEvents) {
        this.mevents = events;
        this.mcolorevents = colorEvents;
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




    }

    @Override
    public int getItemCount() {
        return mevents.size();
    }

}
