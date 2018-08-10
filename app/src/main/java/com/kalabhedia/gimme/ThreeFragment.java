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
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class ThreeFragment extends Fragment {
    View view;
    DataBaseHelper db;
    int count = 0;
    private BroadcastReceiver mMyBroadcastReceiver;
    private Button bnNoActivity;

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
        MainActivity mainActivity = (MainActivity) getActivity();
        if (!(count == 0))
            mainActivity.tabLayout.getTabAt(1).setText("Activity(" + count + ")");
        else
            mainActivity.tabLayout.getTabAt(1).setText("Activity");
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
        bnNoActivity = view.findViewById(R.id.noActivity);
        return view;

    }

    private void updateUi() {
        db = new DataBaseHelper(getContext());
        Cursor cr = db.getAllData();
        count = 0;
        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<>();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToLast();
            do {
                String number = cr.getString(1);

                String name = ((MainActivity) getActivity()).getName(cr.getString(1));
                String check = (cr.getString(4) + cr.getString(5));
                if (check.equals("01"))
                    count++;
                if (check.equals("31") || check.equals("13")) {
                    db = new DataBaseHelper(getContext());
                    db.getWritableDatabase();
                    db.deleteUserData(number);
                }
                arrayOfActivity.add(new ActivityArray(cr.getString(0), name, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), number));
            }
            while (cr.moveToPrevious());

        }
        if (arrayOfActivity.size() == 0)
            bnNoActivity.setVisibility(View.VISIBLE);
        else
            bnNoActivity.setVisibility(View.GONE);
        ActivityAdapter adapter = new ActivityAdapter(getContext(), arrayOfActivity, (MainActivity) getActivity());
        ListView listView = view.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


}
