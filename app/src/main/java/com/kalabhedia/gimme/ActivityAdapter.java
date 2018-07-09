package com.kalabhedia.gimme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityAdapter extends ArrayAdapter<ActivityArray> {

    public ActivityAdapter(Context context, ArrayList<ActivityArray> activity){
        super(context,0,activity);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ActivityArray activityArray=getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_activity,parent,false);
        }
        ImageView im=convertView.findViewById(R.id.color_wheel);
        TextView tvOwe = convertView.findViewById(R.id.owe_text_view);
        TextView tvTime = convertView.findViewById(R.id.time_text_view);
        TextView tvMoney = convertView.findViewById(R.id.money_text_view);
        TextView tvReason = convertView.findViewById(R.id.reason_text_view);
        tvTime.setText(formatDate(activityArray.time));
        String moneyString = activityArray.money;
        String statement = activityArray.name;
        String reasonStatement = activityArray.reason;
        if (reasonStatement.trim().isEmpty()) {
            reasonStatement = "     -----------------------";
        }
        int moneyInt=Integer.parseInt(moneyString);
        if (moneyInt<0){
            moneyInt=(-1)*moneyInt;
            im.setImageResource(R.drawable.circle_minus);
            statement = "is to be given to " + statement;
        } else {
            im.setImageResource(R.drawable.circle_plus);
            statement = "is to be taken from " + statement;
        }
        tvMoney.setText("₹"+moneyInt);
        tvOwe.setText(statement + "");
        tvReason.setText("For " + reasonStatement);

        return convertView;
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }
        return "";
    }
}

