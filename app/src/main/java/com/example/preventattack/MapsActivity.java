package com.example.preventattack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity {
    private TextView logView, errorTxt;
    private ImageView imageView, imageCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_maps);

        errorTxt = findViewById(R.id.errorTxt);
        logView = findViewById(R.id.log);
        logView.setText("\nPanic Alert Activated...\n");
        imageView = findViewById(R.id.resultImage);
        imageCall = findViewById(R.id.call);
        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        logView.append("Checking SMS Permission...\n");
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            logView.append("> GRANTED ✔️\n");
            getCurrentLocation();
        } else {
            logView.append("> NOT GRANTED ❌\n");
            exit(-1);
        }

        if(HomeActivity.fusedLocationProviderClient == null)
            HomeActivity.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(HomeActivity.fusedLocationProviderClient == null) {
            HomeActivity.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    void exit(int exitCode) {
        switch (exitCode) {
            case 0: // Process successful
                errorTxt.setVisibility(View.GONE);
                logView.setText(logView.getText().toString() + "END\n");
                logView.setTextColor(getResources().getColor(R.color.darkGreen));
                imageView.setImageResource(R.mipmap.sms_success);
                setResult(RESULT_OK);
                break;
            case -1:
                //logView.setText(logView.getText().toString() + "ERROR...\n");
                errorTxt.setVisibility(View.VISIBLE);
                logView.setText(logView.getText().toString() + "Could not send location! Please try again!\n");
                logView.setText(logView.getText().toString() + "Terminating...\n");
                logView.setText(logView.getText().toString() + "END\n");

                //logView.setTextColor(getResources().getColor(R.color.cherryRed));
                imageView.setImageResource(R.mipmap.sms_unsuccess);
                setResult(RESULT_CANCELED);
                break;
        }
    }

    private void call(){
        String phoneNumber = new DatabaseHelper(getApplicationContext()).getEmergencyPhone();
        try{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Phone Call Permission not granted", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
            }
            else{
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendSMS(Location location) {

        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                logView.append("Sending the location co-ordinates through SMS...\n");
                String phoneNumber = new DatabaseHelper(getApplicationContext()).getEmergencyPhone();
                String message = getResources().getString(R.string.message)+"http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                System.out.println("Sending sms to "+phoneNumber);
                smsManager.sendTextMessage(phoneNumber.trim(), null, message, null, null);
                logView.append("> SMS sent successfully\n");
                if(new DatabaseHelper(getApplicationContext()).insertLocation(location.getLongitude(), location.getLatitude())){
                    Toast.makeText(getApplicationContext(), "Location details saved",Toast.LENGTH_SHORT).show();
                }
                exit(0);

            } catch (SecurityException e) {
                e.printStackTrace();
                logView.append("ERROR! Could not send SMS\n");
                exit(-1);
            }
        }else{
            logView.append("> NOT GRANTED  ❌\n");
            exit(-1);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        logView.append("Checking Location Access Permissions...\n");

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            logView.append("> GRANTED  ✔️\n");

            if(HomeActivity.fusedLocationProviderClient!=null) {
                HomeActivity.fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        logView.append("Finding location co-ordinates...\n");

                        final Location location = task.getResult();
                        if (location != null) {
                            logView.append("Co-ordinates:-\n" + "Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude() + "\n");
                            sendSMS(location);
                        } else {
                            LocationRequest locationRequest = LocationRequest.create()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000)
                                    .setNumUpdates(1);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    Location location1 = locationResult.getLastLocation();

                                    while(location1 == null){
                                        location1 = locationResult.getLastLocation();
                                    }

                                    if(location1!=null) {
                                        logView.append("Co-ordinates:-\n" + "Latitude: " + location1.getLatitude() + "\n" + "Longitude: " + location1.getLongitude() + "\n");
                                        sendSMS(location1);
                                    }else{
                                        logView.append("ERROR Could not find Co-ordinates!");
                                    }

                                }
                            };

                            HomeActivity.fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                    locationCallback, Looper.myLooper());
                        }
                    }
                });
            }else{
                HomeActivity.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            }
        }else{
            logView.append("> NOT GRANTED  ❌\n");
            exit(-1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101 & grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            call();
        }
    }
}
