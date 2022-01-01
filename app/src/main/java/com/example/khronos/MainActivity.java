package com.example.khronos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.khronos.api.ApiCalls;
import com.example.khronos.api.ApiClient;
import com.example.khronos.api.ApiInterface;
import com.example.khronos.databinding.ActivityMainBinding;
import com.example.khronos.structures.Login;
import com.example.khronos.structures.TodoGroup;
import com.example.khronos.structures.User;
import com.example.khronos.ui.calendar.CalendarFragment;
import com.example.khronos.ui.home.HomeFragment;
import com.example.khronos.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // current fragment
    public static String activeFragment = "";

    // api interface
    public static final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    // colors
    public static ArrayList<String> colors;

    // todo list
    public static LinkedList<TodoGroup> todoGroups;

    // preferences
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get token
        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = preferences.getString("token","");

        // set api client and token
        ApiClient.getClient().create(ApiInterface.class);
        ApiClient.setSharedPreferences(getSharedPreferences("myPrefs", MODE_PRIVATE).edit());
        ApiClient.token = token;

        Log.d(TAG, "onCreate: MAIN ACTIVITY TOKEN: " + token);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set action bar
        setSupportActionBar(binding.appBarMain.toolbar);

        // init task groups list
        todoGroups = new LinkedList<>();

        // init colors
        colors = new ArrayList<>();
        fillColors(colors);

        DrawerLayout drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_tasks, R.id.nav_calendar)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);

    }

    private void fillColors(ArrayList<String> colors) {
        colors.add("#7367F0".toUpperCase());
        colors.add("#6EC193".toUpperCase());
        colors.add("#53AFBE".toUpperCase());
        colors.add("#FEB449".toUpperCase());
        colors.add("#FE5C36".toUpperCase());
        colors.add("#739BAA".toUpperCase());
        colors.add("#F5C89F".toUpperCase());
        colors.add("#8EBFB5".toUpperCase());
        colors.add("#FEA6B0".toUpperCase());
        colors.add("#95B2D1".toUpperCase());
        colors.add("#42A48D".toUpperCase());
        colors.add("#86415E".toUpperCase());
        colors.add("#BC1654".toUpperCase());
        colors.add("#F53435".toUpperCase());
        colors.add("#FBF37C".toUpperCase());
        colors.add("#7F7F7F".toUpperCase());
        colors.add("#58555A".toUpperCase());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ali je glavni menu
        if (item.getGroupId() == 0)
            switch (item.getItemId()) {

                // HOME
                case R.id.nav_home:
                    goToHome();
                    break;

                // TASKS
                case R.id.nav_tasks:
                    goToTasks(0);
                    break;

                // CALENDAR
                case R.id.nav_calendar:
                    goToCalendar(0);
            }
        else // je submenu
            goToTasks(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // set credentials in navbar
        String username = preferences.getString("username", "");
        TextView userView =  binding.navView.findViewById(R.id.nameSurname);
        userView.setText(username);
        String mail = preferences.getString("mail", "");
        TextView mailView =  binding.navView.findViewById(R.id.email);
        mailView.setText(mail);

        Context context = this;

        MenuItem logOut = binding.appBarMain.toolbar.getMenu().getItem(0);
        logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                
                Call<Void> call = apiInterface.logout();
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        Log.d(TAG, "onResponse: RESPONSE: " + response.code());
                        
                        // go to login page
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);

                        Log.d(TAG, "onResponse: LOGOUT SUCCESSFUL");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(TAG, "onFailure: failed to logout" + t.getMessage());
                    }
                });

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void goToHome() {

        Log.d(TAG, "goToHome: clicked home in nav");

        // change fragment
        Fragment fragment = new HomeFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment_content_main, fragment, "home");

        getSupportActionBar().setTitle("Home");
        ft.commit();
    }

    public void goToTasks(int item) {

        // re-init linked list
        todoGroups = new LinkedList<>();

        // get data from api, change fragment in response
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Log.d(TAG, "goToTasks: item=" + item);
        // get all groups
        ApiCalls.getTodoGroups(todoGroups, item, ft);

        getSupportActionBar().setTitle("Tasks");
    }

    public void goToCalendar(int item) {

        Log.d(TAG, "goToCalendar: clicked tasks in nav");


        Fragment fragment = new CalendarFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment_content_main, fragment, "calendar");

        getSupportActionBar().setTitle("Calendar");
        ft.commit();
    }

    public void showPopUp(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim);
        ft.add(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void goBack(View view) {
        this.onBackPressed();
    }
}