package com.meterview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/12/1.
 */

public class MeterView extends View {

    /**
     * 圆盘最大值
     */
    private int maxValue = 500;
    /**
     * 圆盘旋转角度
     */
    private int sweepAngle;
    /**
     * 圆盘初始角度
     */
    private int startAngle;
    /**
     * 外圆环宽度
     */
    private int outSweepWidth;
    /**
     * 内圆环宽度
     */
    private int inSweepWidth;
    /**
     * 圆弧画笔
     */
    private Paint paint;
    /**
     * 中间数字画笔
     */
    private Paint numPaint;
    /**
     * 中间文字画笔
     */
    private Paint textPaint;
    /**
     * 进度条画笔
     */
    private Paint indicatorPaint;
    /**
     * 小圆点画笔
     */
    private Paint circlePaint;
    /**
     * 控件宽度
     */
    private int mWidth;
    /**
     * 控件高度
     */
    private int mHeight;
    /**
     * 半径
     */
    private int radius;
    /**
     * 画布绘制的区域
     */
    private RectF rectF;
    /**
     * 当前进度
     */
    private int currentValue;


    public MeterView(Context context) {
        this(context, null);
    }

    public MeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numPaint.setAntiAlias(true);
        numPaint.setColor(Color.WHITE);
        numPaint.setDither(true);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setDither(true);
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setDither(true);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MeterView);
        maxValue = array.getInt(R.styleable.MeterView_maxValue, 500);
        sweepAngle = array.getInt(R.styleable.MeterView_sweepAngle, 220);
        outSweepWidth = array.getInt(R.styleable.MeterView_outSweepWidth, dp2px(3));
        inSweepWidth = array.getInt(R.styleable.MeterView_outSweepWidth, dp2px(8));
        startAngle = array.getInt(R.styleable.MeterView_startAngle, 160);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            if (width < dp2px(200)) {
                mWidth = dp2px(200);
            } else {
                mWidth = width;
            }
        } else {
            mWidth = dp2px(300);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            if (height < dp2px(200)) {
                mHeight = dp2px(200);
            } else {
                mHeight = height;
            }
        } else {
            mHeight = dp2px(300);
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "currentValue", 0, currentValue);
        animator.setDuration(2000);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = getMeasuredWidth() / 2 - dp2px(20);
        rectF = new RectF(-radius, -radius, radius, radius);
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        drawRound(canvas);
        drawScale(canvas);
        drawNum(canvas, currentValue + "");
        drawCenterText(canvas);
        drawIndicator(canvas);
        canvas.restore();
    }

    private void drawIndicator(Canvas canvas) {
        indicatorPaint.setStrokeWidth(dp2px(3));
        int[] colors = new int[]{0xffffffff, 0x10ffffff, 0x99ffffff, 0xffffffff};
        indicatorPaint.setShader(new SweepGradient(0, 0, colors, null));
        float sweep;
        if (currentValue < maxValue) {
            sweep = ((float) currentValue / (float) maxValue) * sweepAngle;
        } else {
            sweep = sweepAngle;
        }
        int w = dp2px(10);
        RectF inRectF = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(inRectF, startAngle, sweep, false, indicatorPaint);
        float x = (float) ((float) ((radius + w)) * Math.cos(Math.toRadians(startAngle + sweep)));
        float y = (float) ((float) ((radius + w)) * Math.sin(Math.toRadians(startAngle + sweep)));
        circlePaint.setMaskFilter(new BlurMaskFilter(dp2px(4), BlurMaskFilter.Blur.SOLID));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.WHITE);
        canvas.drawCircle(x, y, dp2px(3), circlePaint);
    }

    String[] text = new String[]{"较差", "中等", "良好", "优秀", "极好"};

    private void drawCenterText(Canvas canvas) {
        textPaint.setTextSize(sp2px(20));
        Paint.FontMetrics fm = numPaint.getFontMetrics();
//        float h = (float) Math.ceil(fm.bottom - fm.top);
        String currentText = "信用" + text[currentValue / (maxValue / 5)];
        float w = textPaint.measureText(currentText);
        canvas.drawText(currentText, -w / 2, rectF.centerY() + fm.bottom + dp2px(10), textPaint);
    }

    private void drawNum(Canvas canvas, String text) {
        numPaint.setTextSize(sp2px(30));
        float w = numPaint.measureText(text);
        float h = rectF.centerY() - dp2px(10);
        canvas.drawText(text, -w / 2, h, numPaint);
    }

    private void drawRound(Canvas canvas) {
        canvas.save();
        /*外圆*/
        paint.setAlpha(0x40);
        paint.setStrokeWidth(inSweepWidth);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        /*内圆*/
        paint.setStrokeWidth(outSweepWidth);
        int w = dp2px(10);
        RectF inRectF = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(inRectF, startAngle, sweepAngle, false, paint);
        canvas.restore();
    }

    private void drawScale(Canvas canvas) {
        canvas.save();
        canvas.rotate(startAngle - 270);
        float angle = (float) sweepAngle / 30;
        for (int i = 0; i <= 30; i++) {
            if (i % 6 != 0) {
                paint.setStrokeWidth(dp2px(2));
                paint.setAlpha(0x70);
                canvas.drawLine(0, -radius - inSweepWidth / 2, 0, -radius + inSweepWidth / 2, paint);
            } else {
                paint.setAlpha(0xAA);
                paint.setStrokeWidth(dp2px(3));
                canvas.drawLine(0, -radius - inSweepWidth / 2, 0, -radius + inSweepWidth / 2, paint);
                drawText(canvas, i * maxValue / 30 + "", paint);
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {
                paint.setAlpha(0x90);
                drawText(canvas, text[(i - 3) / 6], paint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String text, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        float width = paint.measureText(text);
        canvas.drawText(text, -width / 2, -radius + dp2px(15), paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
