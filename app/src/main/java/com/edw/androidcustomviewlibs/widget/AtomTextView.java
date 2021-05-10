package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
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
 * Date:            2021-05-05
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.gitee.io/
 * <p>
 * Description：    自定义TextView
 * <p>
 * **************************************************************************************************
 */
public class AtomTextView extends View {
    private static final String TAG = "AtomTextView";
    private String mText = "";
    private int mTextSize = UnitUtils.sp2px(getRootView().getContext(), 15);
    private int mTextColor = Color.BLACK;
    private Paint mPaint;

    public AtomTextView(Context context) {
        this(context, null);
    }

    public AtomTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint({"Recycle"})
    public AtomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AtomTextView);
        mText = ta.getString(R.styleable.AtomTextView_mText);
        mTextColor = ta.getColor(R.styleable.AtomTextView_mTextColor, Color.BLACK);
        mTextSize = ta.getDimensionPixelSize(R.styleable.AtomTextView_mTextSize, UnitUtils.sp2px(getRootView().getContext(), 15));
        //回收资源
        ta.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //初始化画笔的参数
        initPaint();
    }

    private void initPaint() {
        //设置画笔颜色
        mPaint.setColor(mTextColor);
        //设置画笔粗细
        mPaint.setTextSize(mTextSize);
        //设置画笔抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG,"执行onMeasure----》");
        //获取宽高测量模式
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        //1.MeasureSpec.EXACTLY：xml文件中给的是确定值（如20dp、match_parent）
        int width = MeasureSpec.getSize(widthMeasureSpec);

        //2、MeasureSpec.AT_MOST：xml给的是不确定的值（warp_content），需要计算
        if (wMode == MeasureSpec.AT_MOST) {
            //创建矩形（文本被包含在矩形中）
            @SuppressLint("DrawAllocation") Rect bounds = new Rect();
            //测量文本
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
            //算上左右内边距
            width = bounds.width() + getPaddingLeft() + getPaddingRight();
        }

        //1.xml文件中给的是确定值（如20dp、match_parent）
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //2、xml给的是不确定的值（warp_content），需要计算
        if (hMode == MeasureSpec.AT_MOST) {
            @SuppressLint("DrawAllocation") Rect bounds = new Rect();
            //测量文本
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
            //算上上下内边距
            height = bounds.height() + getPaddingBottom() + getPaddingTop();
        }
        //设置控件宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG,"执行onLayout----》");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,"执行onDraw----》");
        initPaint();
        //绘制文本
        //x:文本原点位置x坐标，y:文本的基线的y坐标

        FontMetricsInt fm = mPaint.getFontMetricsInt();
        //1、绘制X轴原点坐标，如果绘制横向居中的话，它的位置X=getWidth/2-绘测字体的长度的一半
        float x = (getWidth() >> 1) - mPaint.measureText(mText) / 2;
        //2、绘测y坐标基线，baseLine=getHeight() / 2+中线到基线的距离,即baseLine=getHeight() / 2+dy
        //中线到基线的距离=文字高度/2-基线到文字底部的距离，即dy=(bottom-top)/2-bottom
        Log.e(TAG, "bottom=  " + fm.bottom + "    descent= " + fm.descent);
        int baseLineY = getHeight() / 2 + ((fm.bottom - fm.top) / 2 - fm.bottom);
        //3、绘制文本
        canvas.drawText(mText, x, baseLineY, mPaint);
    }

    public void setmText(String mText) {
        this.mText = mText;
        //跟invalidate()相反，他只调用measure()和layout()过程，不会调用draw()。
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            if (!isInLayout()) {
//                requestLayout();
//            }
//        }
        //重绘制，调用此方法，会再次调用onDraw方法对布局更新，不会调用measure()和layout()这两个过程
        // 此方法用于主线程
        invalidate();

        //子线程调用此方法，非UI线程使用，在源码中使用Handle发送消息，使用MSG_INVALIDATE标志该消息，最终
        //也是在子线程中调用invalidate()方法，本质上是与invalidate()一致
        //postInvalidate();

    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        invalidate();
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

}
