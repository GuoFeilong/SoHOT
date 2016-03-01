package com.sohot.hot.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ScrollView;

import tools.Logger;


/**
 * Created by jsion on 16/3/1.
 */
public class MyScrollViewF extends ScrollView {
    /**
     * 左滑关闭事件的最大距离
     */
    private static final int LEFT_MAX_DISTANCE = 30;
    /**
     * 垂直误差的范围最大距离
     */
    private static final int VERTICAL_MAX_DISTANCE = 30;
    /**
     * 滑动距离的最小关闭距离
     */
    private static final int FINISH_MIN_DISTANCE = 30;
    /**
     * 最小滑动速度
     */
    private static final int MOVE_MIN_SPEED = 150;

    private int lastX;
    private int lastY;

    private int downX;
    private int downY;


    public interface NotifyActiviyFinishListener {
        void activityFinish();
    }

    private NotifyActiviyFinishListener notifyActiviyFinishListener;

    public void setNotifyActiviyFinishListener(NotifyActiviyFinishListener notifyActiviyFinishListener) {
        this.notifyActiviyFinishListener = notifyActiviyFinishListener;
    }

    public MyScrollViewF(Context context) {
        super(context);
    }

    public MyScrollViewF(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollViewF(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();

        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(e);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) e.getX();
                downY = (int) e.getY();

                Logger.d("downX=" + downX + ">>>>downY=" + downY);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                lastX = x;
                lastY = y;

                Logger.d("lastX=" + lastX + ">>>>lastY=" + lastY);

                // 计算水平滑动速度
                velocityTracker.computeCurrentVelocity(1000);
                int xSpeed = (int) velocityTracker.getXVelocity();


                if (downX <= LEFT_MAX_DISTANCE && Math.abs(lastX - downX) >= FINISH_MIN_DISTANCE && Math.abs(lastY - downY) <= VERTICAL_MAX_DISTANCE) {
                    if (notifyActiviyFinishListener != null) {
                        notifyActiviyFinishListener.activityFinish();
                        Logger.d("执行到这里了....");
                    }
                }

                Logger.e("xSpeed=" + xSpeed);


                break;
        }
        return super.onTouchEvent(e);
    }
}

