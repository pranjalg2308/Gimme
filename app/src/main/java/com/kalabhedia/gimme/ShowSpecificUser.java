package com.kalabhedia.gimme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ShowSpecificUser extends AppCompatActivity {
    private String phoneNumber;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_specific_user);
        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(phoneNumber, null);
        if (name == null)
            setTitle(phoneNumber);
        else
            setTitle(name);
        updateUI();
    }

    private void updateUI() {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        Cursor cr = db.getAllData();
        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<>();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToLast();
            do {
                String number = cr.getString(1);
                if (cr.getString(1).equals(number))
                    arrayOfActivity.add(new ActivityArray(cr.getString(0), phoneNumber, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), number));
            }
            while (cr.moveToPrevious());
            SpecificUserAdapter adapter = new SpecificUserAdapter(getApplicationContext(), arrayOfActivity);
            ListView listView = findViewById(R.id.lvItemsSpecificUser);
            listView.setAdapter(adapter);
        }
    }
}
