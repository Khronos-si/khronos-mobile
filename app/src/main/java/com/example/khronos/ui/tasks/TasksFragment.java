package com.example.khronos.ui.tasks;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.api.ApiCalls;
import com.example.khronos.api.ApiClient;
import com.example.khronos.api.ApiInterface;
import com.example.khronos.structures.TodoGroup;
import com.example.khronos.databinding.FragmentTasksBinding;
import com.example.khronos.structures.User;
import com.google.android.material.navigation.NavigationView;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.ExpandableFabLayout;
import com.nambimobile.widgets.efab.FabOption;

import java.util.ArrayList;
import java.util.LinkedList;

public class TasksFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TasksFragment";
    private TasksViewModel tasksViewModel;
    private FragmentTasksBinding binding;

    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    // side menu
    private NavigationView navigationView;
    private Menu m;
    private SubMenu subMenu;
    
    // task list
    private LinkedList<TodoGroup> todoGroups;
    private TodoGroup group;
    int selected_group;

    // email list for suggestions
    private ArrayList<String> sharedUsersMail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        todoGroups = MainActivity.todoGroups;

        ImageButton editGroupButton = root.findViewById(R.id.editGroup);
        editGroupButton.setOnClickListener(this);

        // set active fragment
        MainActivity.activeFragment = "tasks";

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar tb = view.findViewById(R.id.groupToolbar);

        // if groups are empty
        if (todoGroups.size() == 0) {
            // dont show toolbar
            tb.setVisibility(View.INVISIBLE);

            // set selected group to -1
            selected_group = -1;

        } else {

            // get group to display
            selected_group = getArguments().getInt("selected_group");
            group = todoGroups.get(selected_group);

            // add submenu for groups
            navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
            m = navigationView.getMenu();

            subMenu = m.addSubMenu("Task groups");
            int i = 0;

            // add items to submenu
            for (TodoGroup group:todoGroups) {
                // set color of icon
                Drawable circle = getResources().getDrawable(R.drawable.ic_color_circle).getConstantState().newDrawable().mutate();
                circle.setTint(Color.parseColor(group.getColor()));
                if (i == selected_group)
                    subMenu.add(1, i, subMenu.NONE, group.getName()).setIcon(circle).setChecked(true);
                else
                    subMenu.add(1, i, subMenu.NONE, group.getName()).setIcon(circle).setChecked(false);
                i++;
            }

            // menu set checked items
            subMenu.setGroupCheckable(1, true, true);
            m.getItem(1).setChecked(true);

            // modify toolbar
            tb.setVisibility(View.VISIBLE);
            tb.setTitle(group.getName());
            tb.setBackgroundColor(Color.parseColor(group.getColor()));

            // Get a handle to the RecyclerView.
            mRecyclerView = binding.tasksRecyclerView;
            // Create an adapter and supply the data to be displayed.
            mAdapter = new TaskListAdapter(binding.getRoot().getContext(), group.getTodos(), selected_group);
            // Connect the adapter with the RecyclerView.
            mRecyclerView.setAdapter(mAdapter);
            // Give the RecyclerView a default layout manager.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        }


        // set FAB button options
        ExpandableFab eFab = binding.expandableFab;
        eFab.setFirstFabOptionMarginPx(100);
        FabOption fabAddTodo = binding.expandableFabAddtodo;
        fabAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selected_group >= 0) {
                    // create add task pop-up
                    DialogFragment fragment = EditTodo.newInstance(group.getId(), selected_group, "new");

                    MainActivity main = (MainActivity) view.getContext();
                    main.showPopUp(R.id.nav_host_fragment_content_main, fragment);
                } else {

                    // show message
                    Toast.makeText(getContext(), "Create Todo Group first", Toast.LENGTH_LONG).show();
                }


            }
        });

        FabOption fabAddGroup = binding.expandableFabAddgroup;
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fill mails list
                getAllGroupsSharedUsers();

                // create add group pop-up
                DialogFragment fragment = EditGroup.newInstance(null, sharedUsersMail, selected_group, "new");

                MainActivity main = (MainActivity) view.getContext();
                main.showPopUp(R.id.nav_host_fragment_content_main, fragment);
            }
        });

        FabOption fabAddTag = binding.expandableFabAddtag;
    }


    // on click edit group
    @Override
    public void onClick(View view) {

        // fill mails list
        getAllGroupsSharedUsers();

        // create edit group pop-up
        DialogFragment fragment = EditGroup.newInstance(group.getId(), sharedUsersMail, selected_group, "edit");

        MainActivity main = (MainActivity) getActivity();
        main.showPopUp(R.id.nav_host_fragment_content_main, fragment);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remove submenu for task groups
        if (subMenu != null)
            subMenu.clear();

        binding = null;
    }

    public void getAllGroupsSharedUsers() {

        // init list
        sharedUsersMail = new ArrayList<>();

        // fill with all emails from all groups
        for (TodoGroup currGroup : todoGroups) {
            for (User currUser : currGroup.getSharedWith()) {
                sharedUsersMail.add(currUser.getEmail());
            }
        }
    }
}