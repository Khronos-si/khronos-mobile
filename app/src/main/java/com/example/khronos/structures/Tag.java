package com.example.khronos.structures;

public class Tag {

    private String _id;
    private String name;
    private String color;

    public Tag(String _id, String name, String color) {
        this._id = _id;
        this.name = name;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
