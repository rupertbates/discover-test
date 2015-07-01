package com.theguardian.widget;

import android.support.v7.widget.RecyclerView;

public abstract class ViewStackAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
    public abstract void removeItem(int position);
}
