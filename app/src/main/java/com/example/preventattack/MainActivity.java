package com.example.preventattack;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    private static DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);

        if(helper==null) {
            helper = new DatabaseHelper(MainActivity.this);
        }

        //helper.unRegister();

        if(helper.didUserRegister()) {
            Intent intent = new Intent();
            intent.setClass(this,HomeActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent();
            intent.setClass(this,Registration.class);
            startActivity(intent);
        }
        finish();
    }
}