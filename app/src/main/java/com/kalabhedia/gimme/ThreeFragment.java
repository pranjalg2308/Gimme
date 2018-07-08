package com.kalabhedia.gimme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class ThreeFragment extends Fragment {
    View view;
    DataBaseHelper db;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
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

        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<ActivityArray>();
//        ActivityArray activityArray=new ActivityArray("6:35","Divyanshu","20","ugtv");
//        arrayOfActivity.add(activityArray);
        if (cr != null && cr.getCount() > 0) {
            while (cr.moveToNext()) {
                arrayOfActivity.add(new ActivityArray(cr.getString(0), cr.getString(1), cr.getString(2), cr.getString(3)));

            }
        }


        ActivityAdapter adapter = new ActivityAdapter(getContext(), arrayOfActivity);
        ListView listView = view.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);

    }

}
