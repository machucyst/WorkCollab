package com.example.workcollab;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private int age;
    private String contactNumber;
    private byte[] profile;
    private List<Integer> productBookmarksIds;
    private List<Group> groups;

    public byte[] getProfile() {
        return profile;
    }

    public void setProfile(byte[] image) {
        this.profile = profile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }
    public List<Group> getGroups(){
        return groups;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String firstName, String lastName, String middleInitial, int age, String contactNumber, byte[] profile, String address,List<Group>groups) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.age = age;
        this.contactNumber = contactNumber;
        this.profile = profile;
        this.address = address;
        this.groups = groups;
    }
} 
