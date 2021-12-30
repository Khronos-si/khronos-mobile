package com.example.khronos.ui.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.khronos.MainActivity;
import com.example.khronos.api.ApiCalls;
import com.example.khronos.R;
import com.example.khronos.databinding.ActivityMainBinding;
import com.example.khronos.structures.Todo;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTodo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTodo extends DialogFragment implements View.OnClickListener {

    String TAG = "EditTodo";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "task_id";
    private static final String ARG_PARAM2 = "view_id";
    private static final String ARG_PARAM3 = "pop_up_type";

    private Todo todo;
    private String mtodoID;
    private int mviewID;
    private String mPopUpType;

    // editText fields
    EditText taskNameItemView;
    EditText taskDescItemView;

    public EditTodo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Todo ID
     * @return A new instance of fragment EditTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTodo newInstance(String param1, int param2, String param3) {
        EditTodo fragment = new EditTodo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mtodoID = getArguments().getString(ARG_PARAM1);
            mviewID = getArguments().getInt(ARG_PARAM2);
            mPopUpType = getArguments().getString(ARG_PARAM3);
        }

        // init empty todo
        this.todo = new Todo(mtodoID, null, null, false, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_todo, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: popuptype: " + mPopUpType);

        // set text fields
        taskNameItemView = view.findViewById(R.id.editTaskName);
        taskDescItemView = view.findViewById(R.id.editTaskDescription);

        // set buttons
        Button save = (Button) view.findViewById(R.id.button_save);
        save.setOnClickListener(this);
        Button cancel = (Button) view.findViewById(R.id.button_cancel);
        cancel.setOnClickListener(this);

        // title
        TextView title = view.findViewById(R.id.title);

        // delete button
        ImageButton delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(this);

        // if we use popup for edit - display existing data
       if (mPopUpType.equals("edit")) {

           // api call - get task and display data
           ApiCalls.getTodo(mtodoID, this.todo, view);

           // set title
           title.setText("EDIT TASK");

           // set button text
           save.setText("SAVE");

           // show delete button
           delete.setVisibility(View.VISIBLE);

       } else if (mPopUpType.equals("new")) {

           // set title
           title.setText("ADD TASK");

           // set button text
           save.setText("ADD");

           // hide delete button
           delete.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_cancel:

                // close pop-up
                getActivity().onBackPressed();
                break;

            case R.id.button_save:

                // get fields
                String name = taskNameItemView.getText().toString();
                String description = taskDescItemView.getText().toString();
                //boolean status = statusCheckbox.isChecked();
                //String [] tagsList = tagView.getText().toString().replaceAll(" ", "").split(",");

                JsonObject jsonBody = new JsonObject();
                try {
                    jsonBody.addProperty("name", name);
                    jsonBody.addProperty("description", description);
                    //jsonBody.addProperty("status", status);

                    /*JsonArray tags = new JsonArray();
                    for (String tag : tagsList) {
                        if(tag.length() > 1) {
                            tag = tag.replaceAll("\n", "");
                            tags.add(tag);
                        }
                    }*/

                    //jsonBody.add("tags", tags);

                } catch (JsonIOException e) {
                    e.printStackTrace();
                }

                if (mPopUpType.equals("edit"))
                    // save data (edit task)
                    ApiCalls.editTodo(jsonBody, mtodoID, getActivity(), mviewID);
                else if (mPopUpType.equals("new")) {
                    // pri new hranimo v mTodoId id grupe, ki je polje v jsonu
                    jsonBody.addProperty("todoGroupId", mtodoID);
                    // save data (edit group)
                    ApiCalls.addTodo(jsonBody, getActivity(), mviewID);
                }
                break;
            case R.id.delete:
                Log.d(TAG, "onClick: DELETE");

                ApiCalls.deleteTodo(mtodoID, getActivity(), mviewID);
                break;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // hide keyboard
        if (activity.getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}