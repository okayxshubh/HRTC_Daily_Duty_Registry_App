package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class LocationsPojo implements Serializable {
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return name;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
