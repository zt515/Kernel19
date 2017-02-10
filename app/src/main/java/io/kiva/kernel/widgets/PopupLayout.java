package io.kiva.kernel.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import static android.view.View.MeasureSpec.EXACTLY;

/**
 * This class is decompiled from AIDE.
 */
public class PopupLayout extends ViewGroup {
    private int animationPosition;
    private float dividerSpeed = 1.0f;
    private int dividerTouchSize = 30;
    private int dragPosition;
    private float horizontalSplitRatio = 0.33f;
    private boolean isDragging;
    private boolean isHorizontal;
    private boolean isSplit;
    private boolean isSwipeEnabled;
    private OnPopLayoutListener listener;
    private float verticalSplitRatio = 0.5f;

    public PopupLayout(Context context) {
        super(context);
    }

    public PopupLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PopupLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public static float getWindowWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return ((float) dm.widthPixels / context.getResources().getDisplayMetrics().density);
    }

    public void setOnSplitChangeListener(OnPopLayoutListener listener) {
        this.listener = listener;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        updateChildVisibilities();
        this.isHorizontal = splitHorizontalByDefault();
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.isSwipeEnabled = swipeEnabled;
    }

    public boolean isSplit() {
        return this.isSplit;
    }

    public boolean splitHorizontalByDefault() {
        return getWindowWidth(getContext()) >= 650.0f;
    }

    private boolean isVertical() {
        return !isHorizontal();
    }

    public boolean isHorizontal() {
        return this.isHorizontal;
    }

    public View getBottomView() {
        return getChildAt(2);
    }

    public View getTopView() {
        return getChildAt(0);
    }

    public View getSeparatorView() {
        return getChildAt(1);
    }

    public void openSplit(boolean withAnim) {
        openSplit(this.isHorizontal, withAnim);
    }

    public void openSplit(boolean isHorizontal, boolean withAnim) {
        this.isHorizontal = isHorizontal;
        if (withAnim) {
            if (!this.isSplit || this.isDragging) {
                ObjectAnimator ofInt = ObjectAnimator.ofInt(this, "animationPosition", getDragPosition(),
                        getDividerPosition());
                ofInt.setDuration(getAnimationDuration(getDragPosition(), getDividerPosition()));
                ofInt.start();
                ofInt.addListener(new AnimatorListenerAdapter() {

                    public void onAnimationEnd(Animator animator) {
                        animationPosition = -1;
                        postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (listener != null) {
                                                listener.onPopLayout(isSplit);
                                            }
                                        }
                                    }
                                , 50);
                    }
                });
                this.isSplit = true;
                updateChildVisibilities();
            }
        } else if (!this.isSplit) {
            this.isSplit = true;
            this.animationPosition = -1;
            updateChildVisibilities();
            if (this.listener != null) {
                this.listener.onPopLayout(this.isSplit);
            }
        }
        this.isDragging = false;
    }

    private long getAnimationDuration(int dragPosition, int dividerPosition) {
        return (long) ((float) Math.abs(dragPosition - dividerPosition) / getResources().getDisplayMetrics().density
                / this.dividerSpeed);
    }

    public void closeSplit(boolean showAnimation) {
        closeSplit(showAnimation, null);
    }

    private void closeSplit(boolean showAnimation, final Runnable runnable) {
        if (showAnimation) {
            if (this.isSplit || this.isDragging) {
                ObjectAnimator ofInt = ObjectAnimator.ofInt(this, "animationPosition", getDragPosition(), 0);
                ofInt.setDuration(getAnimationDuration(getDragPosition(), 0));
                ofInt.start();
                ofInt.addListener(new AnimatorListenerAdapter() {

                    public void onAnimationEnd(Animator animator) {
                        PopupLayout.this.isSplit = false;
                        updateChildVisibilities();
                        if (listener != null) {
                            listener.onPopLayout(PopupLayout.this.isSplit);
                        }
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
            }
        } else if (this.isSplit) {
            this.isSplit = false;
            updateChildVisibilities();
            if (this.listener != null) {
                this.listener.onPopLayout(this.isSplit);
            }
        }
        this.isDragging = false;
    }

    public void toggleSplit() {
        if (isSplit() && isVertical()) {
            closeSplit(true);
        } else if (isSplit() && isHorizontal()) {
            setOrientation(false, true);
        } else {
            openSplit(splitHorizontalByDefault(), true);
        }
    }

    private void setOrientation(final boolean isHorizontal, boolean isSplit) {
        if (this.isHorizontal == isHorizontal) {
            return;
        }
        if (isSplit && this.isSplit) {
            closeSplit(true, new Runnable() {
                @Override
                public void run() {
                    openSplit(isHorizontal, true);
                }
            });
            return;
        }
        this.isHorizontal = isHorizontal;
        requestLayout();
        if (this.listener != null) {
            this.listener.onPopLayout(this.isSplit);
        }
    }

    private void updateChildVisibilities() {
        View bottomView = getBottomView();
        bottomView.setVisibility((this.isSplit || this.isDragging) ? VISIBLE : GONE);
        View separatorView = getSeparatorView();
        separatorView.setVisibility((this.isSplit || this.isDragging) ? VISIBLE : GONE);
    }

    public void setAnimationPosition(int animationPosition) {
        this.animationPosition = animationPosition;
        requestLayout();
    }

    private int getDividerPosition() {
        return getDividerPosition(getWidth(), getHeight());
    }

    private int getDividerPosition(int width, int height) {
        if (this.isHorizontal) {
            return (int) (this.horizontalSplitRatio * (float) width);
        }
        return (int) (this.verticalSplitRatio * (float) height);
    }

    private int getCurrentDividerPosition(int width, int height) {
        if (this.isDragging) {
            return getDragPosition();
        }
        if (this.animationPosition < 0) {
            return getDividerPosition(width, height);
        }
        return this.animationPosition;
    }

    private int getDragPosition() {
        if (this.isDragging) {
            return Math.min(this.dragPosition, getDividerPosition());
        }
        return this.isSplit ? getDividerPosition() : 0;
    }

    private int getDragDistance() {
        if (this.isSplit) {
            return getDividerPosition() - getDragPosition();
        }
        return getDragPosition();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View topView = getTopView();
        View bottomView = getBottomView();
        View separatorView = getSeparatorView();
        int deltaWidget = r - l;
        int deltaHeight = b - t;
        if (bottomView.getVisibility() == GONE) {
            topView.layout(0, 0, deltaWidget, deltaHeight);
        } else if (this.isHorizontal) {
            int bottomViewMeasuredWidth = bottomView.getMeasuredWidth();
            int separatorViewMeasuredWidth = separatorView.getMeasuredWidth();
            topView.layout(bottomViewMeasuredWidth + separatorViewMeasuredWidth, 0, deltaWidget, deltaHeight);
            separatorView.layout(bottomViewMeasuredWidth, 0, bottomViewMeasuredWidth + separatorViewMeasuredWidth,
                    deltaHeight);
            bottomView.layout(0, 0, bottomViewMeasuredWidth, deltaHeight);
        } else {
            int topViewMeasuredHeight = topView.getMeasuredHeight();
            int separatorViewMeasuredHeight = separatorView.getMeasuredHeight();
            topView.layout(0, 0, deltaWidget, topViewMeasuredHeight);
            separatorView.layout(0, topViewMeasuredHeight, deltaWidget,
                    topViewMeasuredHeight + separatorViewMeasuredHeight);
            bottomView.layout(0, topViewMeasuredHeight + separatorViewMeasuredHeight, deltaWidget, deltaHeight);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View topView = getTopView();
        View bottomView = getBottomView();
        View separatorView = getSeparatorView();
        MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (bottomView.getVisibility() == GONE) {
            topView.measure(widthMeasureSpec, heightMeasureSpec);
        } else if (this.isHorizontal) {

            bottomView.measure(MeasureSpec.makeMeasureSpec(getCurrentDividerPosition(w, h), EXACTLY),
                    heightMeasureSpec);
            topView.measure(MeasureSpec.makeMeasureSpec(w - bottomView.getMeasuredWidth(), EXACTLY), heightMeasureSpec);
            separatorView.measure(LayoutParams.WRAP_CONTENT, heightMeasureSpec);
        } else {
            bottomView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getCurrentDividerPosition(w, h), EXACTLY));
            topView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h - bottomView.getMeasuredHeight(), EXACTLY));
            separatorView.measure(widthMeasureSpec, LayoutParams.WRAP_CONTENT);
        }
        setMeasuredDimension(w, h);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return getDragStartOrientation(motionEvent) != null || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        Boolean dragStartOrientation = getDragStartOrientation(motionEvent);
        if (dragStartOrientation != null) {
            startDragging(motionEvent, dragStartOrientation);
            return true;
        } else if (!this.isDragging) {
            return super.onTouchEvent(motionEvent);
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                updateDragging(motionEvent);
            }
            if (motionEvent.getAction() != MotionEvent.ACTION_CANCEL
                    && motionEvent.getAction() != MotionEvent.ACTION_UP) {
                return true;
            }
            stopDragging(motionEvent);
            return true;
        }
    }

    private Boolean getDragStartOrientation(MotionEvent motionEvent) {
        if (this.isSwipeEnabled && !this.isSplit && motionEvent.getAction() == 0 && motionEvent.getPointerCount() == 1
                && motionEvent.getX() < (float) this.dividerTouchSize * getResources().getDisplayMetrics().density
                && splitHorizontalByDefault()) {
            return true;
        }
        if (this.isSwipeEnabled && this.isSplit && this.isHorizontal && motionEvent.getAction() == 0
                && motionEvent.getPointerCount() == 1
                && motionEvent.getY() > getResources().getDisplayMetrics().density * 50.0f
                && motionEvent.getY() < ((float) getHeight()) - getResources().getDisplayMetrics().density * 50.0f
                && Math.abs(motionEvent.getX()
                - (float) getWidth() * this.horizontalSplitRatio) < (float) this.dividerTouchSize
                * getResources().getDisplayMetrics().density) {
            return true;
        }
        if (this.isSwipeEnabled && !this.isSplit && motionEvent.getAction() == 0 && motionEvent.getPointerCount() == 1
                && motionEvent.getY() > ((float) getHeight())
                - (float) this.dividerTouchSize * getResources().getDisplayMetrics().density
                && (float) getHeight() > 150.0f * getResources().getDisplayMetrics().density) {
            return false;
        }
        if (!this.isSwipeEnabled || !this.isSplit || this.isHorizontal || motionEvent.getAction() != 0
                || motionEvent.getPointerCount() != 1
                || motionEvent.getX() >= ((float) getWidth()) - (getResources().getDisplayMetrics().density * 50.0f)
                || motionEvent.getX() <= getResources().getDisplayMetrics().density * 50.0f
                || Math.abs(motionEvent.getY()
                - (float) getHeight() * this.verticalSplitRatio) >= (float) this.dividerTouchSize
                * getResources().getDisplayMetrics().density) {
            return null;
        }
        return false;
    }

    private void stopDragging(MotionEvent motionEvent) {
        if (this.isSplit == (float) getDragDistance() > (float) (this.dividerTouchSize * 2)
                * getResources().getDisplayMetrics().density) {
            closeSplit(true);
        } else {
            openSplit(true);
        }
    }

    private void startDragging(MotionEvent motionEvent, boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        this.isDragging = true;
        updateDragging(motionEvent);
        updateChildVisibilities();
    }

    private void updateDragging(MotionEvent motionEvent) {
        if (this.isHorizontal) {
            this.dragPosition = (int) motionEvent.getX();
        } else {
            this.dragPosition = (int) ((float) getHeight() - motionEvent.getY());
        }
        requestLayout();
    }

    public void toggle() {
        if (!isSplit) {
            openSplit(true);
        } else {
            closeSplit(true);
        }
    }

    /**
     * @author Lody
     */
    public static interface OnPopLayoutListener {
        void onPopLayout(boolean isSplit);
    }
}