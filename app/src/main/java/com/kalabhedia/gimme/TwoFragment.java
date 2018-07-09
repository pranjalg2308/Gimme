package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TreeSet;


public class TwoFragment extends Fragment {
    View view;

    public TwoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);
        TextView error = view.findViewById(R.id.errorTextView);
        ListView listOfUsers = view.findViewById(R.id.UserListView);
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
                        if (contactsContainingApp.size() != 0) {
                            error.setVisibility(View.GONE);
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
        return view;
    }
}