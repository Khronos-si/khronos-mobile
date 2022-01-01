package com.example.khronos.ui.login;

import static android.content.Context.MODE_PRIVATE;
import static com.example.khronos.MainActivity.apiInterface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;


import com.example.khronos.R;
import com.example.khronos.api.ApiCalls;
import com.example.khronos.api.ApiClient;
import com.example.khronos.api.ApiInterface;
import com.example.khronos.structures.Login;
import com.example.khronos.structures.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    String TAG = "LoginViewModel";

    // api interface
    public final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    String getToken() {return ApiClient.token;}

    public void login(String username, String password, SharedPreferences preferences) {

        // can be launched in a separate asynchronous job

        Log.d(TAG, "login: USERNAME " + username);
        Log.d(TAG, "login: PASSWORD " + password);

        Call<User> call = apiInterface.login(new Login(username, password));
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                // success
                if (response.code() == 200) {
                    ApiClient.token = response.headers().get("auth-token");
                    Log.d(TAG, "Login - onResponse:" + response.body());
                    Log.d(TAG, "TOKEN: " + ApiClient.token);
                    assert response.body() != null;

                    // save token & credentials
                    preferences.edit().putString("token", ApiClient.token)
                                      .putString("username", response.body().getUser())
                                      .putString("mail", username).apply();

                    // set login status
                    loginResult.setValue(new LoginResult(new LoggedInUserView(response.body().getUser())));

                } else {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Login - onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null;
    }
}