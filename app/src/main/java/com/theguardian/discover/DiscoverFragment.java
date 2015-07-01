package com.theguardian.discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.theguardian.widget.ViewStackLayoutManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DiscoverFragment extends Fragment {
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    public DiscoverFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.inject(this, frameLayout);
        CardAdapter adapter = new CardAdapter();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new ViewStackLayoutManager(adapter));
        recyclerView.setAdapter(adapter);

        return frameLayout;
    }
}
