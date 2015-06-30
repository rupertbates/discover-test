package com.theguardian.discover;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    public final TextView textView;

    public CardViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.text);
    }
}
