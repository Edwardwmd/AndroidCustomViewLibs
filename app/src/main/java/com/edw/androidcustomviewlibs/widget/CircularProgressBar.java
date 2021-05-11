package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.edw.androidcustomviewlibs.R;
import com.edw.androidcustomviewlibs.utils.UnitUtils;


/**
 * **************************************************************************************************
 * Project Name:    CustomViewBasics
 * <p>
 * Date:            2021-05-11
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.gitee.io/
 * <p>
 * Description：    环形进度条
 * <p>
 * **************************************************************************************************
 */
public class CircularProgressBar extends View {
    private static final String TAG = "CircularProgressBar";
    private float rate = 0;
    private String unit = "";
    private int mTextSize = UnitUtils.sp2px(getRootView().getContext(), 15);
    private int textColor;
    private int fullColor;
    private int currentColor;
    private int barWidth;
    private Paint mTextPaint;
    private Paint mInnerPaint;
    private Paint mOuterPaint;
    private Context mContext;
    private int currentProgress = 20;
    private int maxProgress;

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        rate = UnitUtils.getScreenWidth(context) / 1080f;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setDither(true);
        mInnerPaint.setDither(true);
        mOuterPaint.setDither(true);
        mTextPaint.setFakeBoldText(true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar);
        if (ta == null) {
            return;
        }
        try {
            unit = ta.getString(R.styleable.CircularProgressBar_cp_unit);
            mTextSize = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_cp_textSize, UnitUtils.sp2px(context, 15));
            textColor = ta.getColor(R.styleable.CircularProgressBar_cp_textColor, Color.BLACK);
            fullColor = ta.getColor(R.styleable.CircularProgressBar_cp_fullColor, Color.parseColor("#2a5caa"));
            currentColor = ta.getColor(R.styleable.CircularProgressBar_cp_currentColor, Color.parseColor("#ef596f"));
            barWidth = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_cp_barWidth, UnitUtils.dp2px(context, 20));
            maxProgress = ta.getInt(R.styleable.CircularProgressBar_cp_barWidth, 100);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }

    }

    private void initPaint() {
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(mTextSize);

        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setColor(fullColor);
        mInnerPaint.setStrokeWidth(barWidth);

        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setColor(currentColor);
        mOuterPaint.setStrokeWidth(barWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getSize(heightMeasureSpec);

        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        if (wMode == MeasureSpec.AT_MOST) {
            wSize = (int) (UnitUtils.getScreenWidth(mContext) - UnitUtils.dp2px(mContext, 30));
        }


        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        if (hMode == MeasureSpec.AT_MOST) {
            hSize = (int) (UnitUtils.getScreenWidth(mContext) - UnitUtils.dp2px(mContext, 30));
        }

        int size = Math.min(wSize, hSize);
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        String text;
        canvas.save();
        //定义正方形画布的一半
        int centerXY = getWidth() / 2;
        //圆环环宽度的一半
        float radiusOffset = barWidth * rate / 2;
        //圆环在定义的长方形中真实的半径
        float radius = centerXY - radiusOffset;
        @SuppressLint("DrawAllocation") RectF rectF = new RectF(radiusOffset, radiusOffset, centerXY + radius, centerXY + radius);

        canvas.drawArc(rectF, 90, 360f, false, mInnerPaint);
        canvas.restore();
        //当前扫过的角度
        float currentAngle = (float) currentProgress / (float) maxProgress * 360f;
        canvas.drawArc(rectF, 90, currentAngle, false, mOuterPaint);
        if (!TextUtils.isEmpty(unit)) {
            text = currentProgress + unit;
        } else {
            text = String.valueOf(currentProgress);
        }
        float x = (getWidth() >> 1) - mTextPaint.measureText(text) / 2;
        Paint.FontMetricsInt fm = mTextPaint.getFontMetricsInt();
        float textOffset = ((fm.bottom - fm.top) >> 1) - fm.bottom;
        float baseline = (getWidth() >> 1) + textOffset;
        canvas.drawText(text, x, baseline, mTextPaint);


    }


    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setFullColor(int fullColor) {
        this.fullColor = fullColor;
        invalidate();
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
        invalidate();
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
        invalidate();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
}
