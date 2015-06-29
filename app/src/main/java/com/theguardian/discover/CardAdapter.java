package com.theguardian.discover;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Random;

public class CardAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int colour = (int) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
            convertView.setOnTouchListener(new SwipeDismissTouchListener(
                    convertView,
                    null, new SwipeDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(Object token) {
                    return false;
                }

                @Override
                public void onDismiss(View view, Object token) {

                }
            }));
        }

        convertView.findViewById(R.id.text).setBackgroundColor(colour);
        return convertView;
    }
}
