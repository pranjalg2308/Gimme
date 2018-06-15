package com.kalabhedia.gimme;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LeftMenuAdapter extends ArrayAdapter {
    private Context context;
    public LeftMenuAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        if (convertView==null){
            convertView=inflater.inflate(R.layout.row_left_drawer,parent,false);
            TextView textView=convertView.findViewById(R.id.left_menu_text);
            textView.setText((CharSequence) getItem(position));
        }
        return convertView;
    }
}
