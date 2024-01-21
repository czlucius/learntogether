package com.sp.learntogether.ui.communities;

public class groupchatInfo {
    private String name;
    private String description;
    private String capacity;

    public groupchatInfo(String name, String description, String capacity) {
        this.name = name;
        this.description = description;
        this.capacity = capacity + "/20";
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

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
