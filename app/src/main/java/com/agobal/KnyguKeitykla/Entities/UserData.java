package com.agobal.KnyguKeitykla.Entities;

public class UserData {

    private String email;
    private String firstName;
    private String lastName;
    private String cityName;

    public UserData(){}

    UserData(String email, String firstName, String lastName, String cityName){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cityName = cityName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }



    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public String getCityName() {
        return cityName;
    }
}
