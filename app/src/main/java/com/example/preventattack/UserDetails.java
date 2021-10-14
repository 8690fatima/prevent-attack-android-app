package com.example.preventattack;

public class UserDetails {
    private final String firstName, lastName, emailID, emergencyPhone, emergencyEmailID, hashedPin, securityQuestion, securityAnswer;

    public UserDetails(String firstName, String lastName, String emailID, String emergencyPhone, String emergencyEmailID, String hashedPin, String securityQuestion, String securityAnswer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.emergencyPhone = emergencyPhone;
        this.emergencyEmailID = emergencyEmailID;
        this.hashedPin = hashedPin;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
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

    public String getSecurityQuestion(){
        return securityQuestion;
    }

    public String getSecurityAnswer(){
        return securityAnswer;
    }
}
