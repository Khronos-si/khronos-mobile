package com.example.khronos.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.api.ApiClient;
import com.example.khronos.api.ApiInterface;
import com.example.khronos.structures.Login;
import com.example.khronos.structures.TodoGroup;
import com.example.khronos.structures.User;
import com.example.khronos.ui.login.LoginViewModel;
import com.example.khronos.ui.login.LoginViewModelFactory;
import com.example.khronos.databinding.ActivityLoginBinding;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    // api interface
    public static final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // get token
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = preferences.getString("token","");

        //if token exists
        if (token.length() > 0) {

            // set token & get new token (to verify it's correct)
            ApiClient.setSharedPreferences(getSharedPreferences("myPrefs", MODE_PRIVATE).edit());
            ApiClient.token = token;

            Log.d(TAG, "onCreate: TOKEN: " + token);
            Context context = this;
            
            Call<Void> call = apiInterface.refreshToken();
            call.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.code() == 200) {

                        // start main activity
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d(TAG, "onFailure: ni kul: " + t.getMessage());
                }
            });
        }


        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());

                    setResult(Activity.RESULT_OK);

                    //Complete and destroy login activity once successful
                    finish();
                }

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // do login
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), preferences);

                    // hide keyboard
                    final Activity activity = LoginActivity.this;
                    final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (activity.getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    loadingProgressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // do login
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), preferences);

                // hide keyboard
                final Activity activity = LoginActivity.this;
                final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (activity.getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                loadingProgressBar.setVisibility(View.VISIBLE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Login> call = apiInterface.register(new Login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), usernameEditText.getText().toString().split("@")[0]));
                call.enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        
                        // success
                        if (response.code() == 200) {
                            // do login
                            loginViewModel.login(usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString(), preferences);

                            // hide keyboard
                            final Activity activity = LoginActivity.this;
                            final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (activity.getCurrentFocus() != null)
                                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            loadingProgressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        Log.d(TAG, "onFailure: Register failed");
                    }
                });
                
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience

        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        // start main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}