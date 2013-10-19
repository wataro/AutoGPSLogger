package taro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;


public class SpeedMeterView extends View {

    private Paint mPaint = new Paint();
    private Paint mSpeedPaint = new Paint();
    private Paint mSpeedPaints[] = {
            new Paint(), // red
            new Paint(), // orange
            new Paint(), // yellow
            new Paint(), // green
            new Paint(), // cyan
            new Paint(), // blue
            new Paint(), // purple
    };

    private int mMaxSpeed = 50;
    private int mSpeed = mMaxSpeed / 2;

    private double mRadius;

    private double mStartAngle;

    private double mDiffAngle;

    private double mScaleLength;

    private final int mSpan = 2;

    private double mCenterX;

    private double mCenterY;

    private double mEndAngle;


    public SpeedMeterView(Context context) {

        super(context);

        initializePaints();
    }


    private void initializePaints() {

        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setStrokeWidth(3);
        mSpeedPaint.setStrokeWidth(3);
        mSpeedPaint.setStyle(Style.STROKE);

        for(Paint paint : mSpeedPaints) {
            paint.set(mSpeedPaint);
        }
        mSpeedPaints[0].setARGB(128, 255,   0,   0);
        mSpeedPaints[1].setARGB(128, 255, 128,   0);
        mSpeedPaints[2].setARGB(128, 255, 255,   0);
        mSpeedPaints[3].setARGB(128, 128, 255,   0);
        mSpeedPaints[4].setARGB(128,   0, 255,   0);
        mSpeedPaints[5].setARGB(128,   0, 255, 255);
        mSpeedPaints[6].setARGB(128,   0,   0, 255);
    }


    public SpeedMeterView(Context context, AttributeSet attrs) {

        super(context, attrs);

        initializePaints();
    }


    public SpeedMeterView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        initializePaints();
        }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        prepareToDraw(canvas);

        drawSpeed(canvas);

        // 0km/h - 50km/h, span km/h ごと
        final int mod = 10 / mSpan;
        for (int i = 0; i <= mMaxSpeed; i += mSpan) {

            final int j = i / mSpan;
            final int jj = (j - 1 + mod) % mod;
            final double offset = mScaleLength * (1 - ((1 + jj) / (double)mod));
            final double angle = mStartAngle + j * mDiffAngle;
            final double sin = Math.sin(angle);
            final double cos = Math.cos(angle);
            final double shortLength = mRadius + offset;
            final double longLength = mRadius + mScaleLength;
            final float sx = (float)(mCenterX + shortLength * cos);
            final float sy = (float)(mCenterY + shortLength * sin);
            final float ex = (float)(mCenterX + longLength * cos);
            final float ey = (float)(mCenterY + longLength * sin);

            canvas.drawLine(sx, sy, ex, ey, mPaint);
        }
    }

    private void drawSpeed(Canvas canvas) {

        final double rate = (double)mSpeed / mMaxSpeed;
        final int r = (int) (rate * 255);
        mSpeedPaint.setARGB(128, r, 0, 0);
        mSpeedPaint.setStyle(Style.STROKE);

        float startAngleDegree = (float) (mStartAngle * 180 / Math.PI);
        float sweepAngle = (float) ((mEndAngle - mStartAngle) * mSpeed / mMaxSpeed);
        float sweepAngleDegree = (float) (sweepAngle * 180 / Math.PI);

        for (int i = 0; i < mSpeedPaints.length; ++i) {
            drawSpeedArc(canvas, startAngleDegree, sweepAngleDegree, -i * 3 + 10, mSpeedPaints[i]);
        }
    }


    private void drawSpeedArc(Canvas canvas,
            float startAngleDegree,
            float sweepAngleDegree,
            float radiusOffset,
            Paint paint) {

        final double speedRadius = mRadius + mScaleLength / 2 + radiusOffset;
        final float left = (float) (mCenterX - speedRadius);
        final float top =  (float) (mCenterY - speedRadius);
        final float rectWidth = (float) (speedRadius * 2);
        final float right = left + rectWidth;
        final float bottom = top + rectWidth;
        RectF oval = new RectF(left, top, right, bottom);
        canvas.drawArc(oval, startAngleDegree, sweepAngleDegree, false, paint);
    }


    private void prepareToDraw(Canvas canvas) {

        final double w = canvas.getWidth();
        final double h = canvas.getHeight();
        mCenterX = w / 2.0;
        mCenterY = h * 2.0;
        mScaleLength = h / 10.0;

        mRadius = mCenterY - getPaddingTop() - mScaleLength;
        final double baseAngle = Math.asin( (mCenterX - getPaddingLeft() - mScaleLength) / mRadius );
        final double pi2 = Math.PI / 2;
        mStartAngle = -baseAngle - pi2;
        mEndAngle = baseAngle - pi2;
        mDiffAngle = (mEndAngle - mStartAngle) / (mMaxSpeed / mSpan + 1);
    }


    
    public void setSpeed(int speed) {
    
        this.mSpeed = Math.min(mMaxSpeed, speed);
        invalidate();
    }

}
