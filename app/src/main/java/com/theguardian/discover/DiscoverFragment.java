package com.theguardian.discover;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DiscoverFragment extends Fragment {
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    public DiscoverFragment() {
    }

    public int getRandomColour() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.inject(this, frameLayout);
        //setupItems(relativeLayout);
        CardAdapter adapter = new CardAdapter();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new FrameLayoutManager(adapter));
        recyclerView.setAdapter(adapter);

        return frameLayout;
    }

    private void setupItems(final RelativeLayout relativeLayout) {
        // Set up ListView example
        String[] items = new String[20];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Item " + (i + 1);
        }

        for (int i = 0; i < items.length; i++) {
            final View dismissableView = LayoutInflater.from(getActivity()).inflate(R.layout.card_layout, relativeLayout, false);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dismissableView.setLayoutParams(params);
            dismissableView.findViewById(R.id.text).setBackgroundColor(getRandomColour());
            dismissableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),
                            "Clicked a card",
                            Toast.LENGTH_SHORT).show();
                }
            });
            // Create a generic swipe-to-dismiss touch listener.
            dismissableView.setOnTouchListener(new SwipeDismissTouchListener(
                    dismissableView,
                    null,
                    new SwipeDismissTouchListener.DismissCallbacks() {
                        @Override
                        public boolean canDismiss(Object token) {
                            return true;
                        }

                        @Override
                        public void onDismiss(View view, Object token) {
                            relativeLayout.removeView(view);
                        }
                    }));
            relativeLayout.addView(dismissableView);
        }
    }
}
