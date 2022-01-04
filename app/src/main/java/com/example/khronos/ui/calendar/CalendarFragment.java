package com.example.khronos.ui.calendar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.databinding.FragmentCalendarBinding;
import com.example.khronos.structures.CalendarGroup;
import com.example.khronos.structures.TodoGroup;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.LinkedList;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private FragmentCalendarBinding binding;

    // side menu
    private NavigationView navigationView;
    private Menu m;
    private SubMenu subMenu;

    // calendar list
    private LinkedList<CalendarGroup> calendarGroups;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarGroups = MainActivity.calendarGroups;

        //final TextView textView = binding.textSlideshow;
        //calendarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});

        // set active fragment
        MainActivity.activeFragment = "calendar";

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // add submenu for groups
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        m = navigationView.getMenu();

        subMenu = m.addSubMenu("Calendar groups");
        int i = 0;

        // add items to submenu
        for (CalendarGroup group:calendarGroups) {
            // set color of icon
            Drawable circle = getResources().getDrawable(R.drawable.ic_color_circle).getConstantState().newDrawable().mutate();
            Drawable ring = getResources().getDrawable(R.drawable.ic_outline_circle).getConstantState().newDrawable().mutate();
            circle.setTint(Color.parseColor(group.getColor()));
            circle.setAlpha(254); // so we can diff circle and ring
            ring.setTint(Color.parseColor(group.getColor()));

            if (MainActivity.calendarsChecked.get(i))
                subMenu.add(2, i, subMenu.NONE, group.getName()).setIcon(circle);
            else
                subMenu.add(2, i, subMenu.NONE, group.getName()).setIcon(ring);
            i++;
        }

        // modify toolbar
        //tb.setVisibility(View.VISIBLE);
        //tb.setTitle(group.getName());
        //tb.setBackgroundColor(Color.parseColor(group.getColor()));

        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remove submenu for task groups
        if (subMenu != null)
            subMenu.clear();

        binding = null;
    }
}