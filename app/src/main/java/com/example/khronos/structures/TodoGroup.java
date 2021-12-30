package com.example.khronos.structures;

import android.util.Log;

import java.util.LinkedList;

public class TodoGroup {

    private String _id;
    private String name;
    private int permissions;
    private User[] sharedWith;
    private String color;
    private User owner;
    private LinkedList<Todo> todos;

    public TodoGroup(String _id, String name, int permissions, User[] sharedWith, String color, User owner, LinkedList<Todo> todos) {
        this._id = _id;
        this.name = name;
        this.permissions = permissions;
        this.sharedWith = sharedWith;
        this.color = color;
        this.owner = owner;
        this.todos = todos;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public User[] getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(User[] sharedWith) {
        this.sharedWith = sharedWith;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LinkedList<Todo> getTodos() {
        return todos;
    }

    public void setTodos(LinkedList<Todo> todos) {
        this.todos = todos;
    }

    public void logToString() {
        Log.d("TODO GROUP", "logToString: ID " + this._id);
        Log.d("TODO GROUP", "logToString: NAME " + this.name);
        Log.d("TODO GROUP", "logToString: PERMISSIONS " + this.permissions);
        Log.d("TODO GROUP", "logToString: COLOR " + this.color);
        Log.d("TODO GROUP", "logToString: SHARED WITH " + this.sharedWith.length);
    }
}
