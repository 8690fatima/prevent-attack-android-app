package com.example.preventattack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

        logView.append("Checking Location & SMS Permission...\n");
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            logView.append("> GRANTED ✔️\n");
            //getLocationUsingNetwork(); //Ideally we need to call this function only but
                                        //an emulator does not support network hence we won't
                                        //get the location. To get the location co-ordinates on an
                                        //emulator it is mandatory to use GPS
            getLocationUsingGPS();      //Hence directly this function call needs to be made.
        } else {
            logView.append("> NOT GRANTED ❌\n");
            exit(-1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                errorTxt.setVisibility(View.VISIBLE);
                logView.setText(logView.getText().toString() + "Could not send location! Please try again!\n");
                logView.setText(logView.getText().toString() + "Terminating...\n");
                logView.setText(logView.getText().toString() + "END\n");
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
                    Log.i("MapsActivity","Location details added to the database.");
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
    private void getLocationUsingNetwork() {

        boolean isNetworkProviderEnabled;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        logView.append("Checking Network Location Access...\n");

        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isNetworkProviderEnabled){ //if network provider is not enabled
            logView.append("> INACCESSIBLE ❌\n");
            getLocationUsingGPS();
            return;
        }else{ //network provider is enabled
            logView.append("> ENABLED  ✔️\n");
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {

                    logView.append("Finding location co-ordinates through network...\n");

                    if(location!=null){

                        logView.append("Co-ordinates Found:-\n" + "Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude() + "\n");
                        sendSMS(location);
                    }
                    else{
                        logView.append("Could not find location!"+"\n");
                        getLocationUsingGPS();
                    }
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) { }

                @Override
                public void onProviderDisabled(@NonNull String provider) { }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

            }, Looper.myLooper());
        }

    }

    @SuppressLint("MissingPermission")
    void getLocationUsingGPS(){

        boolean isGpsProviderEnabled;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        logView.append("Checking GPS Location Access...\n");

        isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isGpsProviderEnabled)
        {  //GPS Provider is not enabled
            logView.append("> INACCESSIBLE ❌\n");
            exit(-1);
        }
        else
        {  //GPS is enabled
            logView.append("> ENABLED  ✔️\n");

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {

                    logView.append("Finding location co-ordinates through GPS...\n");

                    if(location!=null){

                        logView.append("Co-ordinates Found:-\n" + "Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude() + "\n");
                        sendSMS(location);
                    }
                    else{
                        logView.append("Could not find location!"+"\n");
                        exit(-1);
                    }
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) { }

                @Override
                public void onProviderDisabled(@NonNull String provider) { }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

            }, Looper.myLooper());
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
