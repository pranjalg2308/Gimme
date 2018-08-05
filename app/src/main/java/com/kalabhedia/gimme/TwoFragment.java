package com.kalabhedia.gimme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


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
//        if (contactsContainingApp.size() != 0) {
//                            sharebn.setVisibility(View.GONE);
//                            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, contactsContainingApp.toArray());
//                            listOfUsers.setAdapter(arrayAdapter);
//                        }
        MainActivity.Dataupdate();
        OnlineUserDataBase onlineUserDataBase = new OnlineUserDataBase(getContext());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        ArrayList<FriendsArray> cachedList = new ArrayList<FriendsArray>();

        Cursor cr = onlineUserDataBase.getAllData();
        int x = cr.getCount();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                if (sharedPreferences.getString(numberTemp, null) != null) {
                    cachedList.add(new FriendsArray((sharedPreferences.getString(numberTemp, null)), numberTemp));
//                    + "(" + cr.getString(1) + ")"
                }
                cr.moveToNext();
            }
        }
        if (cachedList.size() != 0) {
            FriendsListViewAdaper friendsListViewAdaper = new FriendsListViewAdaper(getContext(), R.layout.friends_list_view, cachedList);
            listOfUsers.setAdapter(friendsListViewAdaper);
        } else {
            sharebn.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}