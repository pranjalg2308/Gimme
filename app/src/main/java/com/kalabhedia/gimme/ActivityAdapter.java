package com.kalabhedia.gimme;

import android.content.Context;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityAdapter extends ArrayAdapter<ActivityArray> {

    DataBaseHelper db;

    public ActivityAdapter(Context context, ArrayList<ActivityArray> activity) {
        super(context, 0, activity);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        ActivityArray activityArray = getItem(position);
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
        }


        db = new DataBaseHelper(getContext());

        holder.bnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                Boolean check = db.updateData(activityArray.time, "1", "1");
                Log.v("Update SQL", check.toString());
                holder.bnAccept.setText("Accepted");
                holder.bnAccept.setEnabled(false);
                holder.bnReject.setVisibility(View.GONE);
                holder.bnRefresh.setVisibility(View.GONE);
            }
        });
        holder.bnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                Boolean check = db.updateData(activityArray.time, "1", "2");
                Log.v("Update SQL", check.toString());
                holder.bnAccept.setVisibility(View.GONE);
                holder.bnReject.setEnabled(false);
                holder.bnReject.setText("Rejected");
                holder.bnRefresh.setVisibility(View.GONE);
            }
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

