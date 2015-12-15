package com.sohot.hot.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DragListView extends ListView {

    private int mDownX;
    private int mDownY;
    private int mDragPosition;
    private Handler mHandler = new Handler();
    private View mStartDragItemView = null;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private int mPoint2ItemLeft;
    private int mOffset2Left;
    private int mPoint2ItemTop;
    private int mOffset2Top;
    private int mStatusHeight;
    private ImageView mDragImageView;
    private WindowManager mWindowManager;
    private Bitmap mDragBitmap;
    private int mDownScrollBorder;
    private int mUpScrollBorder;
    private OnChanageListener mDragAdapter;
    private boolean isDragAble = false;
    private static final int AUTO_SCROLL_SPEED = 560;

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;

            boolean isUpScroll = moveY > mUpScrollBorder
                    && getLastVisiblePosition() != getCount() - 1;
            boolean isDownScroll = moveY < mDownScrollBorder
                    && getFirstVisiblePosition() != 0;

            if (isUpScroll) {
                scrollY = mItemViewHeight;
                smoothScrollBy(scrollY, AUTO_SCROLL_SPEED);
                onSwapItem(moveX, moveY);
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (isDownScroll) {
                scrollY = -mItemViewHeight;
                smoothScrollBy(scrollY, AUTO_SCROLL_SPEED);
                onSwapItem(moveX, moveY);
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                mHandler.removeCallbacks(this);
            }

        }
    };
    private int mItemViewHeight;

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); // 获取状态栏的高度

    }

    public boolean isDragAble() {
        return isDragAble;
    }

    public void setDragAble(boolean isDragAble) {
        this.isDragAble = isDragAble;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isDragAble) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();

                    // 根据按下的X,Y坐标获取所点击item的position,根据item id实现锁定不可拖拽的条目
                    mDragPosition = pointToPosition(mDownX, mDownY);

                    //滑动右侧才能排序，这里*4/5暂时写死
//				if(mDownX <getChildAt(0).getWidth()*4/5){
//					return super.dispatchTouchEvent(ev);
//				}

                    if (mDragPosition == AdapterView.INVALID_POSITION) {
                        return super.dispatchTouchEvent(ev);
                    }

                    // 根据position获取该item所对应的View
                    mStartDragItemView = getChildAt(mDragPosition
                            - getFirstVisiblePosition());

                    mItemViewHeight = getChildAt(0).getHeight();// 获取item的高度（前提是每个item高度一致）

                    // 下面这几个距离大家可以参考我的博客上面的图来理解下
                    mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                    mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                    mOffset2Top = (int) (ev.getRawY() - mDownY);
                    mOffset2Left = (int) (ev.getRawX() - mDownX);

                    // 开启mDragItemView绘图缓存
                    mStartDragItemView.setDrawingCacheEnabled(true);
                    // 获取mDragItemView在缓存中的Bitmap对象
                    mDragBitmap = Bitmap.createBitmap(mStartDragItemView
                            .getDrawingCache());
                    // 根据按下的点显示item镜像
                    createDragImage(mDragBitmap, mDownY);
                    // 隐藏准备移动的item
                    mStartDragItemView.setVisibility(View.INVISIBLE);

                    // 获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                    mDownScrollBorder = mItemViewHeight;
                    // 获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                    mUpScrollBorder = (int) (getHeight() - mItemViewHeight);

                    // 这一步很关键，释放绘图缓存，避免出现重复的镜像
                    mStartDragItemView.destroyDrawingCache();
                    break;

                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(mScrollRunnable);

                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private int moveX;
    private int moveY;
    private long lastMoveTime;
    private boolean isFirstMove = true;
    private boolean isFirstPost = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDragImageView != null && isDragAble) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    if (isFirstMove) {
                        lastMoveTime = System.currentTimeMillis();
                        isFirstMove = false;
                    }
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();

                    int top = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
                    // 拖动item
                    if (top >= (mOffset2Top - mStatusHeight)) {// 这要加判断条件

                        if ((moveY < mUpScrollBorder && moveY > mDownScrollBorder)
                                || pointToPosition(moveX, moveY) == 0
                                || pointToPosition(moveX, moveY) == getCount() - 1) {
                            if (moveY < mUpScrollBorder
                                    && moveY > mDownScrollBorder)
                                isFirstPost = true;
                            onSwapItem(moveX, moveY);
                        }
                        onDragItem(top);

                    }

                    boolean canUpPost = moveY > mUpScrollBorder
                            && getLastVisiblePosition() != getCount() - 1;
                    boolean canDownPost = moveY < mDownScrollBorder
                            && getFirstVisiblePosition() != 0;

                    if (isFirstPost && (canUpPost || canDownPost)) {
                        mHandler.post(mScrollRunnable);
                        isFirstPost = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isFirstMove = true;
                    isFirstPost = true;
                    onStopDrag();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 拖动item，item镜像的位置更新
     *
     * @param y
     */
    private void onDragItem(int y) {
        mWindowLayoutParams.x = mOffset2Left;
        mWindowLayoutParams.y = y;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); // 更新镜像的位置

    }

    /**
     * 交换item,包括执行交换动画，并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private int mNextDragPosition;

    private void onSwapItem(int moveX, int moveY) {
        // 获取我们手指移动到的那个item的position
        mNextDragPosition = pointToPosition(moveX, moveY);

        if (mNextDragPosition != mDragPosition
                && mNextDragPosition != AdapterView.INVALID_POSITION) {
            mDragAdapter.onChange(mDragPosition, mNextDragPosition);

            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    long currentMoveTime = System.currentTimeMillis();
                    long duration = currentMoveTime - lastMoveTime;
                    if (duration > 300)
                        duration = 300;// 控制动画不要过慢
                    lastMoveTime = currentMoveTime;
                    animateReorder(mDragPosition, mNextDragPosition, duration);
                    // 拖动到了新的item,新的item隐藏掉
                    getChildAt(mNextDragPosition - getFirstVisiblePosition())
                            .setVisibility(View.INVISIBLE);
                    mDragPosition = mNextDragPosition;
                    return true;
                }
            });
        }
    }

    /**
     * item的动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition,
                                long duration) {
        // 找到需要做动画的view
        View targetView = getChildAt(oldPosition - getFirstVisiblePosition());

        TranslateAnimation animator = null;
        if (newPosition > oldPosition) {
            animator = new TranslateAnimation(0, 0, targetView.getHeight(), 0);
        }
        if (newPosition < oldPosition) {
            animator = new TranslateAnimation(0, 0, -targetView.getHeight(), 0);
        }

        if (animator != null) {
            animator.setDuration(duration);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // getChildAt(oldPosition -
                    // getFirstVisiblePosition()).setVisibility(View.VISIBLE);
                    // 为避免拖拽item过快导致item消失，可能仍有极少量情况会消失，但绝大多数已避免
                    if (mNextDragPosition != -1 && mNextDragPosition != 0)// 这里mNextDragPosition
                        // !=
                        // 0可能会在极端情况下出现问题，之后可能需要修改
                        getChildAt(
                                mNextDragPosition - getFirstVisiblePosition())
                                .setVisibility(View.VISIBLE);

                }

            });
            // 开启动画
            targetView.startAnimation(animator);
        }
    }

    /**
     * 停止拖拽 将镜像移除
     */
    private void onStopDrag() {
        // 显示移动的item
        getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(
                View.VISIBLE);

        removeDragImage();

    }

    /**
     * 从界面上面移除拖动镜像
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap 按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的Y坐标
     */
    private void createDragImage(Bitmap bitmap, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top
                - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; // 透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mDragAdapter = (OnChanageListener) adapter;
    }

    public interface OnChanageListener {

        /**
         * 当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
         *
         * @param oldPosition
         * @param newPosition
         */
        public void onChange(int oldPosition, int newPosition);

    }

}
