package com.kalabhedia.gimme;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;


public class OneFragment extends Fragment {
    View view;
    public static FloatingActionButton fab;
    private Context context;
    private Button allSettled;
    private GridView gridView;
    private GridViewAdapter gridAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_one, container, false);
        context = getContext();
        allSettled = (Button) view.findViewById(R.id.allSettled);

        gridView = view.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(context, R.layout.friends_card, getData());
        gridView.setAdapter(gridAdapter);
        fab = view.findViewById(R.id.fab);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CardArray item = (CardArray) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, ShowSpecificUser.class);
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber", item.phoneNumber);
                bundle.putString("amount", item.verifiedSum);
                startActivity(intent);
            }
        });
        fab.setOnClickListener((View v) ->
        {
            ((MainActivity) getActivity()).viewPager.setVisibility(View.GONE);
            fab.setFocusable(false);
            ((MainActivity) getActivity()).swapFragment(new com.kalabhedia.gimme.AddingNewContactFragment(), null, null);
        });

        return view;
    }

    private ArrayList<CardArray> getData() {
        final ArrayList<CardArray> cardContent = new ArrayList<>();
        OnlineUserDataBase db = new OnlineUserDataBase(context);

        DataBaseHelper dbSum = new DataBaseHelper(context);
        Cursor cr = db.getAllData();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                int verifiedSum;
                verifiedSum = dbSum.getVerifiedSum(numberTemp);
                if (!(verifiedSum == 0)) {
                    String userName = cr.getString(0);
                    cardContent.add(new CardArray(userName, (verifiedSum) + "", cr.getString(0)));
                }
                cr.moveToNext();
            }
        }
        if (cardContent.size() == 0)
            allSettled.setVisibility(View.VISIBLE);
        else
            allSettled.setVisibility(View.GONE);
        return cardContent;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
