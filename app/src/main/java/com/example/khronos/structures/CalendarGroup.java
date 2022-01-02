package com.example.khronos.structures;

public class CalendarGroup {

    private String _id;
    private String name;
    private String color;
    private CalendarEvent[] events;

    public CalendarGroup(String _id, String name, String color, CalendarEvent[] events) {
        this._id = _id;
        this.name = name;
        this.color = color;
        this.events = events;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public CalendarEvent[] getEvents() {
        return events;
    }

    public void setEvents(CalendarEvent[] events) {
        this.events = events;
    }
}
