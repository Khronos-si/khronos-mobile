package com.example.khronos.api;

import static com.example.khronos.MainActivity.apiInterface;
import static com.example.khronos.MainActivity.todoGroups;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.khronos.structures.CalendarGroup;
import com.example.khronos.structures.Login;
import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.structures.Todo;
import com.example.khronos.structures.TodoGroup;
import com.example.khronos.structures.User;
import com.example.khronos.ui.calendar.CalendarFragment;
import com.example.khronos.ui.tasks.TasksFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCalls {

    private static final String TAG = "ApiCalls";

    // tasks
    public static void addTodo(JsonObject jsonObject, Activity activity, int groupViewID) {

        Log.d(TAG, "addTodo: call");

        Call<Todo> call = apiInterface.addTodo(jsonObject);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(groupViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.d(TAG, "onResponse: addTodo " + t.toString());
            }
        });
    }

    public static void addTodoGroup(JsonObject jsonObject, Activity activity, int groupViewID) {

        Log.d(TAG, "addTodoGroup: call");
        
        Call<TodoGroup> call = apiInterface.addTodoGroup(jsonObject);
        call.enqueue(new Callback<TodoGroup>() {
            @Override
            public void onResponse(Call<TodoGroup> call, Response<TodoGroup> response) {

                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(groupViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();

                assert response.body() != null;
            }

            @Override
            public void onFailure(Call<TodoGroup> call, Throwable t) {
                Log.d(TAG, "onResponse: addTodoGroupData " + t.toString());
            }
        });
    }

    public static void getTodo(String todoID, Todo todo, View view) {

        Log.d(TAG, "getTodo: call");

        Call<Todo> call = apiInterface.getTodo(todoID);
        call.enqueue(new Callback<Todo>() {

            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                //Log.d(TAG, "getTodo - onResponse:" + response.body());

                // set values
                todo.setName(response.body().getName());
                todo.setDescription(response.body().getDescription());
                todo.setStatus(response.body().isStatus());
                todo.setId(response.body().getId());
                todo.setTags(response.body().getTags());

                if (view != null) {
                    // set editTexts
                    EditText taskNameItemView = view.findViewById(R.id.editTaskName);
                    taskNameItemView.setText(todo.getName());
                    EditText taskDescItemView = view.findViewById(R.id.editTaskDescription);
                    taskDescItemView.setText(todo.getDescription());
                }

            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.d(TAG, "getTodo - onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public static void getTodoGroup(TodoGroup group, View view) {

        Log.d(TAG, "getTodoGroup: call");
        
        Call<TodoGroup> call = apiInterface.getTodoGroup(group.getId());
        call.enqueue(new Callback<TodoGroup>() {
            @Override
            public void onResponse(Call<TodoGroup> call, Response<TodoGroup> response) {

                // set values
                group.setName(response.body().getName());
                group.setId(response.body().getId());
                group.setColor(response.body().getColor());
                group.setTodos(response.body().getTodos());
                group.setPermissions(response.body().getPermissions());
                group.setOwner(response.body().getOwner());
                group.setSharedWith(response.body().getSharedWith());

                if (view != null) {

                    // set name field
                    EditText groupNameView = view.findViewById(R.id.editGroupName);
                    groupNameView.setText(group.getName());

                    // convert user array to show string of mails
                    User[] sharedWith = group.getSharedWith();

                    StringBuilder sb = new StringBuilder();
                    for (User user : sharedWith)
                        sb.append(user.getEmail() + ", ");

                    // show shared emails string
                    MultiAutoCompleteTextView mailView = view.findViewById(R.id.sharedWith);
                    mailView.setText(sb.toString());

                    // set permissions
                    CheckBox writeCheckBox = view.findViewById(R.id.checkEdit);
                    CheckBox deleteCheckBox = view.findViewById(R.id.checkDelete);
                    int permissions = group.getPermissions();
                    Log.d(TAG, "onResponse: PERMISSIONS " + permissions);

                    if (permissions > 0) {
                        writeCheckBox.setChecked(true);
                        if (permissions > 1)
                            deleteCheckBox.setChecked(true);
                    }

                    String color = group.getColor().toUpperCase();
                    TextView colorTextView = view.findViewById(R.id.colorText);

                    // color picker background color
                    Drawable background = colorTextView.getBackground().mutate();
                    background.setTint(Color.parseColor(color));
                    colorTextView.setBackground(background);
                    colorTextView.setText(color);
                }
            }

            @Override
            public void onFailure(Call<TodoGroup> call, Throwable t) {
                Log.d(TAG, "onFailure: getTodoGroup " + t.getLocalizedMessage());
            }
        });
    }

    public static void getTodoGroups(LinkedList<TodoGroup> taskGroups, int selectedGroup, FragmentTransaction ft) {

        Log.d(TAG, "getTodoGroups: call");
        
        Call<List<TodoGroup>> call = apiInterface.getTodoGroups();
        call.enqueue(new Callback<List<TodoGroup>>() {

            @Override
            public void onResponse(@NonNull Call<List<TodoGroup>> call, @NonNull Response<List<TodoGroup>> response) {

               if (response.body() != null) {
                   // fill task group list
                   for (TodoGroup group:response.body()) {
                       //Log.d(TAG, "GROUP NAME: " + group.getName());
                       taskGroups.addLast(group);
                   }

                   int newSelectedGroup = selectedGroup;
                   // set active group to show
                   if (selectedGroup < 0) // first item
                       newSelectedGroup= todoGroups.size() - 1;

                   // send data to fragment & show it
                   Bundle bundle = new Bundle();
                   bundle.putInt("selected_group", newSelectedGroup);

                   Fragment fragment = new TasksFragment();
                   fragment.setArguments(bundle);

                   if (ft != null) {
                       ft.replace(R.id.nav_host_fragment_content_main, fragment, "tasks");
                       ft.commit();
                   }
               }
            }

            @Override
            public void onFailure(Call<List<TodoGroup>> call, Throwable t) {
                Log.d(TAG, "onFailure: getTodoGroups " + t.getLocalizedMessage());
            }
        });
    }

    public static void editTodo(JsonObject jsonObject, String todoId, Activity activity, int groupViewID) {

        Log.d(TAG, "editTodo: call");
        
        Log.d(TAG, "editTodo: EDIT");
        Call<Todo> call = apiInterface.editTodo(todoId, jsonObject);
        call.enqueue(new Callback<Todo>() {

            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {

                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(groupViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.d(TAG, "onResponse: editTodo " + t.toString());
            }
        });
    }

    public static void editTodoGroup(JsonObject jsonObject, String groupId, Activity activity, int groupViewID) {

        Log.d(TAG, "editTodoGroup: call");
        
        Call<TodoGroup> call = apiInterface.editTodoGroup(groupId, jsonObject);
        call.enqueue(new Callback<TodoGroup>() {
            @Override
            public void onResponse(Call<TodoGroup> call, Response<TodoGroup> response) {

                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(groupViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();

                assert response.body() != null;
            }

            @Override
            public void onFailure(Call<TodoGroup> call, Throwable t) {
                Log.d(TAG, "onResponse: saveTodoGroupData " + t.toString());
            }
        });
    }

    public static void deleteTodo(String todoID, Activity activity, int groupViewID) {
        Log.d(TAG, "deleteTodo: call");

        Call<JsonObject> call = apiInterface.deleteTodo(todoID);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(groupViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: deleteTodo");
            }

        });
    }

    public static void deleteTodoGroup(String groupID, Activity activity, int groupViewID) {
        Log.d(TAG, "deleteTodoGroup: call");
        
        Call<JsonObject> call = apiInterface.deleteTodoGroup(groupID);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int newViewID = groupViewID;
                if (newViewID > 0)
                    newViewID --;


                // reload tasks fragment to update data
                if (activity != null)
                    ((MainActivity) activity).goToTasks(newViewID);

                // close pop-up
                if(activity != null)
                    activity.onBackPressed();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: deleteTodo");
            }

        });
    }

    // calendar
    public static void getCalendarGroups(LinkedList<CalendarGroup> calendarGroups, LinkedList<Boolean> calendarsChecked, FragmentTransaction ft) {

        Log.d(TAG, "getCalendarGroups: call");

        Call<List<CalendarGroup>> call = apiInterface.getCalendarGroups();
        call.enqueue(new Callback<List<CalendarGroup>>() {

            @Override
            public void onResponse(@NonNull Call<List<CalendarGroup>> call, @NonNull Response<List<CalendarGroup>> response) {

                if (response.body() != null) {

                    // new list for checked calendars
                    LinkedList<Boolean> newCalendarsChecked = new LinkedList<>();

                    // fill calendar group list
                    for (CalendarGroup group:response.body()) {
                        //Log.d(TAG, "GROUP NAME: " + group.getName());
                        calendarGroups.addLast(group);

                        // set new checked list
                        if (calendarsChecked.size() > 0) {
                            if (!calendarsChecked.getFirst())
                                newCalendarsChecked.addLast(false);
                            else
                                newCalendarsChecked.addLast(true);
                            calendarsChecked.removeFirst();
                        } else
                            newCalendarsChecked.addLast(true);
                    }
                    calendarsChecked.addAll(newCalendarsChecked);

                    // if any group was removed -> check all
                    if (calendarsChecked.size() != calendarGroups.size()) {
                        calendarsChecked.clear();
                        for (int i = 0; i < calendarGroups.size(); i++)
                            calendarsChecked.addLast(true);
                    }


                    /*int newSelectedGroup = selectedGroup;

                    // set active group to show
                    if (selectedGroup < 0) // first item
                        newSelectedGroup= todoGroups.size() - 1;*/

                    // send data to fragment & show it
                    //Bundle bundle = new Bundle();
                    //bundle.putInt("selected_group", newSelectedGroup);

                    Fragment fragment = new CalendarFragment();
                    //fragment.setArguments(bundle);

                    if (ft != null) {
                        ft.replace(R.id.nav_host_fragment_content_main, fragment, "events");
                        ft.commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CalendarGroup>> call, Throwable t) {
                Log.d(TAG, "onFailure: getCalendarGroups " + t.getLocalizedMessage());
            }
        });
    }


}
