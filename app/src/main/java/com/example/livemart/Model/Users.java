package com.example.livemart.Model;

public class Users {
    private String name, phone, password, address, user, dob;

    public Users()
    {

    }

    public Users(String name, String phone, String password, String image, String address, String user, String dob) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.user = user;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDob() { return dob; }

    public void setDob(String dob) {this.dob = dob; }
}
