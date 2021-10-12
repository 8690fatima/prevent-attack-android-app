package com.example.preventattack;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class PanicRecords extends AppCompatActivity {

    ArrayList<HashMap<String, String>> records;
    ListView lv = null;
    ListAdapter adapter;
    private static DatabaseHelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_records);

        db = new DatabaseHelper(this);
        records = db.getPanicRecords();
        lv = (ListView) findViewById(R.id.panic_records_list_view);
        adapter = new SimpleAdapter(PanicRecords.this, records, R.layout.record,new String[]{"id","date-time","longitude","latitude"}, new int[]{R.id.record_id, R.id.record_dateNtime, R.id.record_longitude, R.id.record_latitude});
        lv.setAdapter(adapter);
    }
}