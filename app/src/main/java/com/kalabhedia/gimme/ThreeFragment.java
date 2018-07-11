package com.kalabhedia.gimme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ThreeFragment extends Fragment {
    View view;
    DataBaseHelper db;
    private BroadcastReceiver mMyBroadcastReceiver;


    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mMyBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getFragmentManager().beginTransaction().detach(ThreeFragment.this).attach(ThreeFragment.this).commit();
            }
        };
        try {

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMyBroadcastReceiver, new IntentFilter("your_action"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        updateUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMyBroadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_three, container, false);
        return view;

    }

    private void updateUi() {
        db = new DataBaseHelper(getContext());
        Cursor cr = db.getAllData();

        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<>();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToLast();
            do {
                String name = ((MainActivity) getActivity()).getName(cr.getString(1));
                arrayOfActivity.add(new ActivityArray(cr.getString(0), name, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5)));

            }
            while (cr.moveToPrevious());

        }


        ActivityAdapter adapter = new ActivityAdapter(getContext(), arrayOfActivity);
        ListView listView = view.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

}
