package com.agobal.KnyguKeitykla.Entities;

public class UserData {
    public String userName, email, firstName, lastName, cityName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public UserData()
    {

    }

    /*
    public UserData(String userName, String FirstName, String LastName, String email, String CityName)
    {
        this.userName=userName;
        this.firstName=FirstName;
        this.lastName=LastName;
        this.email=email;
        this.cityName=CityName;
    }
    */

}

