package com.kalabhedia.gimme;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class FriendsListViewAdaper extends ArrayAdapter<FriendsArray> {

    private Context context;

    public FriendsListViewAdaper(@NonNull Context context, int resource, @NonNull List<FriendsArray> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_list_view, parent, false);
        FriendsArray currentElement = getItem(position);
        TextView tvHighlighter = convertView.findViewById(R.id.highlighter);
        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvPhonenumber = convertView.findViewById(R.id.tvPhoneNumber);
        int magnitudeColor = getMagnitudeColor(currentElement.nameFriends.charAt(0));
        GradientDrawable highlighterCircle = (GradientDrawable) tvHighlighter.getBackground();
        highlighterCircle.setColor(magnitudeColor);
        tvHighlighter.setText(currentElement.nameFriends.charAt(0) + "");
        tvPhonenumber.setText(currentElement.phoneNumber);
        tvUsername.setText(currentElement.nameFriends);
        return convertView;
    }

    private int getMagnitudeColor(char c) {
        int magnitudeColorResourceId;
        int magnitudeFloor = c % 10;
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
