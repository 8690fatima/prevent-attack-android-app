package com.example.preventattack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Registration extends AppCompatActivity {

    private EditText firstName, lastName, emailID, emergencyPhone, emergencyEmailID, pin, rePin;
    private Button btnRegister, btnGetContact;
    private static boolean isSuccess;
    private static DatabaseHelper helper;
    private static boolean validFirstName,
            validLastName,
            validEmail,
            validEmergencyPhone,
            validEmergencyEmailID,
            validPin;
    private boolean validContactPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        isSuccess = validFirstName = validLastName = validEmail = validEmergencyPhone = validEmergencyEmailID = validPin = validContactPermission =  false;

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(helper==null) {
            helper = new DatabaseHelper(getApplicationContext());
        }

        initializeRegistration();
        registrationMethod();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            validContactPermission = true;
        }else{
            validContactPermission = false;
        }
    }

    private void initializeRegistration(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailID = findViewById(R.id.email);
        pin = findViewById(R.id.pin);
        rePin = findViewById(R.id.rePin);
        emergencyPhone = findViewById(R.id.emergencyPhone);
        emergencyEmailID = findViewById(R.id.emergencyEmail);
        btnRegister = findViewById(R.id.btnRegister);
        btnGetContact = findViewById(R.id.getContact);

        firstName.requestFocus();
    }

    private void registrationMethod(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().matches("[A-Za-z]+")) {
                            validFirstName = true;
                            checkRegistrationDetails();
                            firstName.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validFirstName = false;
                            checkRegistrationDetails();
                            firstName.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                    }
                });
            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().matches("[A-Za-z]+")) {
                            validLastName = true;
                            checkRegistrationDetails();
                            lastName.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validLastName = false;
                            checkRegistrationDetails();
                            lastName.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                    }
                });
            }
        });

        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().matches("^[A-Za-z0-9][A-Za-z0-9.+_-]+@[A-Za-z]+[.][A-Za-z][A-Za-z.]+[A-Za-z]$")) {
                            validEmail = true;
                            checkRegistrationDetails();
                            emailID.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validEmail = false;
                            checkRegistrationDetails();
                            emailID.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                    }
                });
            }
        });

        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() == 4) {
                            validPin = true;
                            pin.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validPin = false;
                            rePin.getText().clear();
                            pin.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                        checkRegistrationDetails();
                    }
                });
            }
        });

        rePin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pin.getText().length() != 4) {
                            rePin.getText().clear();
                            pin.requestFocus();
                        } else if (s.length() == 4 & pin.getText().toString().equals(s.toString())) {
                            validPin = true;
                            rePin.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validPin = false;
                            rePin.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                        checkRegistrationDetails();
                    }
                });
            }
        });

        emergencyPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().matches("[0-9]{10}")) {
                            validEmergencyPhone = true;
                            emergencyPhone.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validEmergencyPhone = false;
                            emergencyPhone.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                        checkRegistrationDetails();
                    }
                });
            }
        });

        emergencyEmailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().matches("^[A-Za-z0-9][A-Za-z0-9.+_-]+@[A-Za-z]+[.][A-Za-z][A-Za-z.]+[A-Za-z]$")) {
                            validEmergencyEmailID = true;
                            emergencyEmailID.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            validEmergencyEmailID = false;
                            emergencyEmailID.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                        checkRegistrationDetails();
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (helper.didUserRegister()) {
                            Toast.makeText(getApplicationContext(), "ALREADY REGISTERED!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            UserDetails user = new UserDetails(
                                    firstName.getText().toString().toLowerCase(),
                                    lastName.getText().toString().toLowerCase(),
                                    emailID.getText().toString(),
                                    emergencyPhone.getText().toString(),
                                    emergencyEmailID.getText().toString(),
                                    MD5.getHashedPassword(pin.getText().toString()));

                            isSuccess = helper.registerUser(user);
                            if (isSuccess) {
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(getApplicationContext(), "✓ REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "✖ ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnGetContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validContactPermission){
                    getContact();
                }else{ //Requesting Contacts Permission
                    ActivityCompat.requestPermissions(Registration.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            100);
                }
            }
        });
    }

    private void getContact(){
        Intent phoneNumberIntent = new Intent(Intent.ACTION_PICK);
        phoneNumberIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        contactsIntentResultLauncher.launch(phoneNumberIntent);
    }

    ActivityResultLauncher<Intent> contactsIntentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Toast.makeText(getApplicationContext(),"Contacts Result OK",Toast.LENGTH_SHORT).show();

                        Uri uri = result.getData().getData();
                        String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                        cursor.moveToFirst();
                        long id = cursor.getLong(0);
                        String name = cursor.getString(1);
                        Log.i("Picker", "got a contact: " + id + " - " + name);
                        cursor.close();

                        // get email and phone using the contact-ID
                        String[] projection2 = new String[] { ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1 };
                        String selection2 = ContactsContract.Data.CONTACT_ID + "=" + id + " AND " + ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "')";
                        Cursor cursor2 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection2, selection2, null, null);

                        emergencyPhone.setText("");
                        emergencyEmailID.setText("");

                        while (cursor2 != null && cursor2.moveToNext()) {
                            String mimetype = cursor2.getString(0);
                            String data = cursor2.getString(1);
                            if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                Log.i("Picker", "got a phone: " + data);
                                //Removing all special characters except digits
                                data = data.replaceAll("[^\\d]+","");
                                if(data.length()>10){ //Removing 91 from the start of the number
                                    data = data.replaceFirst("91","");
                                }
                                emergencyPhone.setText(data);
                            } else {
                                Log.i("Picker", "got an email: " + data);
                                emergencyEmailID.setText(data);
                            }
                        }
                        cursor2.close();
                    }
                }
            });


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 & grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            validContactPermission = true;
            getContact();
        }else{
            validContactPermission = false;
            Toast.makeText(getApplicationContext(),"CONTACT PERMISSION NOT GRANTED!",Toast.LENGTH_SHORT).show();
        }
    }

    private synchronized void checkRegistrationDetails(){

        if(validFirstName & validLastName & validEmail & validPin & validEmergencyPhone & validEmergencyEmailID){
            btnRegister.setEnabled(true);
        }else{
            btnRegister.setEnabled(false);
        }
    }



}