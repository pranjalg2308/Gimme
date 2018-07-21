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
        amount = bundle.getString("amount");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(phoneNumber, null);
        String toolbarTitle = "";
        if (name == null)
            toolbarTitle = phoneNumber;
        else
            toolbarTitle = name;
        if (Integer.parseInt(amount) < 0)
            toolbarTitle = "You owe " + toolbarTitle + " ₹" + (-1 * Integer.parseInt(amount));
        else
            toolbarTitle = toolbarTitle + " owes you" + " ₹" + amount;

        setTitle(toolbarTitle);
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
                if (cr.getString(1).equals(number) && ((cr.getString(4) + cr.getString(5)).equals("11")))
                    arrayOfActivity.add(new ActivityArray(cr.getString(0), phoneNumber, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), number));
            }
            while (cr.moveToPrevious());
            SpecificUserAdapter adapter = new SpecificUserAdapter(getApplicationContext(), arrayOfActivity);
            ListView listView = findViewById(R.id.lvItemsSpecificUser);
            listView.setAdapter(adapter);
        }
    }
}
