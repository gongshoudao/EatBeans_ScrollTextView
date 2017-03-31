package com.androidcycle.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;

public class ScrollTextView extends View {
    private int topTvColor = Color.BLACK;
    private int bottomTvColor = Color.LTGRAY;
    private float topTvSize = 0;

    private TextPaint topTextPaint;
    private float topTextHeight;
    private String topStr;
    private String bottomStr;
    private String nextTopStr;
    private String nextBottomStr;
    private float bottomTvSize;
    private TextPaint bottomTextPaint;
    private boolean hasEllipsizeTopStr;
    private AlphaAnimation alphaAnimation;
    private Transformation transformation;
    private final ArrayList<String[]> scrollTexts = new ArrayList<>();
    private boolean animStarted;
    private float descentTop;
    private float descentBottom;
    private int index = 0;
    private int size;
    private boolean autoPlay;
    private int paddingLeft = -1;
    private int paddingTop = -1;
    private int paddingRight = -1;
    private int paddingBottom = -1;
    private int contentWidth = -1;
    private int contentHeight = -1;

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
        if (topStr == null) {
            topStr = "";
        }
        topTvColor = a.getColor(
                R.styleable.ScrollTextView_topTextColor,
                topTvColor);
        topTvSize = a.getDimension(
                R.styleable.ScrollTextView_topTextSize,
                topTvSize);

        bottomStr = a.getString(
                R.styleable.ScrollTextView_bottomString);
        if (bottomStr == null) {
            bottomStr = "";
        }
        bottomTvColor = a.getColor(
                R.styleable.ScrollTextView_bottomTextColor,
                bottomTvColor);
        bottomTvSize = a.getDimension(
                R.styleable.ScrollTextView_bottomTextSize,
                bottomTvSize);
        autoPlay = a.getBoolean(R.styleable.ScrollTextView_autoPlay, false);
        a.recycle();

        // Set up a default TextPaint object
        topTextPaint = new TextPaint();
        topTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        topTextPaint.setTextAlign(Paint.Align.LEFT);

        bottomTextPaint = new TextPaint();
        bottomTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bottomTextPaint.setTextAlign(Paint.Align.LEFT);
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
    }

    public void setScrollText(ArrayList<String[]> strs) {
        scrollTexts.addAll(strs);
        size = scrollTexts.size();
        index = 0;
        final String[] first = scrollTexts.get(index);
        nextTopStr = first[0];
        nextBottomStr = first[1];
        topStr = first[0];
        bottomStr = first[1];
        invalidateTextPaintAndMeasurements();
    }

    private void getNextString() {
        if (size == 0) {
            return;
        }
        if (size > index + 1) {
            index++;
        } else {
            index = 0;
        }
        final String[] second = scrollTexts.get(index);
        topStr = second[0];
        bottomStr = second[1];
    }

    private void startAnimInternal() {
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

    private void stopAnimInternal() {
        animStarted = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paddingLeft == -1) {
            paddingLeft = getPaddingLeft();
        }
        if (paddingTop == -1) {
            paddingTop = getPaddingTop();
        }
        if (paddingRight == -1) {
            paddingRight = getPaddingRight();
        }
        if (paddingBottom == -1) {
            paddingBottom = getPaddingBottom();
        }

        if (contentWidth == -1) {
            contentWidth = getWidth() - paddingLeft - paddingRight;
        }
        if (contentHeight == -1) {
            contentHeight = getHeight() - paddingTop - paddingBottom;
        }

        if (!hasEllipsizeTopStr) {
            nextTopStr = TextUtils.ellipsize(nextTopStr, topTextPaint, contentWidth, TextUtils.TruncateAt.END).toString();
            hasEllipsizeTopStr = true;
        }

        float alpha = 1;
        if (animStarted) {
            final long drawingTime = getDrawingTime();
            alphaAnimation.getTransformation(drawingTime, transformation);
            alpha = transformation.getAlpha();
            postInvalidateOnAnimation();
        }

        //进入
        canvas.drawText(topStr,
                paddingLeft,
                paddingTop + topTextHeight + (contentHeight + descentTop) * alpha - descentTop,
                topTextPaint);
        canvas.drawText(bottomStr,
                paddingLeft,
                paddingBottom + contentHeight + contentHeight * alpha - descentBottom,
                bottomTextPaint);

        //滑出
        if (nextTopStr != null && nextBottomStr != null) {
            canvas.drawText(nextTopStr,
                    paddingLeft,
                    (paddingTop + topTextHeight) * alpha - descentTop,
                    topTextPaint);
            canvas.drawText(nextBottomStr,
                    paddingLeft,
                    (contentHeight + paddingBottom) * alpha - descentBottom,
                    bottomTextPaint);
        }
    }

    private final Runnable action = new Runnable() {
        @Override
        public void run() {
            nextTopStr = topStr;
            nextBottomStr = bottomStr;
            getNextString();
            if (contentWidth != -1) {
                topStr = TextUtils.ellipsize(topStr, topTextPaint, contentWidth, TextUtils.TruncateAt.END).toString();
                nextTopStr = TextUtils.ellipsize(nextTopStr, topTextPaint, contentWidth, TextUtils.TruncateAt.END).toString();
            }
            startAnimInternal();
            postDelayed(action, 2500);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (autoPlay) {
            postDelayed(action, 2500);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hasEllipsizeTopStr = false;
        stopAnimInternal();
        removeCallbacks(action);
    }

    public void startAnim() {
        hasEllipsizeTopStr = false;
        removeCallbacks(action);
        postDelayed(action, 2500);
    }

    public void stopAnim() {
        stopAnimInternal();
        removeCallbacks(action);
    }

}
