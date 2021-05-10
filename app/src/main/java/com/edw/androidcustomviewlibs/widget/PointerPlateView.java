package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
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
 * Date:            2021-05-08
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.gitee.io/
 * <p>
 * Description：    是一个经典性指针盘，自定义控件实现
 * <p>
 * **************************************************************************************************
 */
public class PointerPlateView extends View {
    private static final String TAG = "PointerPlateView";
    private String stepText = " ";
    private String unitText = " ";
    private String dataUnit = "KM/h";
    private int stepTextColor;
    private int stepTextSize;
    private int ringColor1;
    private int ringColor2;
    private int ringColor3;
    private String leftTopText = " ";
    private int leftTopTextColor;
    private int leftTopTextSize;
    private String rightTopText = " ";
    private int rightTopTextColor;
    private int rightTopTextSize;
    private Context mC;
    private int outRingBackground;
    private int outRingBackgroundWidth;
    private @ScaleWidthType
    int scaleWidthType;
    private int scaleRingWidth;
    //刻度粗细
    private static final int BIG = 101;
    private static final int NORMAL = 102;
    private static final int SMALL = 103;
    private float maxValue = 100;
    private float currentValue = 0;

    private float innerLineWidth = UnitUtils.dp2px(getRootView().getContext(), 3);
    //圆环扫过的角度
    private static final float SWEEPANGLE = 360.0f;
    //圆环总刻度，为了更好算，这里使用99
    private static final int SCALEFULLCOUNT = 99;
    //字体样式类型（7种）
    private final static int BITWONDER = 104;
    private final static int ORPER = 105;
    private final static int VULCAN = 106;
    private final static int INFINITY = 107;
    private final static int POSEIDON = 108;
    private final static int DAYS_LATER = 109;
    private final static int DIGIT = 110;
    //字体粗细
    private final static int NORMAL_TEXT = 111;
    private final static int BOLD = 112;
    private @TextTypeFace
    int textType;
    private @TextStyle
    int textStyle;

    //5根特殊长度的刻度长
    private int[] specialLineWidth = new int[]{
            UnitUtils.dp2px(getRootView().getContext(), 1f),
            UnitUtils.dp2px(getRootView().getContext(), 2f),
            UnitUtils.dp2px(getRootView().getContext(), 3f),
            UnitUtils.dp2px(getRootView().getContext(), 3.2f),
            UnitUtils.dp2px(getRootView().getContext(), 3.8f)
    };


    //适配分辨率
    private float rate;
    //最外层渐变颜色背景画笔
    private Paint lightColorPaint;
    //刻度盘（空）画笔
    private Paint scaleInnerSPaint;
    //当前数值所占用的刻度盘（着色）画笔
    private Paint scaleOuterSPaint;
    //内里层的小圆圈画笔
    private Paint smallArcPaint;
    //画最里面字体的画笔
    private Paint stepTextPaint;
    //数据单位
    private Paint unitPaint;

    private Paint filledCircle;
    //圆心距离
    private int centerXY;
    //最外部背景圆环的半径
    private float radius;
    //排除圆环宽度的偏移值
    private float dy1;
    //刻度圆盘真实半径
    private float scaleRadius;
    //每个刻度的角度
    private float preTickAngle;
    //绘制刻度的起点
    private float startX;
    //绘制刻度的终点
    private float startY;
    private Typeface typeface;
    private float py;
    private RadialGradient lg = null;
    private float innerRadius;


    public PointerPlateView(Context context) {
        this(context, null);
    }


    public PointerPlateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerPlateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context == null) {
            throw new RuntimeException("PointerPlateView context is null,please check your PointerPlateView code!");
        }
        mC = context;
        //屏幕缩放比率
        rate = UnitUtils.getScreenWidth(context) / 1080.0f;
        //初始化各种画笔(附带抗锯齿效果 ANTI_ALIAS_FLAG)
        lightColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleInnerSPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleOuterSPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stepTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        filledCircle = new Paint(Paint.ANTI_ALIAS_FLAG);

        //防抖动（颜色过渡柔和，不会显现出断层的现象）
        lightColorPaint.setDither(true);
        scaleInnerSPaint.setDither(true);
        scaleOuterSPaint.setDither(true);
        smallArcPaint.setDither(true);
        stepTextPaint.setDither(true);
        unitPaint.setDither(true);
        filledCircle.setDither(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PointerPlateView);
        if (ta == null) return;

        try {
            stepText = ta.getString(R.styleable.PointerPlateView_pp_stepText);
            unitText = ta.getString(R.styleable.PointerPlateView_pp_unit);
            stepTextColor = ta.getColor(R.styleable.PointerPlateView_pp_stepTextColor, Color.BLACK);
            stepTextSize = ta.getDimensionPixelSize(R.styleable.PointerPlateView_pp_stepTextSize, UnitUtils.dp2px(context, 30));
            ringColor1 = ta.getColor(R.styleable.PointerPlateView_pp_ringColor_1, Color.parseColor("#f47920"));
            ringColor2 = ta.getColor(R.styleable.PointerPlateView_pp_ringColor_2, Color.parseColor("#d71345"));
            ringColor3 = ta.getColor(R.styleable.PointerPlateView_pp_ringColor_3, Color.parseColor("#005344"));
            leftTopText = ta.getString(R.styleable.PointerPlateView_pp_leftTopText);
            leftTopTextColor = ta.getColor(R.styleable.PointerPlateView_pp_leftTopTextColor, Color.parseColor("#005344"));
            leftTopTextSize = ta.getDimensionPixelSize(R.styleable.PointerPlateView_pp_leftTopTextSize, UnitUtils.dp2px(context, 15));
            rightTopText = ta.getString(R.styleable.PointerPlateView_pp_rightTopText);
            rightTopTextColor = ta.getColor(R.styleable.PointerPlateView_pp_rightTopTextColor, Color.parseColor("#005344"));
            rightTopTextSize = ta.getDimensionPixelSize(R.styleable.PointerPlateView_pp_rightTopTextSize, UnitUtils.dp2px(context, 15));
            outRingBackground = ta.getColor(R.styleable.PointerPlateView_pp_outRingBackground, Color.parseColor("#005344"));
            outRingBackgroundWidth = ta.getDimensionPixelSize(R.styleable.PointerPlateView_pp_outRingBackgroundWidth, UnitUtils.dp2px(context, 35));
            scaleWidthType = ta.getInt(R.styleable.PointerPlateView_pp_scaleWidth, NORMAL);
            scaleRingWidth = ta.getDimensionPixelSize(R.styleable.PointerPlateView_pp_scaleRingWidth, UnitUtils.dp2px(context, 35));
            textType = ta.getInt(R.styleable.PointerPlateView_pp_typeFace, VULCAN);
            textStyle = ta.getInt(R.styleable.PointerPlateView_pp_textStyle, BOLD);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }

    }


    private void initPaint() {
        lightColorPaint.setStyle(Paint.Style.STROKE);
        lightColorPaint.setStrokeWidth(outRingBackgroundWidth * rate);


        scaleInnerSPaint.setStyle(Paint.Style.FILL);
        scaleOuterSPaint.setStyle(Paint.Style.FILL);

        smallArcPaint.setStyle(Paint.Style.STROKE);
        smallArcPaint.setColor(Color.parseColor("#3e4145"));
        smallArcPaint.setStrokeWidth(innerLineWidth * rate);

        stepTextPaint.setStyle(Paint.Style.FILL);
        stepTextPaint.setColor(Color.parseColor("#d21e21"));
        stepTextPaint.setTextSize(stepTextSize * rate);

        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setColor(Color.parseColor("#d21e21"));
        unitPaint.setTextSize((stepTextSize * rate) / 2);

        filledCircle.setStyle(Paint.Style.FILL);
        filledCircle.setColor(Color.parseColor("#007d65"));
        switch (scaleWidthType) {
            case BIG:
                scaleInnerSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 7));
                scaleOuterSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 7));
                break;
            case NORMAL:
                scaleInnerSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 5));
                scaleOuterSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 5));
                break;
            case SMALL:
                scaleInnerSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 3));
                scaleOuterSPaint.setStrokeWidth(UnitUtils.dp2px(mC, 3));
                break;
        }
        switch (textType) {
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
        stepTextPaint.setTypeface(typeface);
        unitPaint.setTypeface(typeface);

        switch (textStyle) {
            case NORMAL_TEXT:
                stepTextPaint.setFakeBoldText(false);
                unitPaint.setFakeBoldText(false);
                break;
            case BOLD:
                stepTextPaint.setFakeBoldText(true);
                unitPaint.setFakeBoldText(true);
                break;
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        if (wMode == MeasureSpec.AT_MOST) {
            //指定一个宽度
            wSize = (int) (UnitUtils.getScreenWidth(mC) - UnitUtils.getScreenWidth(mC) / 20.0f);
        }
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (hMode == MeasureSpec.AT_MOST) {
            //指定一个高度
            hSize = (int) (UnitUtils.getScreenWidth(mC) - UnitUtils.getScreenWidth(mC) / 20.0f);
        }
        //做比较，保证整个view画布是正方形
        int maxSize = Math.max(wSize, hSize);
        //最外发光的布局背景
        centerXY = maxSize / 2;
        //获取圆环的圆心
        radius = centerXY - (outRingBackgroundWidth * rate) / 2;
        //排除掉最外背景圆环的StrokeWidth以及刻度圆环自身的StrokeWidth
        dy1 = outRingBackgroundWidth * rate + (scaleRingWidth * rate) / 2;
        //刻度圆环的真实半径
        scaleRadius = centerXY - dy1;
        //实现测距
        setMeasuredDimension(maxSize, maxSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔参数
        initPaint();
        //最外部分的背景
        outerBackground(canvas);
        //空白指针盘（max）
        innerFullScaleArc(canvas);
        //当前数值对应指针的进度
        outerScaleArc(canvas);
        //最内部细小圆环
        innerThinArc(canvas);
        canvas.drawCircle(centerXY,centerXY,innerRadius-(innerLineWidth*rate)/2,filledCircle);
        //圆环中心显示数值的文本
        innerStepText(canvas);
        //单位文本
        innerUnitText(canvas);

    }

    private void innerUnitText(Canvas canvas) {
        unitText = dataUnit;
        float x = (getWidth() >> 1) - unitPaint.measureText(unitText) / 2;
        Paint.FontMetricsInt fm = unitPaint.getFontMetricsInt();
        int offset = fm.bottom - fm.top;
        float baseLineByUnitTextY = py + offset;

        canvas.drawText(unitText, x, baseLineByUnitTextY, unitPaint);
    }

    private void innerStepText(Canvas canvas) {
        stepText = String.valueOf((int) currentValue);
        Paint.FontMetricsInt fm = stepTextPaint.getFontMetricsInt();
        float x = (getWidth() >> 1) - stepTextPaint.measureText(stepText) / 2;
        py = (getWidth() >> 1) + ((fm.bottom - fm.top) >> 1);
        float baseLineByStepTextY = py - fm.bottom;
        canvas.drawText(stepText, x, baseLineByStepTextY, stepTextPaint);
    }

    private void innerThinArc(Canvas canvas) {
        float dy2 = outRingBackgroundWidth * rate + scaleRingWidth * rate + innerLineWidth * 2 * rate;
        innerRadius = centerXY - dy2;
        @SuppressLint("DrawAllocation") RectF innerRectF = new RectF(dy2, dy2, centerXY + innerRadius, centerXY + innerRadius);
        canvas.drawArc(innerRectF, 0, 360, false, smallArcPaint);
    }


    private void outerScaleArc(Canvas canvas) {
        //保存上面的数据
        canvas.save();
        float currentScaleCount = currentValue / maxValue * SCALEFULLCOUNT;
        //将画的刻度原点平移至圆环的圆心位置上
        canvas.translate(centerXY, centerXY);
        //根据当前数据换算出来的刻度所处的区间显来示颜色(99个刻度分三份)
        if (currentScaleCount <= 33) {
            scaleOuterSPaint.setColor(Color.parseColor("#2a5caa"));
        } else if (currentScaleCount <= 66) {
            scaleOuterSPaint.setColor(Color.parseColor("#f58220"));
        } else {
            scaleOuterSPaint.setColor(Color.parseColor("#ed1941"));
        }

        for (int i = 0; i < currentScaleCount; i++) {
            //判断后面五根指针，并增加其长度
            if (i > currentScaleCount - specialLineWidth.length) {
                canvas.drawLine(0, startX, 0, startY + specialLineWidth[(specialLineWidth.length - 1) - ((int) currentScaleCount - i)] * rate, scaleOuterSPaint);
            }
            canvas.drawLine(0, startX, 0, startY, scaleOuterSPaint);
            canvas.rotate(preTickAngle);
        }
        canvas.restore();
    }

    private void innerFullScaleArc(Canvas canvas) {
        //保存上面刻度盘绘制圆弧的结果
        canvas.save();
        //将坐标平移至圆心，原来的圆心位置是矩形的左上角（0，0），圆心位置是圆环所在的圆心位置，即半径
        canvas.translate(centerXY, centerXY);
        //绘制每个刻度间隔的角度（100）
        preTickAngle = SWEEPANGLE / SCALEFULLCOUNT;
        startX = scaleRadius - (scaleRingWidth * rate) / 2;
        startY = scaleRadius + (scaleRingWidth * rate) / 2;

        for (int i = 0; i <= SCALEFULLCOUNT; i++) {
            //将刻度盘平均分三等分
            if (i <= SCALEFULLCOUNT / 3) {
                scaleInnerSPaint.setColor(Color.parseColor("#952a5caa"));
            } else if (i <= SCALEFULLCOUNT * 2 / 3) {
                scaleInnerSPaint.setColor(Color.parseColor("#95f58220"));
            } else {
                scaleInnerSPaint.setColor(Color.parseColor("#95ed1941"));
            }
            canvas.drawLine(0, startX, 0, startY, scaleInnerSPaint);
            canvas.rotate(preTickAngle);
        }
        canvas.restore();
    }

    /**
     * 画最外层背景
     *
     * @param canvas 画布
     */
    private void outerBackground(Canvas canvas) {

        if (currentValue == 0) {
            lg = new RadialGradient(getWidth() >> 1,
                    getHeight() >> 1,
                    radius, Color.TRANSPARENT, Color.TRANSPARENT, TileMode.CLAMP);
        } else if (currentValue <= maxValue / 3) {
            lg = new RadialGradient(getWidth() >> 1,
                    getHeight() >> 1,
                    radius, new int[]{
                    Color.parseColor("#2a5caa"),
                    Color.parseColor("#ff2a5caa"),
                    Color.parseColor("#752a5caa"),
                    Color.parseColor("#012a5caa")

            }, null, TileMode.CLAMP);
        } else if (currentValue <= (maxValue * 2) / 3) {
            lg = new RadialGradient(getWidth() >> 1,
                    getHeight() >> 1,
                    radius, new int[]{
                    Color.parseColor("#f58220"),
                    Color.parseColor("#fff58220"),
                    Color.parseColor("#75f58220"),
                    Color.parseColor("#01f58220")

            }, null, TileMode.CLAMP);
        } else {
            lg = new RadialGradient(getWidth() >> 1,
                    getHeight() >> 1,
                    radius, new int[]{
                    Color.parseColor("#ed1941"),
                    Color.parseColor("#ffed1941"),
                    Color.parseColor("#75ed1941"),
                    Color.parseColor("#01ed1941")

            }, null, TileMode.CLAMP);
        }

        lightColorPaint.setShader(lg);
        @SuppressLint("DrawAllocation") RectF rectF = new RectF((outRingBackgroundWidth * rate) / 2, (outRingBackgroundWidth * rate) / 2, centerXY + radius, centerXY + radius);
        canvas.drawArc(rectF, 0, 360, false, lightColorPaint);
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
        invalidate();
    }

    public void setStepTextColor(int stepTextColor) {
        this.stepTextColor = stepTextColor;
        invalidate();
    }

    public void setStepTextSize(int stepTextSize) {
        this.stepTextSize = stepTextSize;
        invalidate();
    }

    public void setRingColor1(int ringColor1) {
        this.ringColor1 = ringColor1;
        invalidate();
    }

    public void setRingColor2(int ringColor2) {
        this.ringColor2 = ringColor2;
        invalidate();
    }

    public void setRingColor3(int ringColor3) {
        this.ringColor3 = ringColor3;
        invalidate();
    }

    public void setLeftTopText(String leftTopText) {
        this.leftTopText = leftTopText;
        invalidate();
    }

    public void setLeftTopTextColor(int leftTopTextColor) {
        this.leftTopTextColor = leftTopTextColor;
        invalidate();
    }

    public void setLeftTopTextSize(int leftTopTextSize) {
        this.leftTopTextSize = leftTopTextSize;
        invalidate();
    }

    public void setRightTopText(String rightTopText) {
        this.rightTopText = rightTopText;
        invalidate();
    }

    public void setRightTopTextColor(int rightTopTextColor) {
        this.rightTopTextColor = rightTopTextColor;
        invalidate();
    }

    public void setRightTopTextSize(int rightTopTextSize) {
        this.rightTopTextSize = rightTopTextSize;
        invalidate();
    }

    public void setOutRingBackground(int outRingBackground) {
        this.outRingBackground = outRingBackground;
        invalidate();
    }

    public void setOutRingBackgroundWidth(int outRingBackgroundWidth) {
        this.outRingBackgroundWidth = outRingBackgroundWidth;
        invalidate();
    }

    public void setScaleWidthType(int scaleWidthType) {
        this.scaleWidthType = scaleWidthType;
        invalidate();
    }

    public void setScaleRingWidth(int scaleRingWidth) {
        this.scaleRingWidth = scaleRingWidth;
        invalidate();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
        invalidate();
    }

    public void setTextType(int textType) {
        this.textType = textType;
        invalidate();
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
        invalidate();
    }

    @IntDef({BIG, NORMAL, SMALL})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    public @interface ScaleWidthType {
    }

    @IntDef({BITWONDER, ORPER, VULCAN, INFINITY, POSEIDON, DAYS_LATER, DIGIT})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface TextTypeFace {
    }

    @IntDef({NORMAL_TEXT, BOLD})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface TextStyle {
    }

}
