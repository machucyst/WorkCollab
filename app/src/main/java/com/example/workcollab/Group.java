package com.example.workcollab;

import java.util.List;

public class Group {
    private final String groupName;
    private final String code;
    private final List<User> members;
    private final List<User> invites;

    public Group(String groupName, String code, List<User> members,List<User> invites){
        this.groupName = groupName;
        this.code = code;
        this.members = members;
        this.invites = invites;
    }
    public List<User> getMembers() {
        return members;
    }
    public String getCode() {
        return code;
    }
    public String getGroupName() {
        return groupName;
    }

    public List<User> getInvites() {
        return invites;
    }
}
