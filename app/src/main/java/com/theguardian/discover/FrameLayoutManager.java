package com.theguardian.discover;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public class FrameLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = "FrameLayoutManager";
    private final CardAdapter adapter;
    protected int currentScroll = 0;

    public FrameLayoutManager(CardAdapter cardAdapter) {
        this.adapter = cardAdapter;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getItemCount(); i++) {
            View viewForPosition = recycler.getViewForPosition(i);
            addView(viewForPosition);
            measureChildWithMargins(viewForPosition, 0, 0);
            layoutDecorated(viewForPosition, 0, 0, getWidth(), getWidth());
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,
                                  RecyclerView.State state) {

        if(dy > 0) //Can't scroll up
            return 0;
        if(getItemCount() == 1) //Can't scroll the last item
            return 0;

        int delta = -dy;
        currentScroll += delta;

        View view = getTopChild();
        Log.d(TAG, "Current scroll=" + currentScroll + " child height=" + getDecoratedMeasuredHeight(view));
        if (currentScroll > getDecoratedMeasuredHeight(view) / 2) {
            Log.i(TAG, "More than halfway, snap");
            view.offsetTopAndBottom(delta);
        } else {
            Log.i(TAG, "Less than halfway, scroll");
            view.offsetTopAndBottom(delta);
        }
        return -delta;
    }

    private View getTopChild() {
        return getChildAt(getChildCount() - 1);
    }

    private int getTopItemIndex() {
        return getItemCount() - 1;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            Log.i(TAG, "Scrolling finished");
            if (currentScroll < getDecoratedMeasuredHeight(getTopChild()) * 0.75) {
                snapBack();
            } else {
                scrollOff();
            }
        } else if(state == AbsListView.OnScrollListener.SCROLL_STATE_FLING){

        }
    }

    private void scrollOff() {
        Log.i(TAG, "Scroll off");
        smoothScrollToPosition(getHeight(), new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentScroll = 0;
                adapter.removeItem(getTopItemIndex());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void snapBack() {
        Log.i(TAG, "Snap back");
        smoothScrollToPosition(0, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentScroll = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void smoothScrollToPosition(int position, Animator.AnimatorListener listener) {
        int duration = 150; // Math.abs(position - currentScroll);
        Log.d(TAG, "Position=" + position + " currentScroll=" + currentScroll + " duration=" + duration);
        ValueAnimator animator = ValueAnimator.ofInt(currentScroll, position)
                .setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "Animating to " + animation.getAnimatedValue());
                getTopChild().setTop((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(listener);
        animator.start();
    }


    int scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "ScrollBy " + dy);
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        final int absDy = Math.abs(dy);
        return absDy;
    }
}
