package com.example.workcollab;

import android.net.Uri;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String id;
    private String message;
    private String senderId;
    private String senderUsername;
    private String groupId;
    private Uri file;
    private String fileType;
    private Timestamp timestamp;

    public Message(String id, String message, String senderId, String senderUsername, String groupId, Uri file, String fileType, Timestamp timestamp) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.groupId = groupId;
        this.file = file;
        this.fileType = fileType;
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("senderId", senderId);
        messageMap.put("groupId", groupId);
        messageMap.put("file", file);
        messageMap.put("fileType", fileType);
        messageMap.put("timestamp", timestamp);
        messageMap.put("senderUsername", senderUsername);
        return messageMap;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
