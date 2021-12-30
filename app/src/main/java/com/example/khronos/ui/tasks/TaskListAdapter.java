package com.example.khronos.ui.tasks;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.api.ApiCalls;
import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.structures.Todo;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.LinkedList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private static final String TAG = "TaskListAdapter";
    private final int mPositionId;

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView taskNameItemView; // task name Itemview
        public final TextView taskDescItemView; // task description Itemview
        public final CheckBox statusCheckBox;
        final TaskListAdapter mAdapter;

        private Todo task;

        public TaskViewHolder(@NonNull View itemView, TaskListAdapter mAdapter) {
            super(itemView);
            
            this.taskNameItemView = itemView.findViewById(R.id.task_name);
            this.taskDescItemView = itemView.findViewById(R.id.task_description);
            this.statusCheckBox = itemView.findViewById(R.id.task_status);
            this.mAdapter = mAdapter;

            // task init to null
            this.task = null;

            // set click listener
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            // create edit task pop-up
            DialogFragment fragment = EditTodo.newInstance(task.getId(), mPositionId, "edit");

            MainActivity main = (MainActivity) view.getContext();
            main.showPopUp(R.id.nav_host_fragment_content_main, fragment);
        }
    }

    private final LinkedList<Todo> mtodos;
    private LayoutInflater mInflater;

    public TaskListAdapter(Context context, LinkedList<Todo> tasks, int positionId) {
        this.mtodos = tasks;
        this.mPositionId = positionId;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        // get current task
        Todo mTodo = mtodos.get(position);
        int pos = position;

        // place data in textviews
        holder.taskNameItemView.setText(mTodo.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.taskDescItemView.setText(Html.fromHtml(mTodo.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        else
            holder.taskDescItemView.setText(Html.fromHtml(mTodo.getDescription()));

        // checkbox for status
        holder.statusCheckBox.setChecked(mTodo.isStatus());
        
        // set checkbox click listener
        holder.statusCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // toggle checkbox data
                mTodo.setStatus(!mTodo.isStatus());

                JsonObject jsonBody = new JsonObject();
                try {
                    jsonBody.addProperty("status", mTodo.isStatus());

                } catch (JsonIOException e) {
                    e.printStackTrace();
                }

                ApiCalls.editTodo(jsonBody, mTodo.getId(), null, pos);
            }
        });

        // get whole task
        holder.task = mTodo;

        Log.d(TAG, "onBindViewHolder: ADDED ITEM ID: " + mTodo.getId());
    }

    @Override
    public int getItemCount() {
        return mtodos.size();
    }
}
