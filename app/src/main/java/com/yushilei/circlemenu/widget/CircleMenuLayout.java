package com.yushilei.circlemenu.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yushilei.circlemenu.R;

/**
 * @author by  yushilei.
 * @time 2016/9/7 -09:59.
 * @Desc
 */
public class CircleMenuLayout extends ViewGroup implements View.OnClickListener {

    static final float CHILD_SIZE_RATE = 1 / 4F;
    static final float CENTER_SIZE_RATE = 1 / 4F;


    int centerX;
    int centerY;

    String[] strItems;
    int[] rids;

    int childSize;
    int centerSize;

    int startAngle = 0;
    int perAngle;

    int mR;

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int min = Math.min(w, h);

        mR = min / 4;
        centerX = w / 2;
        centerY = h / 2;
        Log.d(TAG, "centerX=" + centerX + ";centerY=" + centerY + ";mR=" + mR);

        childSize = (int) (min * CHILD_SIZE_RATE);
        centerSize = (int) (min * CENTER_SIZE_RATE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        int childMode = MeasureSpec.EXACTLY;
        int childMS = MeasureSpec.makeMeasureSpec(childSize, childMode);
        int centerMS = MeasureSpec.makeMeasureSpec(centerSize, childMode);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getId() == R.id.id_circle_menu_center) {
                measureChild(child, centerMS, centerMS);
            } else {
                measureChild(child, childMS, childMS);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count - 1 > 0) {
            perAngle = 360 / (count - 1);
            Log.d(TAG, "perAngle=" + perAngle);
        }
        int j = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int perWidth = child.getMeasuredWidth() / 2;
            int perHeight = child.getMeasuredHeight() / 2;
            if (child.getId() == R.id.id_circle_menu_center) {
                child.setOnClickListener(this);
                child.layout(centerX - perWidth, centerY - perHeight, centerX + perWidth, centerY + perHeight);
            } else {
                int angle = startAngle + perAngle * j;
                int[] childXY = getChildXY(angle);
                child.layout(childXY[0] - perWidth, childXY[1] - perHeight
                        , childXY[0] + perWidth, childXY[1] + perHeight);
                j++;
            }
        }
    }

    /**
     * xy[0] 边上X坐标  xy[1] 表示Y坐标
     *
     * @param mAngle
     * @return
     */
    private int[] getChildXY(int mAngle) {
        int angle = mAngle % 360;
        int quadrant = getQuadrant(angle);
        int[] xy = new int[2];
        switch (quadrant) {
            case 1:
                xy[0] = centerX + (int) (Math.cos(Math.toRadians(angle)) * mR);
                xy[1] = centerY - (int) (Math.sin(Math.toRadians(angle)) * mR);
                break;
            case 2:
                angle = angle - 90;
                xy[0] = (int) (centerX - Math.sin(Math.toRadians(angle)) * mR);
                xy[1] = (int) (centerY - Math.cos(Math.toRadians(angle)) * mR);
                break;
            case 3:
                angle = angle - 180;
                xy[0] = (int) (centerX - Math.cos(Math.toRadians(angle)) * mR);
                xy[1] = (int) (centerY + Math.sin(Math.toRadians(angle)) * mR);
                break;
            case 4:
                angle = angle - 270;
                xy[0] = (int) (centerX + Math.sin(Math.toRadians(angle)) * mR);
                xy[1] = (int) (centerY + Math.cos(Math.toRadians(angle)) * mR);
                break;
        }
        Log.d(TAG, "x=" + xy[0] + ";y=" + xy[1]);
        return xy;
    }

    String TAG = "CircleMenuLayout";

    /**
     * 获取当前角度位于哪个象限
     *
     * @param angle 偏转角度
     * @return 1-4
     */
    private int getQuadrant(int angle) {
        int i = angle % 360;
        int xiangXian = 1;
        if (i >= 0 && i < 90) {
            xiangXian = 1;
        } else if (i >= 90 && i < 180) {
            xiangXian = 2;
        } else if (i >= 180 && i < 270) {
            xiangXian = 3;
        } else {
            xiangXian = 4;
        }
        Log.d(TAG, "象限=" + xiangXian);
        return xiangXian;
    }

    float lastX;
    float lastY;
    boolean isMove = false;
    boolean isFling = false;
    long downTime;
    float mTmpAngle = 0f;
    float FLING_SPEED = 300f;

    Runnable flingRun;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                downTime = System.currentTimeMillis();
                if (isFling) {
                    removeCallbacks(flingRun);
                    isFling = false;
                    return true;
                }
                mTmpAngle = 0f;
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                double start = getAngle(lastX, lastY);
                double end = getAngle(x, y);
                int quadrant = getQuadrant(x, y);
                if (quadrant == 1 || quadrant == 4) {
                    startAngle -= end - start;
                    mTmpAngle -= end - start;
                } else {
                    startAngle += end - start;
                    mTmpAngle += end - start;
                }
                Log.d(TAG, "startAngle=" + startAngle);

                requestLayout();
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                float speedPerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - downTime);
                Log.d(TAG, "SPEED=" + speedPerSecond);
                if (Math.abs(speedPerSecond) >= FLING_SPEED) {
                    post(flingRun = new AutoFlingRunnable(speedPerSecond));
                    return true;
                }
                if (Math.abs(mTmpAngle) > 3) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class AutoFlingRunnable implements Runnable {
        float speed;

        public AutoFlingRunnable(float speed) {
            this.speed = speed;
            isFling = true;
        }

        @Override
        public void run() {
            if (Math.abs(speed) < 20) {
                isFling = false;
                return;
            }
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            startAngle += (speed / 30);
            // 逐渐减小这个值
            speed /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }
    
    private int getQuadrant(float x, float y) {
        if (x >= centerX && y <= centerY) {
            return 1;
        }
        if (x >= centerX && y > centerY) {
            return 4;
        }
        if (x < centerX && y <= centerY) {
            return 2;
        }
        return 3;
    }

    private double getAngle(float lastX, float lastY) {
        double x = lastX - centerX;
        double y = lastY - centerY;
        //弧度转角度
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    public void addItemAndText(String[] strItems, int[] rids) {
        if (strItems == null || rids == null || strItems.length != rids.length) {
            throw new RuntimeException("参数不一致");
        }
        this.strItems = strItems;
        this.rids = rids;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getId() == R.id.id_circle_menu_center) {
                continue;
            }
            removeView(child);
        }
        addItems(strItems);
    }

    private void addItems(String[] strItems) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < strItems.length; i++) {
            final int pos = i;
            View child = inflater.inflate(R.layout.circle_item, this, false);
            ImageView img = (ImageView) child.findViewById(R.id.id_circle_menu_img);
            TextView tv = (TextView) child.findViewById(R.id.id_circle_menu_text);
            img.setImageResource(rids[i]);
            tv.setText(strItems[i]);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.childClick(v, pos);
                    }
                }
            });
            addView(child);
        }
    }

    public void animStart() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "startAngle", startAngle, startAngle + 720);
        animator.setDuration(10 * 1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                requestLayout();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.centerClick(v);
        }
    }

    public interface MenuItemClickListener {
        void centerClick(View view);

        void childClick(View view, int pos);
    }

    private MenuItemClickListener mListener;

    public void setListener(MenuItemClickListener listener) {
        mListener = listener;
    }
}
