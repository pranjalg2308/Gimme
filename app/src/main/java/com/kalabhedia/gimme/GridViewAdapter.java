package com.kalabhedia.gimme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<CardArray> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<CardArray> data = new ArrayList<CardArray>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<CardArray> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.count = (TextView) row.findViewById(R.id.count);
            holder.title = (TextView) row.findViewById(R.id.card_title);
            holder.number = (TextView) row.findViewById(R.id.card_number);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        CardArray item = data.get(position);
        int verifiedSumToDisplay = 0;
        verifiedSumToDisplay = Integer.parseInt(item.verifiedSum);
        if (verifiedSumToDisplay < 0) {
            holder.count.setBackgroundResource(R.drawable.circle_minus);
            verifiedSumToDisplay = -1 * verifiedSumToDisplay;
        }
        holder.count.setText("₹" + verifiedSumToDisplay);
        holder.title.setText(item.name);
        holder.number.setText(item.phoneNumber);
        return row;
    }

    static class ViewHolder {
        TextView count;
        TextView title;
        TextView number;
    }

}
