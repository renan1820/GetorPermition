package com.gitproject.getorpermition.ui.scan;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;

public class RadarView extends View {

    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float sweepAngle = 0f;
    private ValueAnimator animator;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.parseColor("#00E5A0"));
        circlePaint.setStrokeWidth(2f);
        circlePaint.setAlpha(60);

        sweepPaint.setStyle(Paint.Style.STROKE);
        sweepPaint.setColor(Color.parseColor("#00E5A0"));
        sweepPaint.setStrokeWidth(4f);
        sweepPaint.setAlpha(200);
    }

    public void startAnimation() {
        if (animator != null && animator.isRunning()) return;
        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            sweepAngle = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        sweepAngle = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float maxRadius = Math.min(cx, cy) - 8f;

        // Draw concentric rings
        for (int i = 1; i <= 4; i++) {
            canvas.drawCircle(cx, cy, maxRadius * i / 4f, circlePaint);
        }

        // Draw sweep arc (60° wide, starting at sweepAngle)
        RectF oval = new RectF(cx - maxRadius, cy - maxRadius, cx + maxRadius, cy + maxRadius);
        canvas.drawArc(oval, sweepAngle - 90, 60, false, sweepPaint);
    }
}
