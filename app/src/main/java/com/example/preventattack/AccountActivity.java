package com.example.preventattack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

public class AccountActivity extends AppCompatActivity {


    private TextView firstName, lastName, email, emergencyPhone, emergencyEmail;
    private Map<String,String> userDetails;
    private static DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        emergencyPhone = findViewById(R.id.emergencyPhone);
        emergencyEmail = findViewById(R.id.emergencyEmail);

        if(helper==null){
            helper = new DatabaseHelper(getApplicationContext());
        }

        userDetails = helper.getUserDetails();
        firstName.setText(userDetails.get("firstName"));
        lastName.setText(userDetails.get("lastName"));
        email.setText(userDetails.get("email"));
        emergencyPhone.setText(userDetails.get("emergencyPhone"));
        emergencyEmail.setText(userDetails.get("emergencyEmail"));

        helper = null;
    }
}