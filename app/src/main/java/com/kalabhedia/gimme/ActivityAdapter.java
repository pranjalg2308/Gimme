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

public class ActivityAdapter extends ArrayAdapter<ActivityArray> {

    DataBaseHelper db;
    ArrayList<ActivityArray> activity;
    Context context;
    MainActivity mainActivity;

    public ActivityAdapter(Context context, ArrayList<ActivityArray> activity, MainActivity mainActivity) {
        super(context, 0, activity);
        this.activity = activity;
        this.context = context;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        ActivityArray activityArray = activity.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_activity, parent, false);
            holder = new ViewHolder();
            holder.im = convertView.findViewById(R.id.color_wheel);
            holder.tvOwe = convertView.findViewById(R.id.owe_text_view);
            holder.tvTime = convertView.findViewById(R.id.time_text_view);
            holder.tvMoney = convertView.findViewById(R.id.money_text_view);
            holder.tvReason = convertView.findViewById(R.id.reason_text_view);
            holder.bnAccept = convertView.findViewById(R.id.bn_accept);
            holder.bnReject = convertView.findViewById(R.id.bn_reject);
            holder.bnRefresh = convertView.findViewById(R.id.bn_refresh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.bnAccept.setText("Accept");
            holder.bnAccept.setVisibility(View.VISIBLE);
            holder.bnAccept.setEnabled(true);
            holder.bnAccept.setTextColor(Color.parseColor("#7cb342"));
            holder.bnAccept.setBackground(ContextCompat.getDrawable(context, R.drawable.activity_item_view_button_accept));

            holder.bnReject.setText("Reject");
            holder.bnReject.setVisibility(View.VISIBLE);
            holder.bnReject.setEnabled(true);

            holder.bnRefresh.setVisibility(View.VISIBLE);
            holder.bnRefresh.setEnabled(true);
        }


        db = new DataBaseHelper(getContext());
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserId", Context.MODE_PRIVATE);
        String senderKey = sharedPref.getString("currentUserId", null);

        holder.bnAccept.setOnClickListener(view -> {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            Boolean check = db.updateData(activityArray.time, "1", "1");
            Log.v("Update SQL", check.toString());
            holder.bnAccept.setText("Accepted");
            holder.bnAccept.setEnabled(false);
            holder.bnReject.setVisibility(View.GONE);
            holder.bnRefresh.setVisibility(View.GONE);
            String receiverKey = getReceiverKey(activityArray.number);
            AddingNewContactFragment.sendNotificationToUser(activityArray.time, senderKey, receiverKey, "0", "0", " ", "11");
            notifyingdataChanged();
        });
        holder.bnReject.setOnClickListener(view -> {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            Boolean check = db.updateData(activityArray.time, "2", "1");
            Log.v("Update SQL", check.toString());
            holder.bnAccept.setVisibility(View.GONE);
            holder.bnReject.setEnabled(false);
            holder.bnReject.setText("Rejected");
            holder.bnRefresh.setVisibility(View.GONE);
            String receiverKey = getReceiverKey(activityArray.number);
            AddingNewContactFragment.sendNotificationToUser(activityArray.time, senderKey, receiverKey, "0", "0", " ", "12");
            notifyingdataChanged();
        });
        holder.bnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phonenumber", null);
        String[] conversion = phoneNumber.split(" ");
        phoneNumber = "";
        for (String i : conversion) {
            phoneNumber += i;
        }
        String finalPhoneNumber = phoneNumber;
        holder.bnRefresh.setOnClickListener(view -> {
            String receiverKey = getReceiverKey(activityArray.number);
            AddingNewContactFragment.sendNotificationToUser(activityArray.time, senderKey, receiverKey, finalPhoneNumber, activityArray.money, activityArray.reason, "01");
        });


        String code1 = activityArray.code1;
        String code2 = activityArray.code2;
        String checkCode = code1 + code2;
        switch (checkCode) {
            case "10":
                holder.bnAccept.setText("Pending");
                holder.bnAccept.setEnabled(false);
                holder.bnReject.setVisibility(View.GONE);
                holder.bnRefresh.setVisibility(View.GONE);
                holder.bnAccept.setTextColor(Color.parseColor("#FFFFD800"));
                holder.bnAccept.setBackground(ContextCompat.getDrawable(context, R.drawable.activity_item_view_button_pending));
                break;
            case "01":
                holder.bnRefresh.setVisibility(View.GONE);
                break;
            case "11":
                holder.bnAccept.setText("Accepted");
                holder.bnAccept.setEnabled(false);
                holder.bnReject.setVisibility(View.GONE);
                holder.bnRefresh.setVisibility(View.GONE);
                break;
            case "12":
                holder.bnAccept.setVisibility(View.GONE);
                holder.bnReject.setEnabled(false);
                holder.bnReject.setText("Rejected");
                break;
            case "21":
                holder.bnAccept.setVisibility(View.GONE);
                holder.bnReject.setEnabled(false);
                holder.bnReject.setText("Rejected");
                holder.bnRefresh.setVisibility(View.GONE);
                break;
            default:
                break;
        }

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
//            holder.tvOwe.setTextColor(Color.parseColor("#F57F17"));
            holder.tvMoney.setTextColor(Color.parseColor("#F57F17"));
            statement = "to be given to " + statement;
        } else {
            holder.im.setImageResource(R.drawable.circle_plus);
//            holder.tvOwe.setTextColor(Color.parseColor("#7cb342"));
            holder.tvMoney.setTextColor(Color.parseColor("#7cb342"));
            statement = "to be taken from " + statement;
        }
        holder.tvMoney.setText("₹" + moneyInt);
        holder.tvOwe.setText(statement + "");
        holder.tvReason.setText(reasonStatement);

        return convertView;
    }

    private void notifyingdataChanged() {
        db = new DataBaseHelper(getContext());
        Cursor cr = db.getAllData();
        int count = 0;
        ArrayList<ActivityArray> arrayOfActivity = new ArrayList<>();
        if (cr != null && cr.getCount() > 0) {
            cr.moveToLast();
            do {
                String phoneNumber = cr.getString(1);
                String[] conversionNumber = phoneNumber.split(" ");
                phoneNumber = "";
                for (String i : conversionNumber) {
                    phoneNumber += i;
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences("Gimme", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString(phoneNumber, null);
                if (name == null) {
                    name = phoneNumber;
                }
                if ((cr.getString(4) + cr.getString(5)).equals("01"))
                    count++;
                arrayOfActivity.add(new ActivityArray(cr.getString(0), name, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5), phoneNumber));
            }
            while (cr.moveToPrevious());
        }
        if (!(count == 0))
            mainActivity.tabLayout.getTabAt(1).setText("Activity(" + count + ")");
        else
            mainActivity.tabLayout.getTabAt(1).setText("Activity");

        activity.clear();
        activity.addAll(arrayOfActivity);
        notifyDataSetChanged();
    }

    private String formatDate(String dateStr) {
        long yourmilliseconds = Long.parseLong(dateStr);
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        Date resultdate = new Date(yourmilliseconds);
        return sdf.format(resultdate);
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
}