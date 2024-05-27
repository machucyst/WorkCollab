package com.example.workcollab;

import android.net.Uri;

import java.util.Map;

public class DeadlineModel {
    private String groupId;
    private String groupName;
    private Uri image;
    private Map task;

    public DeadlineModel(String groupId, String groupName, Uri image, Map task) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.image = image;
        this.task = task;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Map getTask() {
        return task;
    }

    public void setTask(Map task) {
        this.task = task;
    }
}
