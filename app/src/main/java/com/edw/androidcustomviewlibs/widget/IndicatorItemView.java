package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.edw.androidcustomviewlibs.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **************************************************************************************************
 * Project Name:    CustomViewBasics
 * <p>
 * Date:            2021-05-10
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.gitee.io/
 * <p>
 * Description：    实现类似TabLayout
 * 不需要自己重写onMeasure方法，AppCompatTextView中已经帮忙测量好了
 * <p>
 * **************************************************************************************************
 */
@SuppressLint("AppCompatCustomView")
public class IndicatorItemView extends TextView {
    private static final String TAG = "IndicatorItemView";
    //绘制文字变色前的画笔
    private Paint mOriginPaint;
    //绘制文字变色后的画笔
    private Paint mChangePaint;
    //改变前的颜色
    private int originColors;
    //改变后的颜色
    private int changeColors;

    private float changeRate = 0f;


    public static final int SHIFT_LEFT = 0x10;
    public static final int SHIFT_RIGHT = 0x11;
    private @ScrollDirection
    int mDirection = SHIFT_LEFT;

    public IndicatorItemView(Context context) {
        this(context, null);
    }

    public IndicatorItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //创建抗锯齿画笔
        mOriginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //防抖动
        mOriginPaint.setDither(true);
        mChangePaint.setDither(true);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorItemView);
        if (ta == null) return;
        try {
            originColors = ta.getColor(R.styleable.IndicatorItemView_il_originColor, Color.parseColor("#5e7c85"));
            changeColors = ta.getColor(R.styleable.IndicatorItemView_il_changeColor, Color.parseColor("#f47920"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }
    }

    private void initPaint() {
        mOriginPaint.setTextSize(getTextSize());
        mOriginPaint.setColor(originColors);

        mChangePaint.setTextSize(getTextSize());
        mChangePaint.setColor(changeColors);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas); 不用AppCompatTextView的onDraw，用自己自定义的
        //实现思路：使用clipRect实现文字分割，改变字体颜色
        //左边字体用一个画笔去绘制文本，另外一个颜色使用另外一支画笔去改变

        //初始化画笔属性
        initPaint();

        //获取文字偏移量（距离）
        int middleOffset = (int) (changeRate * getWidth());

        //画原来未改变颜色的文本
        if (mDirection == SHIFT_LEFT) {
            //黑色到红色 middleOffset-getWidth()表示这二者间的距离是不被截取的
            drawOriginOrChangeText(canvas, mChangePaint, 0, middleOffset);
            drawOriginOrChangeText(canvas, mOriginPaint, middleOffset, getWidth());
        } else {
           //红色到黑色
            drawOriginOrChangeText(canvas, mChangePaint, getWidth() - middleOffset, getWidth());
            drawOriginOrChangeText(canvas, mOriginPaint, 0, getWidth()-middleOffset);
        }

    }

    private void drawOriginOrChangeText(Canvas canvas, Paint mPaint, int startX, int endY) {
        //保存画布
        canvas.save();
        String indicatorText = getText().toString();
        //根据位置截取文本
        canvas.clipRect(startX, 0, endY, getHeight());
        float originX = (getWidth() >> 1) - mPaint.measureText(indicatorText) / 2;
        Paint.FontMetricsInt fm1 = mPaint.getFontMetricsInt();
        float originOffset = ((fm1.bottom - fm1.top) >> 1) - fm1.bottom;
        float originBaseLineY = (getHeight() >> 1) + originOffset;
        canvas.drawText(indicatorText, originX, originBaseLineY, mPaint);
        //释放画布
        canvas.restore();
    }

    public void setOriginColors(int originColors) {
        this.originColors = originColors;
        invalidate();
    }

    public void setChangeColors(int changeColors) {
        this.changeColors = changeColors;
        invalidate();
    }

    public void setChangeRate(float changeRate) {
        if (changeRate < 0 || changeRate > 1.0f) {
            throw new RuntimeException("您的变化率不正确，变化率的范围因该在0~1.0之间，请检查您的数据计算是否存在问题~~");
        }
        this.changeRate = changeRate;
        invalidate();
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
        invalidate();
    }


    @IntDef({SHIFT_LEFT, SHIFT_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface ScrollDirection {
    }


}
