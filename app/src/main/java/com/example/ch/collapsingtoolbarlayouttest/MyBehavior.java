package com.example.ch.collapsingtoolbarlayouttest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;


/**
 * Created by ch on 2017/8/7.
 */

public class MyBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "MyBehavior";
    private View mTargetView;
    private int mParentHeight;
    private int mTargetViewHeight;
    private static final float TARGET_HEIGHT = 500;
    private float mTotalDy;
    private float mLastScale;
    private int mLastBottom;


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        Log.e("ch","onLayoutChild");
        boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
        if (mTargetView == null) {
            mTargetView = parent.findViewWithTag(TAG);
            if (mTargetView != null) {
                initial(abl);
            }
        }
        return handled;
    }

    private void initial(AppBarLayout abl) {
        abl.setClipChildren(false);
        mParentHeight = abl.getHeight();
        mTargetViewHeight = mTargetView.getHeight();
        ViewCompat.setTranslationY(mTargetView,mTargetViewHeight/6);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        Log.e("ch","onNestedPreScroll");
        if (mTargetView != null && ((dy < 0 && child.getBottom() >= mParentHeight) || (dy > 0 && child.getBottom() > mParentHeight))) {
            Log.e("ch1","scale");
            scale(child, target, dy);
        } else {
            Log.e("ch1","noscale"+"dy___"+dy+"bottom"+child.getBottom());
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }

    private void scale(AppBarLayout abl, View target, int dy) {
        mTotalDy += -dy;
        if ((mTotalDy+mParentHeight)>mTargetViewHeight){
            mTotalDy = Math.min(mTotalDy, TARGET_HEIGHT);
            mLastScale = Math.max(1f, 1f + (mTotalDy+mParentHeight-mTargetViewHeight) / TARGET_HEIGHT);
            ViewCompat.setScaleX(mTargetView, mLastScale);
            ViewCompat.setScaleY(mTargetView, mLastScale);
            ViewCompat.setTranslationY(mTargetView,(mTargetViewHeight/3));
            mLastBottom = (int) (mParentHeight + mTotalDy);
            abl.setBottom(mLastBottom);
        }else {
            ViewCompat.setScaleX(mTargetView, 1f);
            ViewCompat.setScaleY(mTargetView, 1f);
            ViewCompat.setTranslationY(mTargetView,(mTotalDy/2+mTargetViewHeight/6));
            mLastBottom = (int) (mParentHeight + mTotalDy);
            abl.setBottom(mLastBottom);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        Log.e("ch","onStopNestedScroll");
        recovery(abl);
        super.onStopNestedScroll(coordinatorLayout, abl, target);
    }

    private boolean isAnimate;

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.e("ch","onStartNestedScroll");
        isAnimate = true;
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        Log.e("ch","onNestedPreFling");
        if (velocityY > 100) {
            isAnimate = false;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    private void recovery(final AppBarLayout abl) {
        if (mTotalDy > 0) {
            mTotalDy = 0;
            if (isAnimate) {
                Log.e("Cj","isAnimate _t");
              /*  ObjectAnimator  translationY= ObjectAnimator.ofFloat(mTargetView, "translationY", ViewCompat.getTranslationY(mTargetView), mTargetViewHeight/6);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mTargetView, "scaleX", mLastScale, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTargetView, "scaleY", mLastScale, 1f);
                AnimatorSet animSet = new AnimatorSet();
                animSet.play(scaleX).with(scaleY).after(translationY);
                animSet.setDuration(200);
                animSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setTranslationY(mTargetView,mTargetViewHeight/6);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animSet.start();*/

                ValueAnimator anim = ValueAnimator.ofFloat(mLastScale, 1f).setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        ViewCompat.setScaleX(mTargetView, value);
                        ViewCompat.setScaleY(mTargetView, value);
                        abl.setBottom((int) (mLastBottom - (mLastBottom - mParentHeight) * animation.getAnimatedFraction()));
                    }
                });
                ValueAnimator anim1 = ValueAnimator.ofFloat(ViewCompat.getTranslationY(mTargetView),mTargetViewHeight/6).setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        ViewCompat.setTranslationY(mTargetView, value);
                    }
                });
                anim1.start();
            } else {
                Log.e("Cj","isAnimate _f");
                ViewCompat.setScaleX(mTargetView, 1f);
                ViewCompat.setScaleY(mTargetView, 1f);
                ViewCompat.setTranslationY(mTargetView,mTargetViewHeight/6);
                abl.setBottom(mParentHeight);
            }
        }
    }
}
