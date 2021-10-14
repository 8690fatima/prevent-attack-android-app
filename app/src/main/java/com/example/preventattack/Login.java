package com.example.preventattack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

    private EditText loginDigit1,loginDigit2,loginDigit3,loginDigit4;
    private TextView forgotPassword;
    private int[] loginPin = new int[4];
    private int loginPinCount;
    private static DatabaseHelper helper;
    private String securityAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginPinCount = 0;

        if(helper==null) {
            helper = new DatabaseHelper(getApplicationContext());
        }

        initializeLogin();
        loginMethod();
    }

    private void initializeLogin(){

        loginDigit1 = findViewById(R.id.passDigit1);
        loginDigit2 = findViewById(R.id.passDigit2);
        loginDigit3 = findViewById(R.id.passDigit3);
        loginDigit4 = findViewById(R.id.passDigit4);

        forgotPassword = findViewById(R.id.forgotPassword);

        loginDigit1.requestFocus();
    }

    private void loginMethod(){

        loginDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==0){
                    loginPinCount--;
                    loginPin[0] = -1;
                }
                else if (s.length() == 1) {
                    loginPinCount++;
                    loginPin[0] = Integer.parseInt(s.toString());
                    loginDigit2.requestFocus();
                }
                checkLoginDetails();
            }
        });

        loginDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    loginPinCount--;
                    loginPin[1] = -1;
                }
                else if(s.length()==1){
                    loginPinCount++;
                    loginPin[1] = Integer.parseInt(s.toString());
                    loginDigit3.requestFocus();
                }
                checkLoginDetails();
            }
        });

        loginDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    loginPinCount--;
                    loginPin[2] = -1;
                }
                else if(s.length()==1){
                    loginPinCount++;
                    loginPin[2] = Integer.parseInt(s.toString());
                    loginDigit4.requestFocus();
                }
                checkLoginDetails();
            }
        });

        loginDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    loginPinCount--;
                    loginPin[3] = -1;
                }
                else if(s.length()==1){
                    loginPinCount++;
                    loginPin[3] = Integer.parseInt(s.toString());
                }
                checkLoginDetails();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent();
                settingsIntent.putExtra("forgotPassword", true);
                settingsIntent.setClass(Login.this, Settings.class);
                startActivity(settingsIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void checkLoginDetails(){
        if(loginPinCount==4){
            String enteredPin = ""+loginPin[0]+loginPin[1]+loginPin[2]+loginPin[3];
            if(helper.verifyPassword(enteredPin)){
                setResult(RESULT_OK);
                helper = null;
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"❌ INVALID PIN!",Toast.LENGTH_LONG).show();
                loginDigit1.getText().clear();
                loginDigit2.getText().clear();
                loginDigit3.getText().clear();
                loginDigit4.getText().clear();
                loginDigit1.requestFocus();
            }
        }
    }

}