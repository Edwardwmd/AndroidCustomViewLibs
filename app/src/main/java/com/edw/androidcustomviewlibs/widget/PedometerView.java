package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;


import com.edw.androidcustomviewlibs.R;
import com.edw.androidcustomviewlibs.utils.UnitUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **************************************************************************************************
 * Project Name:    CustomViewBasics
 * <p>
 * Date:            2021-05-07
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.gitee.io/
 * <p>
 * Description：    缺口圆环进度
 * <p>
 * **************************************************************************************************
 */
public class PedometerView extends View {
    private static final String TAG = "PedometerView";
    private int innerColor = Color.WHITE;
    private int outerColor = Color.WHITE;
    private int mTextColor;
    private int mTextSize;
    private String mText = " ";
    private int arcWidth = 0;
    private final Paint outPaint;
    private final Paint inPaint;
    private final Paint textPaint;
    private int maxSteps = 100;
    private int currentStep = 0;
    //字体粗细
    private final static int NORMAL = 1001;
    private final static int BOLD = 1002;
    //字体样式 （BITWONDER，ORPER，VULCAN,INFINITY,POSEIDON,DAYS_LATER,DIGIT）
    private final static int BITWONDER = 1003;
    private final static int ORPER = 1004;
    private final static int VULCAN = 1005;
    private final static int INFINITY = 1006;
    private final static int POSEIDON = 1007;
    private final static int DAYS_LATER = 1008;
    private final static int DIGIT = 1009;
    private Typeface typeface;
    private @TextType
    int textType;
    private @TextTypeFace
    int textTypeFace;
    private int centerXY;
    private Context mContext;

    public PedometerView(Context context) {
        this(context, null);
        Log.e(TAG, "contexts是否为空----->1  " + (context == null ? "是" : "否"));
    }

    public PedometerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Log.e(TAG, "contexts是否为空----->2  " + (context == null ? "是" : "否"));
    }

    @SuppressLint("Recycle")
    public PedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context == null) {
            throw new RuntimeException("PedometerView Context is null,please chek your PedometerView code ");
        }
        mContext = context;
        //初始化画笔
        outPaint = new Paint();
        inPaint = new Paint();
        textPaint = new Paint();
        //防抖动，使绘制颜色平滑，不会断层
        outPaint.setDither(true);
        inPaint.setDither(true);
        textPaint.setDither(true);

        //设置抗锯齿
        outPaint.setAntiAlias(true);
        inPaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PedometerView);


        try {
            mTextColor = ta.getColor(R.styleable.PedometerView_pv_textColor, Color.BLACK);
            innerColor = ta.getColor(R.styleable.PedometerView_inner_color, Color.WHITE);
            outerColor = ta.getColor(R.styleable.PedometerView_outer_color, Color.WHITE);
            arcWidth = ta.getDimensionPixelSize(R.styleable.PedometerView_arc_width, UnitUtils.dp2px(mContext, 10));
            mTextSize = ta.getDimensionPixelSize(R.styleable.PedometerView_pv_textSize,
                    UnitUtils.sp2px(mContext, 15));
            mText = ta.getString(R.styleable.PedometerView_pv_text);
            maxSteps = ta.getInt(R.styleable.PedometerView_pv_max, 100);
            currentStep = ta.getInt(R.styleable.PedometerView_pv_current, 0);
            textType = ta.getInt(R.styleable.PedometerView_pv_textStyle, NORMAL);
            textTypeFace = ta.getInt(R.styleable.PedometerView_pv_typeface, BITWONDER);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            ta.recycle();
        }

    }

    @SuppressLint("SwitchIntDef")
    private void initPaint() {
        //设置画笔颜色
        outPaint.setColor(outerColor);
        //设置画笔宽度
        outPaint.setStrokeWidth(arcWidth);
        //设置画笔的样式：FILL->被填充满的，实心的；STROKE->描边；FILL_AND_STROKE->前两者都执行
        outPaint.setStyle(Paint.Style.STROKE);
        //设置圆弧扫过角度两端的样式（这里是圆弧）
        outPaint.setStrokeCap(Paint.Cap.ROUND);

        //里层
        inPaint.setColor(innerColor);
        inPaint.setStrokeWidth(arcWidth);
        inPaint.setStyle(Paint.Style.STROKE);
        inPaint.setStrokeCap(Paint.Cap.ROUND);

        //text
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);
//        textPaint.setTextAlign(Paint.Align.CENTER);

        //设置文本粗体
        switch (textType) {
            case NORMAL:
                textPaint.setFakeBoldText(false);
                break;
            case BOLD:
                textPaint.setFakeBoldText(true);
                break;
        }
        //设置字体样式
        switch (textTypeFace) {
            case BITWONDER:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/8bitwonder.TTF");
                break;
            case ORPER:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/orper.ttf");
                break;
            case VULCAN:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/vulcan.TTF");
                break;
            case INFINITY:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/infinity.TTF");
                break;
            case POSEIDON:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/poseidon.ttf");
                break;
            case DAYS_LATER:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/28_Days_Later.ttf");
                break;
            case DIGIT:
                typeface = Typeface.createFromAsset(getRootView().getContext().getAssets(), "fonts/digit.ttf");
                break;
        }
        textPaint.setTypeface(typeface);
        //当currentStep大于maxSteps时，将maxSteps置为与maxSteps一致大小
        if (currentStep > maxSteps) {
            maxSteps = currentStep;
        }
    }

    /**
     * 效果分析
     * 1、确定自定义属性，将需要的属性写入attrs.xml中；
     * 2、将自定义好的属性使用的布局中（layout）
     * 3、在自定义View中动态获取属性，根据TypedArray拿到写在attrs中的属性
     * 4、定义画笔,画圆、画弧、画字体
     * 5、设置动画效果
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //绘制字体测量宽高

        //获取测量宽高的模式
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        //Exactly模式宽度size
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        //Exactly模式高度size
        int hSize = MeasureSpec.getSize(widthMeasureSpec);

        //AT_MOST模式宽度size
        if (wMode == MeasureSpec.AT_MOST) {
            //指定一个固定大小
            wSize = (int) (UnitUtils.getScreenWidth(getRootView().getContext()) - (1.0f / 5.0f) * UnitUtils.getScreenWidth(getRootView().getContext()));
        }
        //AT_MOST模式高度size
        if (hMode == MeasureSpec.AT_MOST) {
            hSize = (int) (UnitUtils.getScreenWidth(getRootView().getContext()) - (1.0f / 5.0f) * UnitUtils.getScreenWidth(getRootView().getContext()));
        }
        //主要针对当测量模式为Exactly，宽高可能出现不一样的情况，而这比较取最大
        int maxSize = Math.max(wSize, hSize);
        //圆心
        centerXY = maxSize / 2;

        setMeasuredDimension(maxSize, maxSize);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1、 初始化画笔设置
        initPaint();

        //2、画外圆弧

        //圆半径
        int radius = centerXY - arcWidth;

        //获取指定画圆弧的矩形（使用RectF是因为精度更高）
        @SuppressLint("DrawAllocation") RectF rectF = new RectF(arcWidth, arcWidth, centerXY + radius, centerXY + radius);

        //指定圆弧的初始角度以及圆弧扫过的角度（圆弧扫过角度=360-空缺角度），一般的空却角度的终点就是起始角度
        //useCenter表示以圆心为原点，被扫过的圆弧都进行着色
        canvas.drawArc(rectF, 135, 270, false, outPaint);

        //3、画内圆弧
        //根据数值显示圆弧扫过的百分比

        float currentAngle = ((float) currentStep / (float) maxSteps) * 270;
//        Log.e(TAG, "currentStep= " + currentStep + "    maxSteps= " + maxSteps + "  currentAngle= " + currentAngle);
        canvas.drawArc(rectF, 135, currentAngle, false, inPaint);


        //4、画文字(文字在正中间)
        //文字X轴上的基点
        mText = String.valueOf(currentStep);

        float x = (getWidth() >> 1) - textPaint.measureText(mText) / 2;
        //y轴基线
        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        //bottom正常情况下
        float dy = (fm.bottom - fm.top) / 2.0f - fm.bottom;
        //这里是保证bottom有微小的偏移量
        if (fm.bottom == 0) {
            dy = (fm.bottom - fm.top) / 2.0f - (fm.bottom - fm.top) / 20.0f;
        }
        float baseLineY = ((getHeight() >> 1) + dy);
        canvas.drawText(mText, x, baseLineY, textPaint);

    }


    public void setInnerColor(int innerColor) {
        this.innerColor = innerColor;
        invalidate();
    }

    public void setOuterColor(int outerColor) {
        this.outerColor = outerColor;
        invalidate();
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = UnitUtils.sp2px(getRootView().getContext(), mTextSize);
        invalidate();
    }

    public void setmText(String mText) {
        this.mText = mText;
        invalidate();
    }

    public void setArcWidth(int arcWidth) {
        this.arcWidth = UnitUtils.dp2px(getRootView().getContext(), arcWidth);
        invalidate();
    }

    public void setTextType(int textType) {
        this.textType = textType;
        invalidate();
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
        invalidate();
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
        invalidate();
    }

    public void setTextTypeFace(int textTypeFace) {
        this.textTypeFace = textTypeFace;
    }

    @IntDef({NORMAL, BOLD})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface TextType {
    }

    @IntDef({BITWONDER, ORPER, VULCAN, INFINITY, POSEIDON, DAYS_LATER, DIGIT})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface TextTypeFace {
    }

}
