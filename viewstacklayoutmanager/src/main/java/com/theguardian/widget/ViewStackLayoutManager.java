package com.theguardian.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class ViewStackLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = "ViewStackLayoutManager";
    public static final int OFFSET_MULTIPLIER = 20;
    private final ViewStackAdapter adapter;
    protected int currentScroll = 0;
    private int currentScrollState;
    private int numberDismissed = 0;

    public ViewStackLayoutManager(ViewStackAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int topItem = numberDismissed;
        int nextItem = Math.min(5, getItemCount() - 1);

        detachAndScrapAttachedViews(recycler);

        for (int i = nextItem; i >= topItem; i--) {
            Log.d(TAG, "Adding view for item " + i);
            View viewForPosition = recycler.getViewForPosition(i);
            viewForPosition.setMinimumWidth(getWidth());
            addView(viewForPosition);
            measureChildWithMargins(viewForPosition, 0, 0);
            int offset = getOffset(i);
            layoutDecorated(viewForPosition, offset, offset, getDecoratedMeasuredWidth(viewForPosition) - offset, getDecoratedMeasuredHeight(viewForPosition) + offset);
        }
    }

    /**
     * Resize the cards to the correct height when a card is dismissed or brought back
     */
    public void relayout() {
        int topItem = getChildCount() - (numberDismissed + 1);
        int bottomItem = 0;
        for (int i = topItem; i >= bottomItem; i--) {
            View viewForPosition = getChildAt(i);
            int offset = getOffset(topItem - i);
            layoutDecorated(viewForPosition, offset, offset, getDecoratedMeasuredWidth(viewForPosition) - offset, getDecoratedMeasuredHeight(viewForPosition) + offset);
        }
    }

    public int getOffset(int index) {
        return (index * OFFSET_MULTIPLIER) + OFFSET_MULTIPLIER;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {

        if (dx - currentScroll > 0) { //If we are trying to swipe a card to the right from its starting point
            if (numberDismissed > 0) { //If there are any dismissed cards then bring them back
                numberDismissed--;
                Log.i(TAG, "Swiping card back from offscreen numberdismissed = " + numberDismissed);
                currentScroll = getWidth();
            } else
                return 0; //There are no dismissed cards so we can't swipe right
        }

        if (getItemCount() < 2) //Can't scroll the last item
            return 0;

        Log.v(TAG, "getItemCount=" + getItemCount());

        int delta = -dx;
        currentScroll += delta;

        View view = getTopChild();
        Log.d(TAG, "Current scroll=" + currentScroll + " child width=" + getDecoratedMeasuredWidth(view));
        if (scrollingRight(dx) && scrolledPastDismissPoint(view) && currentScrollState == SCROLL_STATE_FLING) {
            Log.i(TAG, "Passed dismiss point and flinging off screen, complete the dismiss");
            int scrollDistance = getWidth() - currentScroll;
            scrollOff();
            return scrollDistance;
        } else if (scrollingLeft(dx) && !scrolledPastDismissPoint(view) && currentScrollState == SCROLL_STATE_FLING) {
            Log.i(TAG, "Passed dismiss point and flinging back from off screen, complete the snap back");
            snapBack();
            return currentScroll;
        } else {
            Log.d(TAG, "Haven't reached dismiss point, scroll");
            view.offsetLeftAndRight(delta);
        }
        return -delta;
    }

    private boolean scrollingLeft(int dx) {
        return !scrollingRight(dx);
    }

    private boolean scrollingRight(int dx) {
        return dx < 0;
    }

    private boolean scrolledPastDismissPoint(View view) {
        return currentScroll > getDecoratedMeasuredWidth(view) * 0.60;
    }

    private View getTopChild() {
        return getChildAt(getChildCount() - (1 + numberDismissed));
    }


    private void removeItem() {
        numberDismissed++;
        relayout();
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
        smoothScrollToPosition(getWidth(), new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentScroll = 0;
                //adapter.removeItem(getTopItemIndex());
                removeItem();
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
        int offset = getOffset(numberDismissed);
        Log.i(TAG, "Snap back offset = " + offset);
        smoothScrollToPosition(offset, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentScroll = 0;
                relayout();
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
        int duration = 150;
        Log.d(TAG, "Position=" + position + " currentScroll=" + currentScroll + " duration=" + duration);
        ValueAnimator animator = ValueAnimator.ofInt(currentScroll, position)
                .setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.v(TAG, "Animating to " + animation.getAnimatedValue());
                View topChild = getTopChild();
                int left = (int) animation.getAnimatedValue();
                int right = left + topChild.getWidth();
                topChild.setLeft(left);
                topChild.setRight(right);
            }
        });
        animator.addListener(listener);
        animator.start();
    }
}
