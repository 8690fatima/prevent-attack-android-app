package com.example.preventattack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Map;

public class Settings extends AppCompatActivity {

    private EditText detailsEditText, newPinEditText, newRePinEditText, emergencyPhone, emergencyEmailID;
    private Spinner spinner;
    private int spinnerSelection;
    private RelativeLayout layoutPersonalDetails, layoutChangeEmergencyDetails, layoutPinDetails;
    private TextView txtEditPersonalDetails, txtEditPinDetails, txtChangeEmergencyDetails;
    Button btnChangeDetails, btnChangePassword, btnGetContact, btnChangeEmergencyDetails;
    private boolean validNewPin, validNewRePin, validContactPermission;
    private static DatabaseHelper helper;
    private static boolean
            validEmergencyPhone,
            validEmergencyEmailID;
    private Map securityDetails = null;
    private Intent intent;

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            validContactPermission = true;
        }else{
            validContactPermission = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        validNewPin = validNewRePin = validEmergencyPhone = validEmergencyEmailID  = validContactPermission =  false;

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(helper == null){
            helper = new DatabaseHelper(getApplicationContext());
        }

        initializeSettings();
        settingsMethod();
        getCurrentEmergencyContactDetails();

        intent = getIntent();
        if(intent.getBooleanExtra("forgotPassword",false)){
            layoutPinDetails.setVisibility(View.VISIBLE);
        }else{
            layoutPinDetails.setVisibility(View.GONE);
        }
    }

    private void initializeSettings(){
        detailsEditText = findViewById(R.id.personalDetailsEditText);
        newPinEditText = findViewById(R.id.editTxtNewPin);
        newRePinEditText = findViewById(R.id.editTxtNewRePin);
        btnChangeDetails = findViewById(R.id.btnSavePersonalDetails);
        btnChangeEmergencyDetails = findViewById(R.id.btnChangeEmergencyContactDetails);
        btnChangePassword = findViewById(R.id.btnSavePinDetails);
        spinner = findViewById(R.id.personalDetailsSpinner);
        emergencyPhone = findViewById(R.id.emergencyPhone);
        emergencyEmailID = findViewById(R.id.emergencyEmail);
        btnGetContact = findViewById(R.id.getContact);
        layoutPersonalDetails = findViewById(R.id.layoutPersonalDetails);
        layoutChangeEmergencyDetails = findViewById(R.id.layoutChangeEmergencyDetails);
        layoutPinDetails = findViewById(R.id.layoutPinDetails);
        txtChangeEmergencyDetails = findViewById(R.id.txtChangeEmergencyDetails);
        txtEditPersonalDetails = findViewById(R.id.txtEditPersonalDetails);
        txtEditPinDetails = findViewById(R.id.txtEditPinDetails);

    }

    private void settingsMethod(){

        txtEditPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutPersonalDetails.getVisibility() == View.VISIBLE) {
                    layoutPersonalDetails.setVisibility(View.GONE);
                }else{
                    layoutPersonalDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        txtChangeEmergencyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutChangeEmergencyDetails.getVisibility() == View.VISIBLE){
                    layoutChangeEmergencyDetails.setVisibility(View.GONE);
                }else{
                    layoutChangeEmergencyDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        txtEditPinDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutPinDetails.getVisibility() == View.VISIBLE){
                    layoutPinDetails.setVisibility(View.GONE);
                }else{
                    layoutPinDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                detailsEditText.getText().clear();
                switch(position){
                    case 0:
                        spinnerSelection = 0;
                        detailsEditText.setHint(getOldValue(spinnerSelection));
                        detailsEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 1:
                        spinnerSelection = 1;
                        detailsEditText.setHint(getOldValue(spinnerSelection));
                        detailsEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 2:
                        spinnerSelection = 2;
                        detailsEditText.setHint(getOldValue(spinnerSelection));
                        detailsEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        detailsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch(detailsEditText.getInputType()){
                    case InputType.TYPE_CLASS_TEXT:
                        if (s.toString().matches("[A-Za-z]+")) {
                            //validName = true;
                            btnChangeDetails.setEnabled(true);
                            detailsEditText.setTextColor(getResources().getColor(R.color.darkGreen));
                        } else {
                            //validFirstName = false;
                            btnChangeDetails.setEnabled(false);
                            detailsEditText.setTextColor(getResources().getColor(R.color.cherryRed));
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                        if (s.toString().matches("^[A-Za-z0-9][A-Za-z0-9.+_-]+@[A-Za-z]+[.][A-Za-z][A-Za-z.]+[A-Za-z]$")) {
                            //validEmergencyEmailID = true;
                            detailsEditText.setTextColor(getResources().getColor(R.color.darkGreen));
                            btnChangeDetails.setEnabled(true);
                        } else {
                            //validEmergencyEmailID = false;
                            detailsEditText.setTextColor(getResources().getColor(R.color.cherryRed));
                            btnChangeDetails.setEnabled(false);
                        }
                        break;
                }
            }
        });

        btnChangeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(detailsEditText.getInputType()){
                    case InputType.TYPE_CLASS_TEXT:
                        switch(spinnerSelection){
                            case 0: //change first name
                                if(helper.changeDetails("first_name",detailsEditText.getText().toString().toLowerCase())){
                                    Toast.makeText(getApplicationContext(),"✅ UPDATE SUCCESSFUL",Toast.LENGTH_SHORT).show();
                                    detailsEditText.setHint(getOldValue(spinnerSelection));
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"❌ ERROR! TRY AGAIN",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: //change last name
                                if(helper.changeDetails("last_name",detailsEditText.getText().toString().toLowerCase())){
                                    Toast.makeText(getApplicationContext(),"✅ UPDATE SUCCESSFUL",Toast.LENGTH_SHORT).show();
                                    detailsEditText.setHint(getOldValue(spinnerSelection));
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"❌ ERROR! TRY AGAIN",Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                        if(helper.changeDetails("email_address",detailsEditText.getText().toString().toLowerCase())){
                            Toast.makeText(getApplicationContext(),"✅ UPDATE SUCCESSFUL",Toast.LENGTH_SHORT).show();
                            detailsEditText.setHint(getOldValue(2));
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"❌ ERROR! TRY AGAIN",Toast.LENGTH_SHORT).show();
                        }
                }
                detailsEditText.getText().clear();
            }
        });

        btnChangeEmergencyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helper.changeEmergencyDetails(emergencyPhone.getText().toString(),emergencyEmailID.getText().toString())){
                    Toast.makeText(getApplicationContext(),"✅ UPDATE SUCCESSFUL!",Toast.LENGTH_SHORT).show();
                    emergencyPhone.setText("");
                    emergencyEmailID.setText("");
                    getCurrentEmergencyContactDetails();
                }
                else{
                    Toast.makeText(getApplicationContext(),"❌ ERROR! TRY AGAIN",Toast.LENGTH_SHORT).show();
                }
            }
        });

        newPinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 4){
                    validNewPin = true;
                    newPinEditText.setTextColor(getResources().getColor(R.color.darkGreen));
                    newRePinEditText.requestFocus();
                }
                else {
                    validNewRePin = false;
                    newRePinEditText.getText().clear();
                    newPinEditText.setTextColor(getResources().getColor(R.color.cherryRed));
                }
                checkPinDetails();
            }
        });

        newRePinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(newPinEditText.getText().length()!=4){
                    newRePinEditText.getText().clear();
                    newPinEditText.requestFocus();
                }
                else if (s.length() == 4 & newPinEditText.getText().toString().equals(s.toString())) {
                    validNewRePin = true;
                    newRePinEditText.setTextColor(getResources().getColor(R.color.darkGreen));
                } else {
                    validNewRePin = false;
                    newRePinEditText.setTextColor(getResources().getColor(R.color.cherryRed));
                }
                checkPinDetails();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(securityDetails == null){
                    securityDetails = helper.getSecurityDetails();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Security Check");
                builder.setMessage(securityDetails.get("securityQ").toString());
                final View view = getLayoutInflater().inflate(R.layout.forgot_password, null);
                builder.setView(view);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String response = ((EditText)view.findViewById(R.id.securityAnswer)).getText().toString().toLowerCase();

                        if(securityDetails.get("securityAns").equals(MD5.getHashedPassword(response))){
                            if(helper.changePassword(newPinEditText.getText().toString())){
                                Toast.makeText(Settings.this, "PASSWORD UPDATED SUCCESSFULLY!", Toast.LENGTH_SHORT).show();

                                if(intent.getBooleanExtra("forgotPassword",false)){
                                    finish();
                                }
                            }else{
                                Toast.makeText(Settings.this, "❌ ERROR!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Settings.this, "INVALID ANSWER! COULD NOT UPDATE PIN", Toast.LENGTH_SHORT).show();
                        }

                        newPinEditText.getText().clear();
                        newRePinEditText.getText().clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
                        checkEmergencyContactDetails();
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
                        checkEmergencyContactDetails();
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
                    ActivityCompat.requestPermissions(Settings.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            100);
                }
            }
        });
    }

    private String getOldValue(int spinnerSelected){
        switch(spinnerSelected){
            case 0:
                return helper.getCurrentValue("first_name").toUpperCase();
            case 1:
                return helper.getCurrentValue("last_name").toUpperCase();
            case 2:
                return helper.getCurrentValue("email_address");
        }
        return "";
    }

    private void getCurrentEmergencyContactDetails(){
        emergencyPhone.setHint(helper.getCurrentValue("emergency_phone"));
        emergencyEmailID.setHint(helper.getCurrentValue("emergency_email"));
    }

    private void checkPinDetails(){
        if(validNewPin & validNewRePin)
            btnChangePassword.setEnabled(true);
        else
            btnChangePassword.setEnabled(false);
    }

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

    private synchronized void checkEmergencyContactDetails(){

        if(validEmergencyPhone & validEmergencyEmailID){
            btnChangeEmergencyDetails.setEnabled(true);
        }else{
            btnChangeEmergencyDetails.setEnabled(false);
        }
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

                        if(cursor2.getCount()!=0){
                            emergencyPhone.setHint(R.string.emergencyPhoneHint);
                            emergencyEmailID.setText(R.string.emergencyEmailHint);
                        }

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
}