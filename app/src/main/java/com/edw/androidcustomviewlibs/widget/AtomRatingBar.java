package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.edw.androidcustomviewlibs.R;


/**
 * **************************************************************************************************
 * Project Name:    CustomViewBasics
 * <p>
 * Date:            2021-05-12
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.github.io/
 * <p>
 * Description：    自定义五角星点赞功能
 * <p>
 * **************************************************************************************************
 */
public class AtomRatingBar extends View {
    private static final String TAG = "AtomRatingBar";

    private Bitmap mNormalBitmap, mSelectedBitmap;
    private int maxRatingLevel;
    private int currentRatingLevel = 0;
    private int normalRes;
    private int selectedRes;
    private Context mContext;
    private Paint mNormalPaint;
    private Paint mSelectedPaint;
    private int gapLR;
    private int gapTB;

    public AtomRatingBar(Context context) {
        this(context, null);
    }

    public AtomRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AtomRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AtomRatingBar);
        if (ta == null) {
            return;
        }
        try {
            currentRatingLevel = ta.getInt(R.styleable.AtomRatingBar_rb_currentRatingLevel, 0);
            maxRatingLevel = ta.getInt(R.styleable.AtomRatingBar_rb_maxRatingLevel, 5);
            normalRes = ta.getResourceId(R.styleable.AtomRatingBar_rb_normalRes, 0);
            if (normalRes == 0) {
                throw new RuntimeException("normalRes 资源为null,请指定一个normalRes的资源！");
            }
            Options opts;
            //获取图片
            mNormalBitmap = BitmapFactory.decodeResource(getResources(), normalRes);
            selectedRes = ta.getResourceId(R.styleable.AtomRatingBar_rb_selectedRes, 0);
            if (selectedRes == 0) {
                throw new RuntimeException("selectedRes 资源为null,请指定一个selectedRes的资源！");
            }
            //获取图片
            mSelectedBitmap = BitmapFactory.decodeResource(getResources(), selectedRes);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }

        mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mNormalPaint.setDither(true);
        mSelectedPaint.setDither(true);


    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hSize = 0;
        int wSize = 0;
        gapLR = getPaddingLeft() + getPaddingRight();
        gapTB = getPaddingBottom() + getPaddingTop();

        if (gapLR > 0) {
            //因为mNormalBitmap和mSelectedBitmap图片都是在iconfont取的，二者图片大小差不知道，建议取大的
            wSize = (Math.max(mNormalBitmap.getWidth(), mSelectedBitmap.getWidth()) + gapLR) * maxRatingLevel;
        } else {

            wSize = (Math.max(mNormalBitmap.getWidth(), mSelectedBitmap.getWidth())) * maxRatingLevel;
        }

        if (gapTB > 0) {
            //为了避免图片存在高度差，尽量把这个高度差也加上
            hSize = Math.max(mNormalBitmap.getHeight(), mSelectedBitmap.getHeight()) + gapTB;

        } else {
            hSize = Math.max(mNormalBitmap.getHeight(), mSelectedBitmap.getHeight());
        }

        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "getWidth= " + getWidth() + "   getHeight= " + getHeight());
        for (int i = 0; i < maxRatingLevel; i++) {
            //先画默认的星星图片
            //水平方向绘制，需要绘制maxRatingLevel个bitmap加左右边距之和的长度
            int x = (mNormalBitmap.getWidth() + gapLR) * i;
            //需要绘制上下边距之和的高度
            int y = gapTB;
            //当左右存在内边距时，坐标向右水平平移getPaddingLeft()大小的距离
            //当上下存在内边距时，坐标垂直向上平移getPaddingTop()大小的距离
            //主要解决设置了padding无法居中的问题
            scrollXy();
            //绘制默认图片
            if (currentRatingLevel > i) {
                canvas.drawBitmap(mSelectedBitmap, x, y, mSelectedPaint);
            } else {
                canvas.drawBitmap(mNormalBitmap, x, y, mNormalPaint);

            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            //手指按下屏幕
            //case MotionEvent.ACTION_DOWN:
            // 手指移动(调用此方法，down和up也会一起调用)
            case MotionEvent.ACTION_MOVE:
                //表示在当前这个自定义控件内水平移动的距离，
                // 总距离/单个五角星的宽度就是我们选中的个数
                //后面为什么+1，
                float moveX = event.getX();

                int currentCount = (int) (moveX / (mSelectedBitmap.getWidth() + gapLR) + 1);
                if (currentCount < 0) {
                    currentCount = 0;
                }
                if (currentCount > maxRatingLevel) {
                    currentCount = maxRatingLevel;
                }
                //当获取的星星个数一样时，停止invalidate，防止多次调用onDraw
                if (currentCount == currentRatingLevel) {
                    return true;
                }
                currentRatingLevel = currentCount;
                invalidate();
                break;
        }

        //返回false表示不消费事件，第一次按下action_down后，
        // down以后的事件都进不来，所以要置为true

        return true;

    }

    private void scrollXy() {
        scrollTo(-getPaddingLeft(), getPaddingTop());
    }
}
