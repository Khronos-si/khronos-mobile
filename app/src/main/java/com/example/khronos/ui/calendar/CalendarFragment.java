package com.example.khronos.ui.calendar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.databinding.FragmentCalendarBinding;
import com.example.khronos.structures.CalendarEvent;
import com.example.khronos.structures.CalendarGroup;
import com.example.khronos.ui.tasks.TaskListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static String TAG = "CalendarFragment";

    private CalendarViewModel calendarViewModel;
    private FragmentCalendarBinding binding;

    // event list view (bottom)
    private RecyclerView mRecyclerView;
    private EventListAdapter mAdapter;

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

        // colors

        Map<CalendarEvent, String> eventColors = new HashMap<>();

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

            if (MainActivity.calendarsChecked.get(i)) {
                subMenu.add(2, i, subMenu.NONE, group.getName()).setIcon(circle);

                CalendarEvent[] calendarEvents = group.getEvents();
                for (CalendarEvent cevent : calendarEvents) {
                    eventColors.put(cevent, group.getColor());
                }
            } else
                subMenu.add(2, i, subMenu.NONE, group.getName()).setIcon(ring);
            i++;



        }

        // modify toolbar
        //tb.setVisibility(View.VISIBLE);
        //tb.setTitle(group.getName());
        //tb.setBackgroundColor(Color.parseColor(group.getColor()));

        LinkedList<DayOfWeek> daysOfWeek = new LinkedList<>();
        daysOfWeek.add(DayOfWeek.MONDAY);
        daysOfWeek.add(DayOfWeek.TUESDAY);
        daysOfWeek.add(DayOfWeek.WEDNESDAY);
        daysOfWeek.add(DayOfWeek.THURSDAY);
        daysOfWeek.add(DayOfWeek.FRIDAY);
        daysOfWeek.add(DayOfWeek.SATURDAY);
        daysOfWeek.add(DayOfWeek.SUNDAY);


        CalendarView calendarView = binding.calendarView;
        YearMonth currMonth = YearMonth.now();
        YearMonth firstMonth = currMonth.minusMonths(10);
        YearMonth lastMonth = currMonth.plusMonths(10);

        DayOfWeek firstDayOfWeek = daysOfWeek.getFirst();
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currMonth);

        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {

            LocalDate selectedDate = LocalDate.now();

            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                DayViewContainer container = new DayViewContainer(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (container.day.getOwner() == DayOwner.THIS_MONTH) {
                            LocalDate currentSel = selectedDate;

                            selectedDate = container.day.getDate();
                            // reload
                            calendarView.notifyDateChanged(container.day.getDate());
                            if (currentSel != null)
                                // reload
                                calendarView.notifyDateChanged(currentSel);

                            // event viewer (bottom)
                            // Get a handle to the RecyclerView.
                            mRecyclerView = binding.eventsListView;
                            // Create an adapter and supply the data to be displayed.
                            mAdapter = new EventListAdapter(binding.getRoot().getContext(), container.events, container.colorevents);
                            // Connect the adapter with the RecyclerView.
                            mRecyclerView.setAdapter(mAdapter);
                            // Give the RecyclerView a default layout manager.
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                            mRecyclerView.setNestedScrollingEnabled(true);


                        }
                    }
                });
                return container;
            }

            @Override
            public void bind(@NonNull DayViewContainer dayViewContainer, @NonNull CalendarDay calendarDay) {
                int dayOfMonth = calendarDay.getDate().getDayOfMonth();
                String str = Integer.toString(dayOfMonth);
                dayViewContainer.textView.setText(str);
                dayViewContainer.day = calendarDay;

                // set color for in - out dates text
                if (calendarDay.getOwner() == DayOwner.THIS_MONTH)
                    dayViewContainer.textView.setTextColor(Color.WHITE);
                else
                    dayViewContainer.textView.setTextColor(Color.GRAY);


                // highlight selected date
                if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {

                    dayViewContainer.textView.setVisibility(View.VISIBLE);

                    if (calendarDay.getDate().equals(selectedDate)) {
                        dayViewContainer.textView.setTextColor(Color.WHITE);
                        Log.d(TAG, "bind: selected date: " + selectedDate);

                        // cell when selected
                        dayViewContainer.ozadje.setBackgroundResource(R.drawable.calendar_item);


                    } else if (calendarDay.getDate().equals(LocalDate.now())) {
                        dayViewContainer.ozadje.setBackground(null);
                    } else {
                        dayViewContainer.textView.setTextColor(Color.parseColor("#BBBBBB"));

                        // cell when not selected
                        dayViewContainer.ozadje.setBackgroundResource(R.drawable.calendar_item_dark);
                    }

                } else {
                    dayViewContainer.textView.setTextColor(Color.parseColor("#444444"));
                }


                // events
                LinkedList<String> colorevents = new LinkedList<>();
                LinkedList<CalendarEvent> idevents = new LinkedList<>();

                for (Map.Entry<CalendarEvent, String> cevent : eventColors.entrySet()) {

                    //Log.d(TAG, "bind: CEVENT: " + cevent.getKey().getStart().split("T")[0] + ", CALENDAR DAY: " + calendarDay.getDate());

                    for (String dateInEvent : cevent.getKey().getDates()) {
                        Log.d(TAG, "bind: dateInEvent " + dateInEvent);

                        //if date of event in map is same as date in cell
                        if (dateInEvent.equals(calendarDay.getDate().toString())) {
                            colorevents.addLast(cevent.getValue());
                            idevents.addLast(cevent.getKey());
                            Log.d(TAG, "bind: ADDING EVENT " + cevent.getKey().getStart());
                        }
                    }
                }

                dayViewContainer.colorevents = colorevents;
                dayViewContainer.events = idevents;

                // setup recyclerView for event decorations
                RecyclerView mDecorationsRecyclerView;
                EventDecorationsListAdapter mDecorationsAdapter;

                // Get a handle to the RecyclerView.
                mDecorationsRecyclerView = dayViewContainer.eventDecorationsRecyclerView;
                // Create an adapter and supply the data to be displayed.
                mDecorationsAdapter = new EventDecorationsListAdapter(binding.getRoot().getContext(), colorevents);
                // Connect the adapter with the RecyclerView.
                mDecorationsRecyclerView.setAdapter(mDecorationsAdapter);
                // Give the RecyclerView a default layout manager.
                mDecorationsRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                // fix scrolling and click-through
                mDecorationsRecyclerView.suppressLayout(true);
            }
        });

        // header binder
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<ViewContainer>() {
            @NonNull
            @Override
            public ViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull ViewContainer viewContainer, @NonNull CalendarMonth calendarMonth) {
                TextView text = viewContainer.getView().findViewById(R.id.headerTextView);
                text.setText(calendarMonth.getYearMonth().getMonth().name() + " " + calendarMonth.getYear());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // remove submenu for groups
        if (subMenu != null)
            subMenu.clear();

        binding = null;
    }
}