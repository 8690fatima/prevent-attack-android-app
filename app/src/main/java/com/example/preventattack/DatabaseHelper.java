package com.example.preventattack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseHelper extends SQLiteOpenHelper {

    public static final String USER_INFO_TABLE = "user_info";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_EMERGENCY_PHONE = "emergency_phone";
    public static final String COLUMN_EMERGENCY_EMAIL = "emergency_email";
    public static final String COLUMN_PIN = "pin";
    public static final String USER_LOCATIONS_TABLE = "user_locations";
    public static final String COLUMN_DATE_TIME = "date";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SECURITY_QUESTION = "security_question";
    public static final String COLUMN_SECURITY_ANSWER = "security_answer";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void onCreate(final SQLiteDatabase db) {

        String createUserTableQuery = "CREATE TABLE " + USER_INFO_TABLE + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_LAST_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PIN + " TEXT," +
                COLUMN_EMERGENCY_PHONE + " TEXT," +
                COLUMN_EMERGENCY_EMAIL + " TEXT," +
                COLUMN_SECURITY_QUESTION + " TEXT," +
                COLUMN_SECURITY_ANSWER + " TEXT)";

        String createTableLocations = "CREATE TABLE " + USER_LOCATIONS_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATE_TIME + " TEXT," +
                COLUMN_LONGITUDE + " REAL," +
                COLUMN_LATITUDE + " REAL)";

        db.execSQL(createUserTableQuery);
        db.execSQL(createTableLocations);
    }

    public ArrayList getPanicRecords(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> records = new ArrayList<>();
        String query = "SELECT * FROM "+ USER_LOCATIONS_TABLE;
        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()){
            HashMap<String,String> record = new HashMap<>();
            record.put("id",cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            record.put("date-time",cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME)));
            record.put("longitude",cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)));
            record.put("latitude",cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)));
            records.add(record);
        }
        return  records;
    }

    public boolean unRegister(){
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        int isSuccess = db.delete(USER_INFO_TABLE,"1",null);
        if(isSuccess!=0){
            System.out.println("User deleted");
            result = true;
        }else{
            System.out.println("Error deleting");
            result = false;
        }
        return result;
    }

    public boolean insertLocation(Double longitude, Double latitude){

        SQLiteDatabase db = this.getWritableDatabase();

        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_DATE_TIME, dateFormat.format(date));
        initialValues.put(COLUMN_LONGITUDE, longitude);
        initialValues.put(COLUMN_LATITUDE, latitude);
        long result = db.insert(USER_LOCATIONS_TABLE, null, initialValues);

        if(result!=-1){
            return true;
        }
        return false;
    }

    public String getCurrentValue(String detail){

        if(detail=="first_name")
            return getUserDetails().get("firstName").toString();
        else if(detail=="last_name")
            return getUserDetails().get("lastName").toString();
        else if(detail=="email_address")
            return getUserDetails().get("email").toString();
        else if(detail == "emergency_phone")
            return getUserDetails().get("emergencyPhone").toString();
        else if(detail == "emergency_email")
            return getUserDetails().get("emergencyEmail").toString();

        return "";
    }

    public Map getUserDetails(){

        Map<String,String> userDetails = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String getUserDetailsQuery = "SELECT * FROM "+USER_INFO_TABLE;
        Cursor cursor = db.rawQuery(getUserDetailsQuery,null);

        if(cursor.moveToFirst()){
            userDetails.put("firstName",cursor.getString(1));
            userDetails.put("lastName",cursor.getString(2));
            userDetails.put("email",cursor.getString(3));
            userDetails.put("pin",cursor.getString(4));
            userDetails.put("emergencyPhone",cursor.getString(5));
            userDetails.put("emergencyEmail",cursor.getString(6));
        }
        cursor.close();
        db.close();
        return userDetails;
    }

    public Map getSecurityDetails(){
        Map<String, String> securityDetails = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String getSecurityDetailsQuery = "SELECT "+COLUMN_SECURITY_QUESTION+","+COLUMN_SECURITY_ANSWER+" FROM "+USER_INFO_TABLE;
        Cursor cursor = db.rawQuery(getSecurityDetailsQuery,null);

        if(cursor.moveToFirst()){
            securityDetails.put("securityQ",cursor.getString(0));
            securityDetails.put("securityAns",cursor.getString(1));
        }
        cursor.close();
        db.close();
        return securityDetails;
    }

    public boolean didUserRegister(){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String getUserDetailsQuery = "SELECT * FROM "+USER_INFO_TABLE;
        Cursor cursor = db.rawQuery(getUserDetailsQuery,null);
        if(cursor.moveToFirst())
            result = true;
        return result;
    }

    public boolean verifyPassword(String pin){
        boolean isValidPin = false;
        Map m = getUserDetails();
        System.out.println(m);
        System.out.println(MD5.getHashedPassword(pin));
        if(m.get("pin").equals(MD5.getHashedPassword(pin))) {
            isValidPin = true;
        }
        return isValidPin;
    }

    public String getEmergencyPhone(){
        return getUserDetails().get("emergencyPhone").toString();
    }

    boolean result = false;
    public boolean registerUser(final UserDetails user){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userDetailsCv = new ContentValues();
        userDetailsCv.put(COLUMN_USER_ID,1);
        userDetailsCv.put(COLUMN_FIRST_NAME,user.getFirstName());
        userDetailsCv.put(COLUMN_LAST_NAME,user.getLastName());
        userDetailsCv.put(COLUMN_EMAIL,user.getEmailID());
        userDetailsCv.put(COLUMN_PIN,user.getHashedPin());
        userDetailsCv.put(COLUMN_EMERGENCY_PHONE,user.getEmergencyPhone());
        userDetailsCv.put(COLUMN_EMERGENCY_EMAIL,user.getEmergencyEmailID());
        userDetailsCv.put(COLUMN_SECURITY_QUESTION,user.getSecurityQuestion());
        userDetailsCv.put(COLUMN_SECURITY_ANSWER,user.getSecurityAnswer());

        long isSuccess = db.insert(USER_INFO_TABLE,null, userDetailsCv);

        if(isSuccess!=-1)
            result = true;

        db.close();
    return result;
    }

    public boolean changeEmergencyDetails(String emergencyPhone, String emergencyEmail){
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        long isSuccess;
        boolean result = false;

        contentValues.put(COLUMN_EMERGENCY_PHONE,emergencyPhone);
        contentValues.put(COLUMN_EMERGENCY_EMAIL,emergencyEmail);

        isSuccess = db.update(USER_INFO_TABLE,contentValues,COLUMN_USER_ID+"=1",null);
        if(isSuccess == 1){
            result = true;
        }
        return result;
    }


    public boolean changeDetails(String detail, String newValue){

        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        long isupdateSuccess;
        boolean result = false;

        if(detail == "first_name"){
            contentValues.put(COLUMN_FIRST_NAME,newValue);
        }
        else if(detail == "last_name"){
            contentValues.put(COLUMN_LAST_NAME,newValue);
        }
        else if(detail == "email_address"){
            contentValues.put(COLUMN_EMAIL,newValue);
        }
        else if(detail == "emergency_email"){
            contentValues.put(COLUMN_EMERGENCY_EMAIL,newValue);
        }
        else if(detail == "emergency_phone"){
            contentValues.put(COLUMN_EMERGENCY_PHONE,newValue);
        }

        isupdateSuccess = db.update(USER_INFO_TABLE,contentValues,COLUMN_USER_ID+"=1",null);
        if(isupdateSuccess==1)
            result = true;
        return result;
    }

    public boolean changePassword(String newPin){
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        long isupdateSuccess;
        boolean result = false;

        contentValues.put(COLUMN_PIN,MD5.getHashedPassword(newPin));

        isupdateSuccess = db.update(USER_INFO_TABLE,contentValues,COLUMN_USER_ID+"=1",null);
        if(isupdateSuccess==1)
            result = true;
        return result;
    }

}
