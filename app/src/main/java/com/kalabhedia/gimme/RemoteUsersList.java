package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
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

public class RemoteUsersList {
    private static TreeSet<String> contactsContainingApp = new TreeSet<>();

    public static TreeSet<String> userList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Gimme", Context.MODE_PRIVATE);
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
                            FileOutputStream fileOutputStream = context.openFileOutput("Saved Contacts", Context.MODE_PRIVATE);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(contactsContainingApp);
                            objectOutputStream.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
                        Toast.makeText(context, "Unable to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
        TreeSet<String> cachedList = new TreeSet<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput("Saved Contacts");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            cachedList = (TreeSet<String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (cachedList.size() != 0) {
            return cachedList;
        }
        return null;
    }

}
