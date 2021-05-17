package com.edw.androidcustomviewlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
 * Date:            2021-05-13
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.github.io/
 * <p>
 * Description：    字母索引栏 <p>
 * **************************************************************************************************
 *
 * @author EdwardWMD
 */
public class AlphabeticalSearchBar extends View {
    private static final String TAG = "AlphabeticalSearchBar";
    private static final String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private Context mC;
    private int normalTextColor;
    private int selectedColor;
    private int textSize;
    private Paint mTextPaint;
    public static final int NORMAL = 0x01;
    public static final int BOLD = 0x02;
    private @TextStyle
    int mTextStyle = NORMAL;
    private int roundRadius = UnitUtils.dp2px(getRootView().getContext(), 50);
    private String currentLetter = "";
    private UpdateSelectedListener listener;
    //针对与不同手机实现不同的像素比
    private float pixelRatio;
    //左右边距之和
    private int paddingTB;
    //字母高度
    private int letterHeight;

    private boolean isActionUp = true;


    public AlphabeticalSearchBar(Context context) {
        this(context, null);
    }

    public AlphabeticalSearchBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabeticalSearchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mC = context;
        pixelRatio = UnitUtils.getScreenWidth(context) / 1080f;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AlphabeticalSearchBar);
        if (ta == null) {
            return;
        }
        try {
            normalTextColor = ta.getColor(R.styleable.AlphabeticalSearchBar_asb_normalTextColor, Color.BLACK);
            selectedColor = ta.getColor(R.styleable.AlphabeticalSearchBar_asb_selectedColor, Color.BLUE);
            textSize = ta.getDimensionPixelSize(R.styleable.AlphabeticalSearchBar_asb_textSize, UnitUtils.sp2px(context, 12));
            mTextStyle = ta.getInt(R.styleable.AlphabeticalSearchBar_asb_textStyle, NORMAL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //单个字体的宽度
        float textWidth = mTextPaint.measureText(letters[0]);
        //文本左右两边内边距
        int paddingLR = (int) ((getPaddingLeft() + getPaddingRight()) * pixelRatio);
        paddingTB = (int) ((getPaddingTop() + getPaddingBottom()) * pixelRatio);
        //整个控件实现的宽度=文本左右两边内边距+单个字体的宽度
        int wSize = (int) (textWidth + paddingLR);
        //控件高度
        int hSize = (int) (MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(wSize, hSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //针对于每个字体的高度
        letterHeight = (getHeight() - paddingTB) / letters.length;
        Paint.FontMetricsInt fm = mTextPaint.getFontMetricsInt();

        for (int i = 0; i < letters.length; i++) {
            //绘制字体X轴的原点
            int x = (int) (getWidth() / 2 - mTextPaint.measureText(letters[i]) / 2);
            //基线与字体中线的距离
            int dy = (fm.bottom - fm.top) / 2 - fm.bottom;
            //字体高度一般通过叠加绘制
            int centerY = i * letterHeight + letterHeight / 2 + getPaddingTop();
            //基线
            int baseLine = centerY + dy;

            if (!TextUtils.isEmpty(currentLetter)) {
                if (letters[i].equals(currentLetter)) {
                    //当手抬起时侧边栏的颜色恢复默认颜色
                    if (isActionUp) {
                        mTextPaint.setColor(normalTextColor);
                    } else {
                        mTextPaint.setColor(selectedColor);
                    }
                } else {
                    mTextPaint.setColor(normalTextColor);
                }
            }
            canvas.drawText(letters[i], x, baseLine, mTextPaint);

        }

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String curText = "";
        float touchY = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                int currentTouch = (int) (touchY / letterHeight);
                //防止角标越界
                if (currentTouch < 0) {
                    currentTouch = 0;
                }
                //防止角标越界
                if (currentTouch >= letters.length) {
                    currentTouch = letters.length - 1;
                }
                curText = letters[currentTouch];
                if (currentLetter.contains(curText)) {
                    return true;
                }
                currentLetter = curText;
                if (listener != null) {
                    listener.onUpdateSelectedListener(currentLetter, false);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                isActionUp = true;
                invalidate();

                postDelayed((Runnable) () -> {
                    if (listener != null) {
                        listener.onUpdateSelectedListener(currentLetter, true);
                    }
                }, 800);

                break;
        }

        return true;
    }

    //初始化画笔
    private void initPaint() {
        if (mTextPaint == null) {
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(textSize);

        switch (mTextStyle) {
            case NORMAL:
                mTextPaint.setFakeBoldText(false);
                break;
            case BOLD:
                mTextPaint.setFakeBoldText(true);
                break;
        }

    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        mTextPaint.setColor(normalTextColor);
        invalidate();
    }

    public void setNormalTextColor(int normalTextColor) {
        this.normalTextColor = normalTextColor;
        mTextPaint.setColor(normalTextColor);
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(UnitUtils.dp2px(mC, textSize));
        invalidate();
    }


    public void setTextStyle(int mTextStyle) {
        this.mTextStyle = mTextStyle;
        invalidate();
    }


    @IntDef({NORMAL, BOLD})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface TextStyle {
    }

    /**
     * 回调接口，将索引position和字母返回
     */
    public interface UpdateSelectedListener {
        /**
         * 将索引position和字母返回
         *
         * @param item       字母
         * @param isActionUp 手指是否抬起
         */
        void onUpdateSelectedListener(String item, boolean isActionUp);
    }

    public void setUpdateSelectedListener(UpdateSelectedListener listener) {
        this.listener = listener;
    }

    private void removeListener() {
        if (listener != null) {
            listener = null;
        }
    }

}
