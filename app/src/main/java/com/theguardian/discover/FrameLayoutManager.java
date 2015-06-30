package com.theguardian.discover;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class FrameLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = "FrameLayoutManager";
    private final CardAdapter adapter;
    protected int currentScroll = 0;
    private int currentScrollState;

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

        if (dy > 0) //Can't scroll up
            return 0;
        if (getItemCount() == 1) //Can't scroll the last item
            return 0;

        int delta = -dy;
        currentScroll += delta;

        View view = getTopChild();
        Log.d(TAG, "Current scroll=" + currentScroll + " child height=" + getDecoratedMeasuredHeight(view));
        if (scrolledPastDismissPoint(view) && currentScrollState == SCROLL_STATE_FLING) {
            Log.i(TAG, "Passed dismiss point and flinging, complete the dismiss");
            //view.offsetTopAndBottom(delta);
            int scrollDistance = getHeight() - currentScroll;
            scrollOff();
            return scrollDistance;
        } else {
            Log.i(TAG, "Haven't reached dismiss point, scroll");
            view.offsetTopAndBottom(delta);
        }
        return -delta;
    }

    private boolean scrolledPastDismissPoint(View view) {
        return currentScroll > getDecoratedMeasuredHeight(view) * 0.6;
    }

    private View getTopChild() {
        return getChildAt(getChildCount() - 1);
    }

    private int getTopItemIndex() {
        return getItemCount() - 1;
    }

    @Override
    public void onScrollStateChanged(int state) {
        Log.d(TAG, "Scroll state changed old=" + currentScrollState + " new=" + state);
        if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL && state == SCROLL_STATE_IDLE) {
            Log.i(TAG, "Touch Scrolling finished");
            if (!scrolledPastDismissPoint(getTopChild())) {
                snapBack();
            } else {
                scrollOff();
            }
        } else if (currentScrollState == SCROLL_STATE_FLING && state == SCROLL_STATE_IDLE && !scrolledPastDismissPoint(getTopChild())) {
            Log.i(TAG, "Fling finished before dismiss point");
            snapBack();
        }
        currentScrollState = state;
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
                Log.v(TAG, "Animating to " + animation.getAnimatedValue());
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
