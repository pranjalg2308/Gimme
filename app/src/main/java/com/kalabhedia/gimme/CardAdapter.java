package com.kalabhedia.gimme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    private List<CardArray> CardList;
    private String phoneNumber;
    private CardArray cardArray;

    public CardAdapter(Context mContext, List<CardArray> cardArrays) {
        this.mContext = mContext;
        this.CardList = cardArrays;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        cardArray = CardList.get(position);
        holder.title.setText(cardArray.name);
        int money = Integer.parseInt(cardArray.verifiedSum);
        if (money > 0) {
            holder.count.setText("₹ " + money + "");
            holder.count.setBackgroundResource(R.drawable.circle_plus);
        } else {
            holder.count.setText("₹ " + (-1 * money) + "");
            holder.count.setBackgroundResource(R.drawable.circle_minus);
        }
    }


    @Override
    public int getItemCount() {
        return CardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            count = (TextView) view.findViewById(R.id.count);
        }
    }
}

