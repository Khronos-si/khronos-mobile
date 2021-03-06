package com.example.khronos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.khronos.api.ApiCalls;
import com.example.khronos.api.ApiClient;
import com.example.khronos.api.ApiInterface;
import com.example.khronos.databinding.ActivityMainBinding;
import com.example.khronos.structures.CalendarGroup;
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

import java.io.ByteArrayOutputStream;
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

    // context
    Context context = this;

    // api interface
    public static final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    // colors
    public static ArrayList<String> colors;

    // todo list
    public static LinkedList<TodoGroup> todoGroups;

    // event list
    public static LinkedList<CalendarGroup> calendarGroups;
    // checked calendars
    public static LinkedList<Boolean> calendarsChecked;

    // preferences
    SharedPreferences preferences;

    // return to login listener
    public static MutableLiveData<Boolean> returnToLogin = new MutableLiveData<>();

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

        // init calendars list
        calendarGroups = new LinkedList<>();
        calendarsChecked = new LinkedList<>();

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

        // check if token expired -> return to login
        returnToLogin.setValue(false);
        returnToLogin.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    // display message
                    Toast.makeText(getApplicationContext(), "Token expired, need to relogin.", Toast.LENGTH_LONG).show();

                    // start login activity
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ali je glavni menu
        if (item.getGroupId() == 0) {
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
                    goToCalendar();
            }
            // close drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        // je task submenu
        else if (item.getGroupId() == 1) {
            goToTasks(item.getItemId());
            Log.d(TAG, "onNavigationItemSelected: taskSubmenu");

            // close drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        // je calendar submenu
        else if (item.getGroupId() == 2) {

            calendarsChecked.set(item.getItemId(), !calendarsChecked.get(item.getItemId()));
            goToCalendar();
        }

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
        ImageView avatarPic = binding.navView.findViewById(R.id.avatarImage);

        String avatar = preferences.getString("avatar", "");
        Log.d(TAG, "onCreateOptionsMenu: avatar: " + avatar);
        //decode base64 string to image
        byte [] imageBytes = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        avatarPic.setImageBitmap(decodedImage);

        MenuItem logOut = binding.appBarMain.toolbar.getMenu().getItem(0);
        logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                
                Call<Void> call = apiInterface.logout();
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        // display message
                        Toast.makeText(getApplicationContext(), "Goodbye :(", Toast.LENGTH_LONG).show();
                        
                        // go to login page
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);

                        //Log.d(TAG, "onResponse: LOGOUT SUCCESSFUL");
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

        // get data from api
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Log.d(TAG, "goToTasks: item=" + item);

        // get all groups and change fragment
        ApiCalls.getTodoGroups(todoGroups, item, ft);

        getSupportActionBar().setTitle("Tasks");
    }

    public void goToCalendar() {

        // re-init linked list
        calendarGroups = new LinkedList<>();

        // get data from api, change fragment in response
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Log.d(TAG, "goToCalendar:");

        // get all groups and change fragment
        ApiCalls.getCalendarGroups(calendarGroups, calendarsChecked, ft);

        getSupportActionBar().setTitle("Calendar");
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
}