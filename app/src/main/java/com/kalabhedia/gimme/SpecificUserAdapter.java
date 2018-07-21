package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SpecificUserAdapter extends ArrayAdapter<ActivityArray> {

    DataBaseHelper db;
    ArrayList<ActivityArray> activity;
    Context context;


    public SpecificUserAdapter(@NonNull Context context, ArrayList<ActivityArray> activity) {

        super(context, 0, activity);
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SpecificUserAdapter.ViewHolder holder;
        ActivityArray activityArray = activity.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_activity, parent, false);
            holder = new SpecificUserAdapter.ViewHolder();
            holder.im = convertView.findViewById(R.id.color_wheel);
            holder.tvOwe = convertView.findViewById(R.id.owe_text_view);
            holder.tvTime = convertView.findViewById(R.id.time_text_view);
            holder.tvMoney = convertView.findViewById(R.id.money_text_view);
            holder.tvReason = convertView.findViewById(R.id.reason_text_view);
            holder.bnAccept = convertView.findViewById(R.id.bn_accept);
            holder.bnReject = convertView.findViewById(R.id.bn_reject);
            holder.bnRefresh = convertView.findViewById(R.id.bn_refresh);
            holder.bnAccept.setVisibility(View.GONE);
            holder.bnRefresh.setVisibility(View.GONE);
            holder.bnReject.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (SpecificUserAdapter.ViewHolder) convertView.getTag();
        }


        db = new DataBaseHelper(getContext());
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserId", Context.MODE_PRIVATE);
        String senderKey = sharedPref.getString("currentUserId", null);


        holder.tvTime.setText(formatDate(activityArray.time));
        String moneyString = activityArray.money;
        String statement = activityArray.name;
        String reasonStatement = activityArray.reason;
        if (reasonStatement.trim().isEmpty()) {
            reasonStatement = " ";
        } else
            reasonStatement = "For " + reasonStatement;
        int moneyInt = Integer.parseInt(moneyString);
        if (moneyInt < 0) {
            moneyInt = (-1) * moneyInt;
            holder.im.setImageResource(R.drawable.circle_minus);
            holder.tvMoney.setTextColor(Color.parseColor("#F57F17"));
            statement = "to be given";
        } else {
            holder.im.setImageResource(R.drawable.circle_plus);
            holder.tvMoney.setTextColor(Color.parseColor("#7cb342"));
            statement = "to be taken";
        }
        holder.tvMoney.setText("₹" + moneyInt);
        holder.tvOwe.setText(statement + "");
        holder.tvReason.setText(reasonStatement);

        return convertView;
    }


    private String formatDate(String dateStr) {
        long yourmilliseconds = Long.parseLong(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        Date resultdate = new Date(yourmilliseconds);
        return sdf.format(resultdate);
    }

    public String getReceiverKey(String user) {
        OnlineUserDataBase dbUser = new OnlineUserDataBase(getContext());
        Cursor cr = dbUser.getAllData();
        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            while (!cr.isAfterLast()) {
                String numberTemp = cr.getString(0);
                if (numberTemp.equals(user))
                    return cr.getString(1);
                cr.moveToNext();
            }
        }
        return null;
    }

    static class ViewHolder {
        private ImageView im;
        private TextView tvOwe;
        private TextView tvTime;
        private TextView tvMoney;
        private TextView tvReason;
        private Button bnAccept;
        private Button bnReject;
        private Button bnRefresh;
    }

}
