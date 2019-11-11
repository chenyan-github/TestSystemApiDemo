package com.cy.test;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author chenyan@huobi.com
 * @date 2019-11-11 17:27
 * @desp 修复android 9.0以前使用TouchDelegate点击扩大区域后再点击父控件无法响应点击事件的bug
 */
public class TouchDelegateFix extends TouchDelegate {
    /**
     * View that should receive forwarded touch events
     */
    private View mDelegateView;

    /**
     * Bounds in local coordinates of the containing view that should be mapped to the delegate
     * view. This rect is used for initial hit testing.
     */
    private Rect mBounds;

    /**
     * mBounds inflated to include some slop. This rect is to track whether the motion events
     * should be considered to be within the delegate view.
     */
    private Rect mSlopBounds;
    /**
     * True if the delegate had been targeted on a down event (intersected mBounds).
     */
    private boolean mDelegateTargeted;

    private int mSlop;

    public TouchDelegateFix(Rect bounds, View delegateView) {
        super(bounds, delegateView);
        mBounds = bounds;

        mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
        mSlopBounds = new Rect(bounds);
        mSlopBounds.inset(-mSlop, -mSlop);
        mDelegateView = delegateView;
    }



    /**
     * Will forward touch events to the delegate view if the event is within the bounds
     * specified in the constructor.
     *
     * @param event The touch event to forward
     * @return True if the event was forwarded to the delegate, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        boolean sendToDelegate = false;
        boolean hit = true;
        boolean handled = false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDelegateTargeted = mBounds.contains(x, y);//android 9.0之前mDelegateTargeted 没有复位
                sendToDelegate = mDelegateTargeted;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                sendToDelegate = mDelegateTargeted;
                if (sendToDelegate) {
                    Rect slopBounds = mSlopBounds;
                    if (!slopBounds.contains(x, y)) {
                        hit = false;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                sendToDelegate = mDelegateTargeted;
                mDelegateTargeted = false;
                break;
        }
        if (sendToDelegate) {
            final View delegateView = mDelegateView;

            if (hit) {
                // Offset event coordinates to be inside the target view
                event.setLocation(delegateView.getWidth() / 2, delegateView.getHeight() / 2);
            } else {
                // Offset event coordinates to be outside the target view (in case it does
                // something like tracking pressed state)
                int slop = mSlop;
                event.setLocation(-(slop * 2), -(slop * 2));
            }
            handled = delegateView.dispatchTouchEvent(event);
        }
        return handled;
    }
}
