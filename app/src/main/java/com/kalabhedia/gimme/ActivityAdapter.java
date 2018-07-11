package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public ActivityAdapter(Context context, ArrayList<ActivityArray> activity) {
        super(context, 0, activity);
        this.activity = activity;
        this.context = context;
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
            db = new DataBaseHelper(getContext());
            holder = (ViewHolder) convertView.getTag();
            holder.bnAccept.setText("Accept");
            holder.bnAccept.setVisibility(View.VISIBLE);
            holder.bnAccept.setEnabled(true);

            holder.bnReject.setText("Reject");
            holder.bnReject.setVisibility(View.VISIBLE);
            holder.bnReject.setEnabled(true);

            holder.bnRefresh.setVisibility(View.VISIBLE);
            holder.bnRefresh.setEnabled(true);
        }


        db = new DataBaseHelper(getContext());

        holder.bnAccept.setOnClickListener(view -> {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            Boolean check = db.updateData(activityArray.time, "1", "1");
            Log.v("Update SQL", check.toString());
            holder.bnAccept.setText("Accepted");
            holder.bnAccept.setEnabled(false);
            holder.bnReject.setVisibility(View.GONE);
            holder.bnRefresh.setVisibility(View.GONE);
            notifyingdataChanged();
        });
        holder.bnReject.setOnClickListener(view -> {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            Boolean check = db.updateData(activityArray.time, "1", "2");
            Log.v("Update SQL", check.toString());
            holder.bnAccept.setVisibility(View.GONE);
            holder.bnReject.setEnabled(false);
            holder.bnReject.setText("Rejected");
            holder.bnRefresh.setVisibility(View.GONE);
            notifyingdataChanged();
        });

        String code1 = activityArray.code1;
        String code2 = activityArray.code2;

        String checkCode = code1 + code2;
        switch (checkCode) {
            case "10":
                holder.bnAccept.setText("Pending.....");
                holder.bnAccept.setEnabled(false);
                holder.bnReject.setVisibility(View.GONE);
                holder.bnRefresh.setVisibility(View.GONE);
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
                holder.bnRefresh.setVisibility(View.GONE);
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
            statement = "to be given to " + statement;
        } else {
            holder.im.setImageResource(R.drawable.circle_plus);
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
                arrayOfActivity.add(new ActivityArray(cr.getString(0), name, cr.getString(2), cr.getString(3), cr.getString(4), cr.getString(5)));

            }
            while (cr.moveToPrevious());
        }
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
}