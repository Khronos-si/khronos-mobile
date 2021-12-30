package com.example.khronos.ui.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.api.ApiCalls;
import com.example.khronos.structures.TodoGroup;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import dev.sasikanth.colorsheet.ColorSheet;
import dev.sasikanth.colorsheet.utils.ColorSheetUtils;
import kotlin.Unit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditGroup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGroup extends DialogFragment implements View.OnClickListener {

    String TAG = "EditGroup";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "group_id";
    private static final String ARG_PARAM2 = "mail_list";
    private static final String ARG_PARAM3 = "view_id";
    private static final String ARG_PARAM4 = "pop_up_type";

    // TODO: Rename and change types of parameters
    private TodoGroup mGroup;
    private String mGroupId;
    private int mViewId;
    private String mPopUpType;
    private ArrayList<String> mMailList;

    // window elements
    EditText groupNameItemView;
    MultiAutoCompleteTextView emailView;
    TextView colorTextView;
    CheckBox editCheckBox;
    CheckBox deleteCheckBox;

    ArrayList<String> colors = MainActivity.colors;

    public EditGroup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Group ID.
     * @return A new instance of fragment EditGroup.
     */
    // TODO: Rename and change types and number of parameters
    public static EditGroup newInstance(String param1, ArrayList<String> param2, int param3, String param4) {
        EditGroup fragment = new EditGroup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupId = getArguments().getString(ARG_PARAM1);
            mMailList = getArguments().getStringArrayList(ARG_PARAM2);
            mViewId = getArguments().getInt(ARG_PARAM3);
            mPopUpType = getArguments().getString(ARG_PARAM4);
        }

        // init empty group
        this.mGroup = new TodoGroup(mGroupId, null, 0, null, null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_group, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup buttons
        Button save = (Button) view.findViewById(R.id.button_save);
        save.setOnClickListener(this);
        Button cancel = (Button) view.findViewById(R.id.button_cancel);
        cancel.setOnClickListener(this);

        // setup title and texts
        TextView title = view.findViewById(R.id.title);

        // delete button
        ImageButton delete = view.findViewById(R.id.delete);

        if (mPopUpType.equals("edit")) {

            // api call - get group data
            ApiCalls.getTodoGroup(this.mGroup, view);

            // set title
            title.setText("EDIT GROUP");

            // set button text
            save.setText("SAVE");

            // show delete button
            delete.setVisibility(View.VISIBLE);

        } else if (mPopUpType.equals("new")) {
            // set title
            title.setText("ADD GROUP");

            // set button text
            save.setText("ADD");

            // hide delete button
            delete.setVisibility(View.INVISIBLE);
        }

        // listener for delete
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiCalls.deleteTodoGroup(mGroupId, getActivity(), mViewId);
            }
        });


        // setup text fields
        groupNameItemView = view.findViewById(R.id.editGroupName);

        // setup multiautocomplete text view
        emailView = view.findViewById(R.id.sharedWith);
        ArrayAdapter<String> aaStr;

        if (mMailList.size() > 0) {
            mMailList = (ArrayList<String>) mMailList.stream().distinct().collect(Collectors.toList());
            String[] mailList = (String[]) mMailList.toArray(new String[mMailList.size()]);
            aaStr = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, mailList);
            emailView.setAdapter(aaStr);
            emailView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }

        // convert string array of colors to int array
        int [] colorsInt = new int [colors.size()];

        for (int i = 0; i < colors.size(); i++)
            colorsInt[i] = Color.parseColor(colors.get(i));

        // setup color picker
        colorTextView = view.findViewById(R.id.colorText);
        colorTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ColorSheet colorSheet = new ColorSheet();
                colorSheet.colorPicker(colorsInt, 0, false, color -> {

                    String hexcolor = ColorSheetUtils.INSTANCE.colorToHex(color);

                    // set new text
                    colorTextView.setText(hexcolor);

                    // set new background color
                    Drawable background = colorTextView.getBackground().mutate();
                    background.setTint(Color.parseColor(hexcolor));
                    colorTextView.setBackground(background);
                    return null;
                });
                colorSheet.show(getParentFragmentManager());
            }
        });

        // setup checkboxes
        editCheckBox = view.findViewById(R.id.checkEdit);
        deleteCheckBox = view.findViewById(R.id.checkDelete);

        // listener for edit checkbox if unchecked so it unchecks delete
        editCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteCheckBox.isChecked()) {
                    deleteCheckBox.setChecked(false);
                    editCheckBox.setChecked(true);
                }
            }
        });

        // listener for delete checkbox to automatically check edit
        deleteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteCheckBox.isChecked())
                    editCheckBox.setChecked(true);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_cancel:

                // close pop-up
                getActivity().onBackPressed();
                break;

            case R.id.button_save:

                // get all data for request
                String name = groupNameItemView.getText().toString();
                String [] sharedWith = emailView.getText().toString().replaceAll(" ", "").split(",");
                String color = colorTextView.getText().toString();
                int permissions = 0;
                if (editCheckBox.isChecked()) {
                    permissions++;
                    if (deleteCheckBox.isChecked())
                        permissions++;
                }

                // create json object
                JsonObject jsonBody = new JsonObject();
                try {
                    jsonBody.addProperty("name", name);
                    jsonBody.addProperty("permissions", Integer.toString(permissions));
                    jsonBody.addProperty("color", color);

                    JsonArray shareWith = new JsonArray();
                    for (String mail : sharedWith) {
                        if(mail.length() > 1) {
                            mail = mail.replaceAll("\n", "");
                            shareWith.add(mail);
                        }
                        Log.d(TAG, "onClick: MAIL " + mail);
                    }

                    jsonBody.add("sharedWith", shareWith);

                } catch (JsonIOException e) {
                    e.printStackTrace();
                }

                // api call different on create or edit
                if (mPopUpType.equals("edit"))

                    // save data (edit group)
                    ApiCalls.editTodoGroup(jsonBody, mGroupId, getActivity(), mViewId);

                else if (mPopUpType.equals("new")) {

                    // save data (add group)
                    ApiCalls.addTodoGroup(jsonBody, getActivity(), -1);
                }
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