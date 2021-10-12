package com.example.preventattack;

public class UserDetails {
    private String firstName, lastName, emailID, emergencyPhone, emergencyEmailID, hashedPin;

    public UserDetails(String firstName, String lastName, String emailID, String emergencyPhone, String emergencyEmailID, String hashedPin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.emergencyPhone = emergencyPhone;
        this.emergencyEmailID = emergencyEmailID;
        this.hashedPin = hashedPin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public String getHashedPin(){
        return this.hashedPin;
    }

    public String getEmergencyEmailID() {
        return emergencyEmailID;
    }
}
