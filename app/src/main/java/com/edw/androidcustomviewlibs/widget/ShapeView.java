package com.edw.androidcustomviewlibs.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;


import com.edw.androidcustomviewlibs.R;
import com.edw.androidcustomviewlibs.compose.AnimationState;
import com.edw.androidcustomviewlibs.utils.UnitUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * Description：    图形
 * <p>
 * **************************************************************************************************
 */
public class ShapeView extends View implements AnimationState {
    private static final String TAG = "ShapeView";
    private static final int TRIANGLE = 0X01;
    private static final int ROUND = 0X02;
    private static final int SQUARE = 0X03;
    private @ShapeType
    int shapeType = TRIANGLE;
    private int roundColor;
    private int triangleColor;
    private int squareColor;
    //画圆的笔
    private Paint mRoundPaint;
    //画三角形的笔
    private Paint mTrianglePaint;
    //画长方形的笔
    private Paint mSquarePaint;

    private Context mContext;
    private Path path;
    private ValueAnimator am;

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShapeView);
        if (ta == null) {
            return;
        }
        try {
            roundColor = ta.getColor(R.styleable.ShapeView_sp_RoundColor, Color.parseColor("#f47920"));
            triangleColor = ta.getColor(R.styleable.ShapeView_sp_triangleColor, Color.parseColor("#d71345"));
            squareColor = ta.getColor(R.styleable.ShapeView_sp_squareColor, Color.parseColor("#007d65"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }
        initPaint();
        if (path == null) {
            path = new Path();
        }
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (shapeType) {
            case ROUND:
                //画圆
                float centerXY = getWidth() >> 1;
                canvas.drawCircle(centerXY, centerXY, centerXY, mRoundPaint);
                break;
            case TRIANGLE:
                //画等边三角形
                //坐标移至矩形的上边的中点位置
                //使用勾股定理计算出等边三角形的高度
                float triangleHeight = (float) Math.sqrt((Math.pow(getWidth(), 2) - Math.pow(getWidth() >> 1, 2)));
                path.moveTo(getWidth() >> 1, 0);
                path.lineTo(0, triangleHeight);
                path.lineTo(getWidth(), triangleHeight);
                //和最先开始的坐标点闭合
                path.close();
                canvas.drawPath(path, mTrianglePaint);
                break;
            case SQUARE:
                //画正方形
                canvas.drawRect(0, 0, getWidth(), getHeight(), mSquarePaint);
                break;
            default:
                break;
        }

    }

    private void initPaint() {
        mRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setColor(roundColor);
        mTrianglePaint.setColor(triangleColor);
        mSquarePaint.setColor(squareColor);
        mSquarePaint.setDither(true);
        mTrianglePaint.setDither(true);
        mRoundPaint.setDither(true);
    }

    private void animatorForShape() {
        am = ObjectAnimator.ofInt(1, 4);
        am.setDuration(3000);
        am.setRepeatCount(ValueAnimator.INFINITE);
        am.setRepeatMode(ValueAnimator.RESTART);
        am.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if (value == ROUND) {
                shapeType = TRIANGLE;
            } else if (value == TRIANGLE) {
                shapeType = SQUARE;
            } else {
                shapeType = ROUND;
            }
            invalidate();
        });
        am.start();
    }

    @Override
    public void start() {
        animatorForShape();
    }

    @Override
    public void resume() {
        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.resume();
            }
        }
    }

    @Override
    public void paused() {
        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.pause();
            }

        }
    }

    @Override
    public void stop() {
        if (am != null) {
            am.cancel();
        }
    }

    @Override
    public void destory() {
        stop();
        if (am != null) {
            am.removeAllUpdateListeners();
            am.removeAllListeners();
            am = null;
        }
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
        mRoundPaint.setColor(roundColor);
        invalidate();
    }

    public void setTriangleColor(int triangleColor) {
        this.triangleColor = triangleColor;
        mTrianglePaint.setColor(triangleColor);
    }

    public void setSquareColor(int squareColor) {
        this.squareColor = squareColor;
        mSquarePaint.setColor(squareColor);
    }

    @IntDef({TRIANGLE, ROUND, SQUARE})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    public @interface ShapeType {
    }
}