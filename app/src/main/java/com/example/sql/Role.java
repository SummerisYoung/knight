package com.example.sql;

import cn.bmob.v3.BmobObject;

public class Role extends BmobObject {
    private String name;
    private String description;
    private Map map;

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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
