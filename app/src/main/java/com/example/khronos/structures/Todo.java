package com.example.khronos.structures;

import android.util.Log;

import com.example.khronos.structures.Tag;

public class Todo {

    private String _id;
    private String name;
    private String description;
    private boolean status;
    private Tag[] tags;

    public Todo(String _id, String name, String description, boolean status, Tag[] tags) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.tags = tags;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    public void logToString() {
        Log.d("TODO", "logToString: ID " + this._id);
        Log.d("TODO", "logToString: NAME " + this.name);
        Log.d("TODO", "logToString: DESC " + this.description);
        Log.d("TODO", "logToString: STATUS " + this.status);
        // Log.d("TODO", "logToString: TAGS " + this.tags);
    }
}
