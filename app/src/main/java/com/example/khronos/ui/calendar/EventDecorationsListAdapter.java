package com.example.khronos.ui.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.R;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.LinkedList;

public class EventDecorationsListAdapter extends RecyclerView.Adapter<EventDecorationsListAdapter.EventDecorationViewHolder> {

    private final static String TAG = "EventListAdapter";

    public class EventDecorationViewHolder extends RecyclerView.ViewHolder {

        public final TextView eventDot;
        final EventDecorationsListAdapter mAdapter;


        public EventDecorationViewHolder(@NonNull View itemView, EventDecorationsListAdapter mAdapter) {
            super(itemView);

            this.eventDot = itemView.findViewById(R.id.eventDot);
            this.mAdapter = mAdapter;

        }

    }

    private final LinkedList<String> mcolors;
    private LayoutInflater mInflater;

    public EventDecorationsListAdapter(Context context, LinkedList<String> colors) {
        this.mcolors = colors;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public EventDecorationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.event_decoration, parent, false);
        return new EventDecorationViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EventDecorationViewHolder holder, int position) {

        String color = mcolors.get(position);
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED,5)
                .build();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
        shapeDrawable.setFillColor(ColorStateList.valueOf(Color.parseColor(color)));
        ViewCompat.setBackground(holder.eventDot, shapeDrawable);
        //holder.eventDot.setBackgroundColor(Color.parseColor(color));
        // blablabla
        Log.d(TAG, "onBindViewHolder: EVO EVENT");

    }

    @Override
    public int getItemCount() {
        return mcolors.size();
    }
}
