package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class OneFragment extends Fragment {
    View view;
    public static FloatingActionButton fab;
    private CardAdapter cardAdapter;
    private List<CardArray> cardArrayList;
    private Context context;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        context = getContext();
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener((View v) ->
        {
            ((MainActivity) getActivity()).viewPager.setVisibility(View.GONE);
            fab.setFocusable(false);
            ((MainActivity) getActivity()).swapFragment(new com.kalabhedia.gimme.AddingNewContactFragment(), null, null);
        });

        cardArrayList = new ArrayList<>();
        cardAdapter = new CardAdapter(context, cardArrayList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);

        prepareCard();
        return view;
    }

    private void prepareCard() {
        OnlineUserDataBase db;
        db = new OnlineUserDataBase(context);
        DataBaseHelper dbSum = new DataBaseHelper(context);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        Cursor cr = db.getAllData();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                int verifiedSum = dbSum.getVerifiedSum(numberTemp);
                if (!(verifiedSum == 0)) {
                    String userName = cr.getString(0);
                    if (sharedPreferences.getString(userName, null) != null)
                        userName = sharedPreferences.getString(userName, null);
                    //TODO
                    cardArrayList.add(new CardArray(userName, (verifiedSum) + ""));
                }
                cr.moveToNext();
            }
        }
        cardAdapter.notifyDataSetChanged();

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
