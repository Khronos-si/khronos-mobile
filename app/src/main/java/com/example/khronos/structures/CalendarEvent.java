package com.example.khronos.structures;

import android.util.Log;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CalendarEvent {

    private static final String TAG = "CalendarEvent";

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

    public String [] getDates() {

        LinkedList<String> dates = new LinkedList<>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(String.valueOf(LocalDate.parse(start.split("T")[0])));
            date2 = df1 .parse(String.valueOf(LocalDate.parse(end.split("T")[0])));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            String frmt = new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime().getTime());
            dates.add(frmt);
            Log.d(TAG, "getDates: " + frmt);
            cal1.add(Calendar.DATE, 1);
        }

        String [] tab;
        tab = dates.toArray(new String[dates.size()]);
        Log.d(TAG, "getDates: tab size: " + tab.length);


        return tab;
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
