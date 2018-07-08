package com.kalabhedia.gimme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OneFragment extends Fragment {
    View view;
    public static FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_one, container, false);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener((View v) ->
        {
            ((MainActivity) getActivity()).viewPager.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            ((MainActivity) getActivity()).swapFragment(new com.kalabhedia.gimme.AddingNewContactFragment(), null, null);
        });
        return view;
    }
}
