package com.androidcycle.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class EatBeans extends View {
    private AlphaAnimation alphaAnimation;
    private Transformation transformation;
    private AlphaAnimation beansAlphaAnimation;
    private Transformation beansTransformation;

    public EatBeans(Context context) {
        super(context);
    }

    public EatBeans(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EatBeans(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EatBeans(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    final Paint paint = new Paint();
    final RectF oval = new RectF();


    public void startAnimManual() {
        if (alphaAnimation == null) {
            alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        } else {
            alphaAnimation.reset();
        }
        if (beansAlphaAnimation == null) {
            beansAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        } else {
            beansAlphaAnimation.reset();
        }

        if (transformation == null) {
            transformation = new Transformation();
        } else {
            transformation.clear();
        }

        if (beansTransformation == null) {
            beansTransformation = new Transformation();
        } else {
            beansTransformation.clear();
        }

        this.alphaAnimation.setRepeatMode(Animation.REVERSE);
        this.alphaAnimation.setRepeatCount(Animation.INFINITE);
        this.alphaAnimation.setDuration(1000);
        this.alphaAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);

        this.beansAlphaAnimation.setRepeatMode(Animation.RESTART);
        this.beansAlphaAnimation.setRepeatCount(Animation.INFINITE);
        this.beansAlphaAnimation.setDuration(1000);
        this.beansAlphaAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
        postInvalidate();
    }

    public void stopAnimManual() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        final long time = getDrawingTime();
        alphaAnimation.getTransformation(time, transformation);
        final float alpha = transformation.getAlpha();
        System.out.println("alpha = " + alpha);
        beansAlphaAnimation.getTransformation(time, beansTransformation);
        final float beansAlpha = beansTransformation.getAlpha();
        postInvalidateOnAnimation();

        paint.setARGB(255, 200, 200, 200);
        oval.set(100, 100, 400, 400);

        for (int i = 1; i < 5; i++) {
            canvas.drawCircle(oval.centerX() + i * 100 - beansAlpha * 100, oval.centerY(), 20, paint);
        }

        paint.setARGB(255, 0, 200, 255);

        canvas.drawArc(oval, 45 * alpha / 2, 360 - 45 * alpha, true, paint);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimManual();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimManual();
    }
}
