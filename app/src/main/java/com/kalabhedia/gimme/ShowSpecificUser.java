package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowSpecificUser extends AppCompatActivity {
    private String phoneNumber;
    private String amount;
    private Button bnSettle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_specific_user);
        bnSettle = findViewById(R.id.settle_up_button);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
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
        updateUI(phoneNumber);
        String receiverId = getReceiverKey(phoneNumber);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("UserId", Context.MODE_PRIVATE);
        String senderKey = sharedPref.getString("currentUserId", null);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String phNumber = preferences.getString("phonenumber", null);
        bnSettle.setOnClickListener(view -> {
            long time = System.currentTimeMillis();
            String timeStamp = "" + time;
            DataBaseHelper db = new DataBaseHelper(getBaseContext());
            Boolean result = db.insertData(timeStamp, phoneNumber, "", "0", "3", "0");
            AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverId, phNumber, "0", "", "03");
            bnSettle.setText("Settle Pending");
            bnSettle.setClickable(false);
        });
    }

    private void updateUI(String phoneNumber) {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        Cursor cr = db.getUserData(phoneNumber);
        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<>();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToLast();
            do {
                String number = cr.getString(1);
                String codeSettle = cr.getString(4) + cr.getString(5);
                if (codeSettle.equals("30") || codeSettle.equals("03")) {
                    bnSettle.setText("Settle Pending");
                    bnSettle.setEnabled(false);
                }
                if (cr.getString(1).equals(number) && ((cr.getString(4) + cr.getString(5)).equals("11")))
                    arrayOfActivity.add(new ActivityArray(cr.getString(0), phoneNumber, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), number));
            }
            while (cr.moveToPrevious());
            SpecificUserAdapter adapter = new SpecificUserAdapter(getApplicationContext(), arrayOfActivity);
            ListView listView = findViewById(R.id.lvItemsSpecificUser);
            listView.setAdapter(adapter);
        }
    }

    public String getReceiverKey(String user) {
        OnlineUserDataBase dbUser = new OnlineUserDataBase(getApplicationContext());
        Cursor cr = dbUser.getAllData();
        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                if (numberTemp.equals(user))
                    return cr.getString(1);
                cr.moveToNext();
            }
        }
        return null;
    }
}
