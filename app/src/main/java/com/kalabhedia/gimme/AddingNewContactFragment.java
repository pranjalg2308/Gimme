package com.kalabhedia.gimme;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddingNewContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
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
    private EditText reason;
    DataBaseHelper db;


    Button bnAmount10, bnAmount50, bnAmount100, bnAmount500, bnAmount1000;

    public static void sendNotificationToUser(String senderUserID, String receiverUserID, String phoneNumber, String amountEntered) {
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("phone_number", phoneNumber);
        notificationData.put("Amount", amountEntered);
        notificationData.put("From", senderUserID);
        notificationData.put("Type", "request");
        NotificationReferernce.child(receiverUserID).push().setValue(notificationData).addOnFailureListener(e ->
                Toast.makeText(context, "Error in sending data ", Toast.LENGTH_SHORT).show()).addOnCompleteListener(task -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notifications");
            Query applesQuery = ref.child(receiverUserID).orderByChild("From").equalTo(senderUserID);

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Notification", "onCancelled", databaseError.toException());
                }
            });
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DataBaseHelper(getContext());
        db.getWritableDatabase();
        NotificationReferernce = FirebaseDatabase.getInstance().getReference().child("Notifications");
        ((MainActivity) getActivity()).actionbar.setTitle("Add Bill");
        contactName = new ArrayList<>();
        contactNumber = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_adding_new_contact, container, false);
        contactdetail = new ArrayList<>();
        EditText discription=view.findViewById(R.id.idReason);

        amount = view.findViewById(R.id.amount_entry);
        radioGroup = view.findViewById(R.id.idClaim);



        reason=view.findViewById(R.id.reason_text_view);


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

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phonenumber", null);
        senderUserID = sharedPreferences.getString("Current_user_id", null);
        Log.w("Sender id", senderUserID + " ");
        context = getContext();
        Button clearText = view.findViewById(R.id.bn_clear_txt);

        AutoCompleteTextView contact = view.findViewById(R.id.contacts);
        if (checkExternalPermission()) {
            clearText.setOnClickListener(view13 -> {
                number = null;
                contact.setText("");
                contact.setFocusableInTouchMode(true);
            });
            getActivity().getSupportLoaderManager().initLoader(1, null, this);
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


        button.setOnClickListener(view1 -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            button.setEnabled(false);
            amountEntered = amount.getText().toString();
            if (number != null) {
                if (!amountEntered.isEmpty()) {
                    String[] conversionNumber = number.split(" ");
                    number = "";
                    for (String i : conversionNumber) {
                        number += i;
                    }
                    amountEntered = "₹" + amountEntered;
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("Users").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        Log.w("Device numbers", data.child("device_number").getValue().toString());
                                        String[] conversion = data.child("device_number").getValue().toString().split(" ");
                                        String converted = "";
                                        for (String i : conversion) {
                                            converted += i;
                                        }
                                        if (converted.equals(number)) {
                                            Log.w("result", "number present");
                                            receiverKey = data.getKey();
                                            Log.w("receiverKey", receiverKey);
                                        }
                                    }
                                    if (receiverKey == null) {
                                        Toast.makeText(getContext(), "User does not have this app", Toast.LENGTH_SHORT).show();
                                        //todo receiver not found in database
                                    } else {
                                        sendNotificationToUser(senderUserID, receiverKey, phoneNumber, amountEntered);
                                        String reason=discription.getText().toString()+"";
                                        radioButtonClaim=view.findViewById(selectedId);
                                        String claimString=radioButtonClaim.getText().toString();
                                        Log.v("Getinout",claimString);
                                        amountEntered=amountEntered.substring(1);
                                        if (claimString.equals("TAKEN")){
                                            amountEntered="-"+amountEntered;
                                        }
                                        saveInLocalDatabase(number,reason,amountEntered);
                                        OneFragment.fab.setVisibility(View.VISIBLE);
                                        ((MainActivity) getActivity()).viewPager.setVisibility(View.VISIBLE);
                                        amount.setFocusable(false);
                                        contact.setFocusable(false);
                                        ((MainActivity) getActivity()).actionbar.setTitle("Gimme");
                                        getFragmentManager().beginTransaction()
                                                .remove(AddingNewContactFragment.this).commit();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
                                }
                            });
                    ((MainActivity) getActivity()).viewPager.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Amount field can't be empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Contact Field can't be empty", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void saveInLocalDatabase(String number, String reason, String amountEntered) {
        Boolean result = db.insertData(number, reason, amountEntered);
        if (result==true) {
            Toast.makeText(getContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Data Inserted Unsuccessfully", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Loader<Cursor> cursorLoader = new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        SharedPreferences sharedPref = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        HashMap<String, String> item;
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactName.add(name);
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumber.add(number);
            item = new HashMap<>();
            item.put("Name", name);
            item.put("Number", number);
            contactdetail.add(item);
            String[] conversion = number.split(" ");
            String[] conversion1 = number.split("-");
            if (conversion1.length > 1) {
                number = "";
                for (String i : conversion1) {
                    number += i;
                }
            } else if (conversion.length > 1) {
                number = "";
                for (String i : conversion) {
                    number += i;
                }
            }
            editor.putString(number, name);
            editor.apply();
            cursor.moveToNext();
        }
        Map<String, ?> keys = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE).getAll();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

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

    @Override
    public void onClick(View view) {
        int moneyInt;
        String moneyString = amount.getText().toString();
        if (moneyString.isEmpty())
            moneyInt=0;
        else
            moneyInt=Integer.parseInt(moneyString);
        switch (view.getId()) {
            case R.id.bn_amount_10:
                moneyInt+=10;
                amount.setText(moneyInt+"");
                break;
            case R.id.bn_amount_50:
                moneyInt+=50;
                amount.setText(moneyInt+"");
                break;
            case R.id.bn_amount_100:
                moneyInt+=100;
                amount.setText(moneyInt+"");
                break;
            case R.id.bn_amount_500:
                moneyInt+=500;
                amount.setText(moneyInt+"");
                break;
            case R.id.bn_amount_1000:
                moneyInt+=1000;
                amount.setText(moneyInt+"");
                break;
            default:
                break;
        }
        amount.setSelection(amount.getText().length());
    }
}
