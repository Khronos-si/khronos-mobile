package com.example.khronos.api;

import com.example.khronos.structures.CalendarGroup;
import com.example.khronos.structures.Todo;
import com.example.khronos.structures.TodoGroup;
import com.example.khronos.structures.User;
import com.example.khronos.structures.Login;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    // USER
    @Headers("Content-Type: application/json")
    @POST("user/login")
    Call<User> login(@Body Login body);

    @Headers("Content-Type: application/json")
    @GET("user/refresh-token")
    Call<Void> refreshToken();

    @Headers("Content-Type: application/json")
    @POST("user/logout")
    Call<Void> logout();

    // TODOS
    @Headers("Content-Type: application/json")
    @POST("todo")
    Call<Todo> addTodo(@Body JsonObject todo);

    @Headers("Content-Type: application/json")
    @POST("todo/group")
    Call<TodoGroup> addTodoGroup(@Body JsonObject group);

    @Headers("Content-Type: application/json")
    @GET("todo/group")
    Call<List<TodoGroup>> getTodoGroups();

    @Headers("Content-Type: application/json")
    @GET("todo/{id}")
    Call<Todo> getTodo(@Path(value = "id") String id);

    @Headers("Content-Type: application/json")
    @GET("todo/group/{id}")
    Call<TodoGroup> getTodoGroup(@Path(value = "id") String id);

    @Headers("Content-Type: application/json")
    @PUT("todo/{id}")
    Call<Todo> editTodo(@Path(value = "id") String id, @Body JsonObject task);

    @Headers("Content-Type: application/json")
    @PUT("todo/group/{id}")
    Call<TodoGroup> editTodoGroup(@Path(value = "id") String id, @Body JsonObject group);

    @Headers("Content-Type: application/json")
    @DELETE("todo/{id}")
    Call<JsonObject> deleteTodo(@Path (value = "id") String id);

    @Headers("Content-Type: application/json")
    @DELETE("todo/group/{id}")
    Call<JsonObject> deleteTodoGroup(@Path (value = "id") String id);

    // EVENTS
    @Headers("Content-Type: application/json")
    @GET("event")
    Call<List<CalendarGroup>> getCalendarGroups();




}


