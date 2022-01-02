package com.example.khronos.structures;

import com.google.gson.JsonObject;

public class CalendarEvent {
    private String _id;
    private String name;
    private User owner;
    private String description;
    private String start;
    private String end;
    private JsonObject dates;
    private Tag tag;
    private String [] sharedWith;

    public CalendarEvent(String _id, String name, User owner, String description, String start, String end, JsonObject dates, Tag tag, String[] sharedWith) {
        this._id = _id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.start = start;
        this.end = end;
        this.dates = dates;
        this.tag = tag;
        this.sharedWith = sharedWith;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public JsonObject getDates() {
        return dates;
    }

    public void setDates(JsonObject dates) {
        this.dates = dates;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String[] getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String[] sharedWith) {
        this.sharedWith = sharedWith;
    }
}
