package com.kalabhedia.gimme;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddingNewContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ArrayList<String> contactName;
    ArrayList<String> contactNumber;
    private int READ_CONTACT_PERMISSION=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_new_contact, container, false);
        ((MainActivity) getActivity()).actionbar.setTitle("Add Bill");
        contactName = new ArrayList<>();
        contactNumber = new ArrayList<>();
        AutoCompleteTextView contact = view.findViewById(R.id.contacts);
        if (checkExternalPermission()) {
            getActivity().getSupportLoaderManager().initLoader(1, null, this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, contactName);
            contact.setThreshold(1);
            contact.setAdapter(adapter);
        } else
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Loader<Cursor> cursorLoader = new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            contactName.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contactNumber.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).tabLayout.setVisibility(View.VISIBLE);
    }

    private boolean checkExternalPermission() {
        String permission = android.Manifest.permission.READ_CONTACTS;
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
