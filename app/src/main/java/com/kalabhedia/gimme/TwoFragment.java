package com.kalabhedia.gimme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeSet;


public class TwoFragment extends Fragment {
    View view;
    private int READ_CONTACT_PERMISSION = 1;
    private int WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private int READ_EXTERNAL_STORAGE_PERMISSION = 3;
    private Button sharebn;

    public TwoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);
        ListView listOfUsers = view.findViewById(R.id.UserListView);
        sharebn = view.findViewById(R.id.exploreShareButton);
        sharebn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION);
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        TreeSet<String> contactsContainingApp = new TreeSet<>();
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
                            if (sharedPreferences.getString(converted, null) != null) {
                                contactsContainingApp.add(sharedPreferences.getString(converted, null));
                            }
                        }
                        try {
                            FileOutputStream fileOutputStream = getContext().openFileOutput("Saved Contacts", Context.MODE_PRIVATE);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(contactsContainingApp);
                            objectOutputStream.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                        if (contactsContainingApp.size() != 0) {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, contactsContainingApp.toArray());
                            listOfUsers.setAdapter(arrayAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
                        Toast.makeText(getContext(), "Unable to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
        TreeSet<String> cachedList=new TreeSet<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = getContext().openFileInput("Saved Contacts");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            cachedList=(TreeSet<String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (cachedList.size() != 0) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, cachedList.toArray());
            listOfUsers.setAdapter(arrayAdapter);
        } else {
            sharebn.setVisibility(View.VISIBLE);
        }
        return view;
    }
}