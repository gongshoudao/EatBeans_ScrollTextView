package com.androidcycle.demo;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

@Deprecated
public class EatBeans_Old extends View {
    private ValueAnimator angleAnimator;
    private int animatedValue;
    private ValueAnimator pathAnimator;
    private float pathValue;

    public EatBeans_Old(Context context) {
        super(context);
    }

    public EatBeans_Old(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EatBeans_Old(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EatBeans_Old(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void startAnim() {
        if (angleAnimator == null) {
            angleAnimator = new ValueAnimator();
            angleAnimator.setIntValues(0, 90,0);
            angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animatedValue = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            angleAnimator.setRepeatMode(ValueAnimator.RESTART);
            angleAnimator.setRepeatCount(ValueAnimator.INFINITE);
            angleAnimator.setDuration(1500);

        }
        if (pathAnimator == null) {
            pathAnimator = new ValueAnimator();
            pathAnimator.setFloatValues(0f,1.0f);
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pathValue = (float) animation.getAnimatedValue();
                }
            });
            pathAnimator.setRepeatMode(ValueAnimator.RESTART);
            pathAnimator.setRepeatCount(ValueAnimator.INFINITE);
            pathAnimator.setDuration(1500);
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(angleAnimator,pathAnimator);
        animatorSet.start();
    }

    public void stopAnim() {
        angleAnimator.cancel();
    }

    final Paint paint = new Paint();
    final RectF oval = new RectF();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setARGB(255, 200, 200, 200);
        oval.set(100, 100, 400, 400);

        for (int i = 1; i < 5; i++) {
            canvas.drawCircle(oval.centerX()+i*100-pathValue*100,oval.centerY(),20,paint);
        }

        paint.setARGB(255, 0, 200, 255);

        canvas.drawArc(oval, animatedValue/2, 360-animatedValue, true, paint);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
