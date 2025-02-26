package com.example.workcollab;

import android.net.Uri;

import java.util.Map;

public class DeadlineModel {
    private String groupId, groupName;
    private Uri image;
    private Map<String,Object> task;

    public DeadlineModel(String groupId, String groupName, Uri image, Map<String,Object> task) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.image = image;
        this.task = task;
    }
    public String getGroupName() {
        return groupName;
    }

    public Uri getImage() {
        return image;
    }

    public Map<String,Object> getTask() {
        return task;
    }

    public void setTask(Map<String,Object> task) {
        this.task = task;
    }
}
