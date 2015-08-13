package com.theguardian.discover;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theguardian.widget.ViewStackAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CardAdapter extends ViewStackAdapter<CardViewHolder> {
    private static final String TAG = "CardAdapter";
    private List<Integer> items = new ArrayList<>(5);

    public CardAdapter(){
        for (int i = 0; i < 6; i++) {
            items.add(getColour());
        }
    }
    public static int getColour() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "Creating view in CardAdapter");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        CardViewHolder holder = new CardViewHolder(view);
        holder.itemView.setBackgroundColor(items.get(i));
        holder.textView.setText("Item " + i);
        return holder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        Log.d(TAG, "Reusing view in CardAdapter");
        cardViewHolder.itemView.setBackgroundColor(items.get(i));
        cardViewHolder.textView.setText("Item " + i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position){
        items.remove(position);
        notifyDataSetChanged();
    }

}
