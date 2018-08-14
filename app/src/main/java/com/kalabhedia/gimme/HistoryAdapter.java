package com.kalabhedia.gimme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryArray> {
    private Context context;
    private TextView historyStatement;
    private TextView time;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull List<HistoryArray> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_view, parent, false);
        HistoryArray currentElement = getItem(position);
        historyStatement = convertView.findViewById(R.id.tvHistoryStatement);
        time = convertView.findViewById(R.id.time_text_view);
        int amount = Integer.parseInt(currentElement.money);
        String statement = "";
        String phoneNumber = currentElement.name;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString(phoneNumber, null);
        if (amount > 0) {
            statement = "₹" + amount + " was given to " + phoneNumber + " for settling up";
        } else
            statement = "₹" + (-1 * amount) + " was taken from " + phoneNumber + " for settling up";

        historyStatement.setText(statement);
        time.setText(formatDate(currentElement.time));
        return convertView;
    }

    private String formatDate(String dateStr) {
        long yourmilliseconds = Long.parseLong(dateStr);
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        Date resultdate = new Date(yourmilliseconds);
        return sdf.format(resultdate);
    }
}
