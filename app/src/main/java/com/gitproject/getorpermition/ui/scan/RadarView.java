package com.gitproject.getorpermition.ui.scan;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gitproject.getorpermition.R;

public class RadarView extends View {

    // Brand cyan — #22D3EE — matches design system brand_500
    private static final int BRAND_CYAN = 0xFF22D3EE;
    private static final int BRAND_GLOW = 0x3322D3EE; // ~20% alpha

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float sweepAngle = 0f;
    private ValueAnimator animator;

    public RadarView(Context context) {
        super(context);
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Background rings
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(BRAND_CYAN);
        ringPaint.setStrokeWidth(1.5f);
        ringPaint.setAlpha(40); // subtle, design-system border_subtle feel

        // Sweep arc
        sweepPaint.setStyle(Paint.Style.STROKE);
        sweepPaint.setColor(BRAND_CYAN);
        sweepPaint.setStrokeWidth(3f);
        sweepPaint.setAlpha(220);

        // Center dot
        centerDotPaint.setStyle(Paint.Style.FILL);
        centerDotPaint.setColor(BRAND_CYAN);
    }

    public void startAnimation() {
        if (animator != null && animator.isRunning()) return;
        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(2400);
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
        float maxR = Math.min(cx, cy) - 6f;

        // Draw concentric rings at 25%, 50%, 75%, 100%
        for (int i = 1; i <= 4; i++) {
            canvas.drawCircle(cx, cy, maxR * i / 4f, ringPaint);
        }

        // Cross-hair lines (subtle)
        ringPaint.setAlpha(20);
        canvas.drawLine(cx, cy - maxR, cx, cy + maxR, ringPaint);
        canvas.drawLine(cx - maxR, cy, cx + maxR, cy, ringPaint);
        ringPaint.setAlpha(40);

        // Sweep arc (60° wide) — only when animating
        if (animator != null) {
            RectF oval = new RectF(cx - maxR, cy - maxR, cx + maxR, cy + maxR);
            canvas.drawArc(oval, sweepAngle - 90f, 60f, false, sweepPaint);
        }

        // Center dot
        canvas.drawCircle(cx, cy, 5f, centerDotPaint);
    }
}
