package com.example.preventattack;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeActivity extends AppCompatActivity {

    public static FusedLocationProviderClient fusedLocationProviderClient;
    private ImageView btnPanicImageView;
    private static boolean isActivated;
    private static boolean isWaitPeriod;
    private CountDownTimer countDownTimer;
    private static long remainingMilliseconds;
    private TextView timerView;
    private TextView errorTextView;
    private boolean validLocationPermission, validSMSPermission, canAccessLocation;
    private static LocationManager locationManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fusedLocationProviderClient = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        timerView = findViewById(R.id.timer);
        timerView.setText(getResources().getString(R.string.activateMessage));
        timerView.setTextColor(getResources().getColor(R.color.navyBlue));

        errorTextView = findViewById(R.id.error);

        btnPanicImageView = findViewById(R.id.btnPanic);

        isActivated = false;
        isWaitPeriod = false;
        remainingMilliseconds = 10000;
        validLocationPermission = validSMSPermission = canAccessLocation = false;

        btnPanicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLocationPermissions();
                checkSMSPermissions();
                canAccessCurrentLocation();

                if (validLocationPermission & validSMSPermission & canAccessLocation) {
                    if (!isActivated) {
                        activateAlert();
                    } else if (isActivated & isWaitPeriod) {
                        //Verifying user
                        countDownTimer.cancel();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), Login.class);
                        loginActivityResultLauncher.launch(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(locationManager1!=null){

            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                errorTextView.setVisibility(View.INVISIBLE);
                if(locationManager1!=null)
                    if(locationManager1.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        errorTextView.setVisibility(View.INVISIBLE);
                    }else{
                        errorTextView.setVisibility(View.VISIBLE);
                    }
            }
            else{
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),"DEACTIVATED!",Toast.LENGTH_SHORT).show();
                        terminate();
                    }
                    else if(result.getResultCode() == Activity.RESULT_CANCELED){
                        activateAlert();
                    }
                }
            });

    ActivityResultLauncher<Intent> mapsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    terminate();
                }
            });

    private void terminate(){
        btnPanicImageView.setImageResource(R.mipmap.panic);
        timerView.setText(getResources().getString(R.string.activateMessage));
        timerView.setTextColor(getResources().getColor(R.color.navyBlue));
        isActivated = false;
        isWaitPeriod = false;
        remainingMilliseconds = 10000;
    }

    private void success(){
        btnPanicImageView.setImageResource(R.mipmap.success);
        timerView.setText(getResources().getString(R.string.success));
        timerView.setTextColor(getResources().getColor(R.color.darkGreen));
    }

    private void activateAlert(){
        errorTextView.setVisibility(View.INVISIBLE);
        isActivated = true;
        isWaitPeriod = true;
        btnPanicImageView.setImageResource(R.mipmap.stop);
        timerView.setTextColor(getResources().getColor(R.color.cherryRed));
        countDownTimer = new CountDownTimer(remainingMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMilliseconds = millisUntilFinished;
                timerView.setText(getResources().getString(R.string.deactivateMessage)+"\n"+Long.toString(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                isWaitPeriod = false;

                //Sending users current location
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MapsActivity.class);
                mapsActivityResultLauncher.launch(intent);
                remainingMilliseconds = 10000;
            }
        };
        countDownTimer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.settings_menu:
                Intent intent_settings = new Intent();
                intent_settings.setClass(getApplicationContext(),Settings.class);
                startActivity(intent_settings);
                return true;
            case R.id.account_menu:
                Intent intent_account = new Intent();
                intent_account.setClass(getApplicationContext(),AccountActivity.class);
                startActivity(intent_account);
                return true;
            case R.id.panic_records:
                Intent intent_records = new Intent();
                intent_records.setClass(getApplicationContext(), PanicRecords.class);
                startActivity(intent_records);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayPermissionError(){
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(getResources().getString(R.string.permissionError));
    }

    private void displayNoLocationAccessError(){
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(getResources().getString(R.string.noLocationAccess));
    }

    private void checkLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            validLocationPermission = true;
        }
        else {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                System.out.println("LOCATION PERMISSION: TRUE"+"HOME");
                validLocationPermission = true;
            }
        }
    }

    private void checkSMSPermissions(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            System.out.println("SMS PERMISSION: TRUE"+"HOME");
            validSMSPermission = true;
        }
        else{
//            validSMSPermission = false;
            System.out.println("LOCATION PERMISSION: FALSE".getClass());
            System.out.println("REQUESTING SMS"+"HOME");
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    101);

            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)){
                System.out.println("SMS PERMISSION: TRUE"+"HOME");
                validSMSPermission = true;
            }
        }
    }

    private void canAccessCurrentLocation(){
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager1 = locationManager;

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            canAccessLocation = true;
        }
        else{
            canAccessLocation = false;
            displayNoLocationAccessError();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 & grantResults.length > 0 && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
            validLocationPermission = true;
            errorTextView.setVisibility(View.INVISIBLE);
        } else {
            validLocationPermission = false;
            displayPermissionError();
        }
        if (requestCode == 101 & grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            validSMSPermission = true;
            errorTextView.setVisibility(View.INVISIBLE);
        } else {
            validSMSPermission = false;
            displayPermissionError();
        }
    }
}