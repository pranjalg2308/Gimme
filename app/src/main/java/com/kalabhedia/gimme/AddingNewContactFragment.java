package com.kalabhedia.gimme;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddingNewContactFragment extends Fragment implements View.OnClickListener {
    private static DatabaseReference NotificationReferernce;
    private static Context context;
    ArrayList<String> contactName;
    ArrayList<String> contactNumber;
    ArrayList<HashMap<String, String>> contactdetail;
    ArrayList<String> contactView;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    private int READ_CONTACT_PERMISSION = 1;
    private String senderUserID;
    private String receiverKey;
    EditText amount;
    String amountEntered;
    private String number;
    private RadioGroup radioGroup;
    private RadioButton radioButtonClaim;
    private String NO = "2";
    private String YES = "1";
    private String NULL = "0";
    private long time;
    String timeStamp = "";
    DataBaseHelper db;


    Button bnAmount10, bnAmount50, bnAmount100, bnAmount500, bnAmount1000;

    /**
     * @param timeStamp
     * @param senderUserID
     * @param receiverUserID
     * @param phoneNumber
     * @param amountEntered
     * @param reason
     */
    public static void sendNotificationToUser(String timeStamp, String senderUserID, String receiverUserID, String phoneNumber, String amountEntered, String reason, String code) {
        NotificationReferernce = FirebaseDatabase.getInstance().getReference().child("Notifications");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("Code", code);
        notificationData.put("TimeStamp", timeStamp);
        notificationData.put("phone_number", phoneNumber);
        notificationData.put("Amount", amountEntered);
        notificationData.put("From", senderUserID);
        notificationData.put("Reason", reason);
        notificationData.put("Type", "request");
        NotificationReferernce.child(receiverUserID).push().setValue(notificationData)
                .addOnFailureListener(e -> Toast.makeText(context, "Error in sending data ", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (code.equals("01")) {
                        if (dataBaseHelper.updateOnSent(timeStamp)) {
                            Toast.makeText(context, "Success on sent", Toast.LENGTH_SHORT);
                        }
                    }
                });

    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        contactdetail = ((MainActivity) getActivity()).contactdetails;
        OnlineUserDataBase onlineUserDataBase = new OnlineUserDataBase(context);
        db = new DataBaseHelper(getContext());
        db.getWritableDatabase();
        NotificationReferernce = FirebaseDatabase.getInstance().getReference().child("Notifications");
        ((MainActivity) getActivity()).actionbar.setTitle("Add Bill");
        contactName = new ArrayList<>();
        contactNumber = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_adding_new_contact, container, false);
        EditText discription = view.findViewById(R.id.idReason);

        amount = view.findViewById(R.id.amount_entry);
        radioGroup = view.findViewById(R.id.idClaim);


        bnAmount10 = view.findViewById(R.id.bn_amount_10);
        bnAmount50 = view.findViewById(R.id.bn_amount_50);
        bnAmount100 = view.findViewById(R.id.bn_amount_100);
        bnAmount500 = view.findViewById(R.id.bn_amount_500);
        bnAmount1000 = view.findViewById(R.id.bn_amount_1000);

        bnAmount10.setOnClickListener(this::onClick);
        bnAmount50.setOnClickListener(this::onClick);
        bnAmount100.setOnClickListener(this::onClick);
        bnAmount500.setOnClickListener(this::onClick);
        bnAmount1000.setOnClickListener(this::onClick);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phonenumber", null);
        senderUserID = sharedPreferences.getString("Current_user_id", null);
        Log.w("Sender id", senderUserID + " ");
        Button clearText = view.findViewById(R.id.bn_clear_txt);

        AutoCompleteTextView contact = view.findViewById(R.id.contacts);
        if (checkExternalPermission()) {
            clearText.setOnClickListener(view13 -> {
                number = null;
                contact.setText("");
                contact.setFocusableInTouchMode(true);
            });
//            getActivity().getSupportLoaderManager().initLoader(1, null, this);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, contactName);
            SimpleAdapter adapter = new SimpleAdapter(getContext(),
                    contactdetail,
                    R.layout.name_number_view,
                    new String[]{"Name", "Number"},
                    new int[]{R.id.line_a, R.id.line_b});
            contact.setThreshold(1);
            contact.setAdapter(adapter);
            contact.setOnItemClickListener((adapterView, view12, i, l) -> {
                HashMap<String, String> selected = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                contact.setText(selected.get("Name"));
                number = selected.get("Number");
                contact.setFocusable(false);
            });
        } else
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
        Button button = view.findViewById(R.id.bn_save);

/**
 * click listener of save button
 */
        button.setOnClickListener(view1 -> {
            String reason = discription.getText().toString() + "";
            button.setEnabled(false);
            timeStamp = "";
            time = System.currentTimeMillis();
            timeStamp = timeStamp + time;
            hideKeyboard(getActivity());
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButtonClaim = view.findViewById(selectedId);
            String claimString = radioButtonClaim.getText().toString();


            amountEntered = amount.getText().toString();
            if (claimString.equals("TAKEN")) {
                amountEntered = "-" + amountEntered;
            }
            if (number != null) {
                if (!amountEntered.isEmpty()) {
                    String[] conversionNumber = number.split(" ");
                    number = "";
                    for (String i : conversionNumber) {
                        number += i;
                    }
                    if (!number.startsWith("+91")) {
                        number = "+91" + number;
                    }

//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    database.getReference("Users").addListenerForSingleValueEvent(
//                            new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                        Log.w("Device numbers", data.child("device_number").getValue().toString());
//                                        String[] conversion = data.child("device_number").getValue().toString().split(" ");
//                                        String converted = "";
//                                        for (String i : conversion) {
//                                            converted += i;
//                                        }
//                                        if (converted.equals(number)) {
//                                            Log.w("result", "number present");
//                                            receiverKey = data.getKey();
//                                            Log.w("receiverKey", receiverKey);
//                                        }
//                                    }
                    Cursor cr = onlineUserDataBase.getAllData();
                    cr.moveToFirst();
                    if (cr != null && cr.getCount() > 0) {
                        cr.moveToLast();
                        do {
                            if (cr.getString(0).equals(number))
                                receiverKey = cr.getString(1);
                        }
                        while (cr.moveToPrevious());
                    }
                    if (receiverKey == null) {
                        open(view);
                        button.setEnabled(true);
                        //todo receiver not found in database
                    } else {
                        button.setEnabled(false);

                        reason = reason.trim();
                        sendNotificationToUser(timeStamp,
                                senderUserID,
                                receiverKey,
                                phoneNumber,
                                (-1 * Integer.parseInt(amountEntered)) + "",
                                reason,
                                "01");


                        saveInLocalDatabase(timeStamp, number, reason, amountEntered);


                        OneFragment.fab.setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).viewPager.setVisibility(View.VISIBLE);
                        amount.setFocusable(false);
                        contact.setFocusable(false);
                        ((MainActivity) getActivity()).actionbar.setTitle("Gimme");
                        getFragmentManager().beginTransaction()
                                .remove(AddingNewContactFragment.this).commit();
                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
//                                }
//                            });
                    ((MainActivity) getActivity()).viewPager.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Amount field can't be empty", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), "Contact Field can't be empty", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            }

        });
        return view;
    }


    public void open(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Share Gimme");
        alertDialogBuilder.setMessage("User does not have this app.\n Do you want to share this app?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //TODO
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        String shareBody = "Check This Out";
                        String shareSub = "this is the link";
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                        startActivity(Intent.createChooser(shareIntent, "Share Using"));
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void saveInLocalDatabase(String time, String number, String reason, String amountEntered) {
        Boolean result = db.insertData(time, number, reason, amountEntered, YES, NULL);

        if (result == true) {
            Toast.makeText(getContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Data Inserted Unsuccessfully", Toast.LENGTH_SHORT).show();
        }
    }

    //    /**
//     * @param id
//     * @param args
//     * @return
//     */
//    @NonNull
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        Loader<Cursor> cursorLoader = new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
//        return cursorLoader;
//    }
//
//    /**
//     * @param loader
//     * @param cursor
//     */
//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
//        cursor.moveToFirst();
//        SharedPreferences sharedPref = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
//        if (sharedPref != null) {
//            SharedPreferences.Editor editor = sharedPref.edit();
//            HashMap<String, String> item;
//            while (!cursor.isAfterLast()) {
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                contactName.add(name);
//                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                contactNumber.add(number);
//                item = new HashMap<>();
//                item.put("Name", name);
//                item.put("Number", number);
//                contactdetail.add(item);
//                if (!number.startsWith("+91")) {
//                    number = "+91" + number;
//                }
//                String[] conversion = number.split(" ");
//                String[] conversion1 = number.split("-");
//                if (conversion1.length > 1) {
//                    number = "";
//                    for (String i : conversion1) {
//                        number += i;
//                    }
//                } else if (conversion.length > 1) {
//                    number = "";
//                    for (String i : conversion) {
//                        number += i;
//                    }
//                }
//                editor.putString(number, name);
//                editor.apply();
//                cursor.moveToNext();
//            }
//        } else {
//            Toast.makeText(getContext(), "Unable to load", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * @param loader
//     */
//    @Override
//    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//
//    }
//
//    /**
//     *
//     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).tabLayout.setVisibility(View.VISIBLE);
    }

    private boolean checkExternalPermission() {
        String permission = android.Manifest.permission.READ_CONTACTS;
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {
        int moneyInt;
        String moneyString = amount.getText().toString();
        if (moneyString.isEmpty())
            moneyInt = 0;
        else
            moneyInt = Integer.parseInt(moneyString);
        switch (view.getId()) {
            case R.id.bn_amount_10:
                moneyInt += 10;
                amount.setText(moneyInt + "");
                break;
            case R.id.bn_amount_50:
                moneyInt += 50;
                amount.setText(moneyInt + "");
                break;
            case R.id.bn_amount_100:
                moneyInt += 100;
                amount.setText(moneyInt + "");
                break;
            case R.id.bn_amount_500:
                moneyInt += 500;
                amount.setText(moneyInt + "");
                break;
            case R.id.bn_amount_1000:
                moneyInt += 1000;
                amount.setText(moneyInt + "");
                break;
            default:
                break;
        }
        amount.setSelection(amount.getText().length());
    }
}
