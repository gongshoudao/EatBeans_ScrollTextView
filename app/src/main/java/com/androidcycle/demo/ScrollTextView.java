package com.androidcycle.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;

public class ScrollTextView extends View {
    private String topStr;
    private int topTvColor = Color.RED;
    private float topTvSize = 0;
    private Drawable mExampleDrawable;

    private TextPaint topTextPaint;
    private float topTextHeight;
    private String bottomStr;
    private int bottomTvColor;
    private float bottomTvSize;
    private TextPaint bottomTextPaint;
    private boolean hasEllipsizeTopStr;
    private boolean hasEllipsizeBottomStr;
    private AlphaAnimation alphaAnimation;
    private Transformation transformation;
    private float bottomTextHeight;
    private final ArrayList<String[]> scrollTexts = new ArrayList<>();
    private boolean animStarted;
    private float descentTop;
    private float descentBottom;

    public ScrollTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ScrollTextView, defStyle, 0);

        topStr = a.getString(
                R.styleable.ScrollTextView_topString);
        topTvColor = a.getColor(
                R.styleable.ScrollTextView_topTextColor,
                topTvColor);
        topTvSize = a.getDimension(
                R.styleable.ScrollTextView_topTextSize,
                topTvSize);

        bottomStr = a.getString(
                R.styleable.ScrollTextView_bottomString);
        bottomTvColor = a.getColor(
                R.styleable.ScrollTextView_bottomTextColor,
                topTvColor);
        bottomTvSize = a.getDimension(
                R.styleable.ScrollTextView_bottomTextSize,
                topTvSize);

        a.recycle();

        // Set up a default TextPaint object
        topTextPaint = new TextPaint();
        topTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        topTextPaint.setTextAlign(Paint.Align.LEFT);

        bottomTextPaint = new TextPaint();
        bottomTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bottomTextPaint.setTextAlign(Paint.Align.LEFT);

        final ArrayList<String[]> strs = new ArrayList<>();
        final String[] strings = new String[2];
        for (int i = 0; i < 6; i++) {
            strings[0] = i + "-巴拉巴拉巴拉";
            strings[1] = i + "-哈哈哈哈";
            strs.add(strings);
        }
        setScrollText(strs);

        invalidateTextPaintAndMeasurements();
    }


    private void invalidateTextPaintAndMeasurements() {
        topTextPaint.setTextSize(topTvSize);
        topTextPaint.setColor(topTvColor);
        Paint.FontMetrics fontMetrics = topTextPaint.getFontMetrics();
        descentTop = fontMetrics.descent;
        topTextHeight = fontMetrics.bottom + topTextPaint.getFontSpacing() / 2 + descentTop;

        bottomTextPaint.setTextSize(bottomTvSize);
        bottomTextPaint.setColor(bottomTvColor);
        final Paint.FontMetrics bottomFontMetrics = bottomTextPaint.getFontMetrics();
        descentBottom = bottomFontMetrics.descent;
        System.out.println("fontMetrics.descent descentBottom = " + descentBottom);
        bottomTextHeight = bottomFontMetrics.bottom + bottomTextPaint.getFontSpacing() / 2;
    }

    public void setScrollText(ArrayList<String[]> strs) {
        scrollTexts.addAll(strs);
    }

    public void startAnim() {
        animStarted = true;
        if (alphaAnimation == null) {
            alphaAnimation = new AlphaAnimation(1.0f, 0f);
        } else {
            alphaAnimation.reset();
        }
        if (transformation == null) {
            transformation = new Transformation();
        } else {
            transformation.clear();
        }
        alphaAnimation.setDuration(1500);
        alphaAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
        postInvalidate();
    }

    public void stopAnim() {
        animStarted = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    final Rect bounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if (!hasEllipsizeTopStr) {
            topStr = TextUtils.ellipsize(topStr, topTextPaint, contentWidth, TextUtils.TruncateAt.END).toString();
            hasEllipsizeTopStr = true;
        }

        if (!hasEllipsizeBottomStr) {
            bottomStr = TextUtils.ellipsize(bottomStr, bottomTextPaint, contentWidth, TextUtils.TruncateAt.END).toString();
            hasEllipsizeBottomStr = true;
        }

        float alpha = 1;
        if (animStarted) {
            final long drawingTime = getDrawingTime();
            alphaAnimation.getTransformation(drawingTime, transformation);
            alpha = transformation.getAlpha();
            if (alpha == 0) {
                animStarted = false;
            } else {
                postInvalidateOnAnimation();
            }
        }

        // Draw the top text.
        canvas.drawText(topStr,
                paddingLeft,
                paddingTop + topTextHeight + contentHeight * alpha,
                topTextPaint);

        // Draw the bottom text.
        canvas.drawText(bottomStr,
                paddingLeft,
                getHeight() - bottomTextHeight + contentHeight * alpha,
                bottomTextPaint);

        /*canvas.drawText(topStr,
                paddingLeft,
                (paddingTop + topTextHeight) * alpha - descentTop,
                topTextPaint);

        canvas.drawText(bottomStr,
                paddingLeft,
                (bottomTextHeight + contentHeight) * alpha - descentBottom,
                bottomTextPaint);*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnim();
            }
        }, 2000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return topStr;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        topStr = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return topTvColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        topTvColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return topTvSize;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        topTvSize = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
