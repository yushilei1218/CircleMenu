package com.yushilei.circlemenu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author by  yushilei.
 * @time 2016/9/7 -13:50.
 * @Desc
 */
public class ParentLayout extends RelativeLayout {
    String TAG = "ParentLayout";

    public ParentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.d(TAG, TAG + " dispatchTouchEvent=" + b);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        Log.d(TAG, TAG + " onTouchEvent=" + b);
        return b;
    }
}
