package com.edw.androidcustomviewlibs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
 * Date:            2021-05-18
 * <p>
 * Author：         EdwardWMD
 * <p>
 * Github:          https://github.com/Edwardwmd
 * <p>
 * Blog:            https://edwardwmd.github.io/
 * <p>
 * Description：    ToDo
 * <p>
 * **************************************************************************************************
 */
public class TagTextView extends View {
    private static final String TAG = "TagTextView";
    private Paint mTextPaint;
    private Paint mTagShapePaint;
    private int tagColor = Color.RED;
    private int textColor = Color.WHITE;
    private String mText;
    private int mTextSize = UnitUtils.sp2px(getRootView().getContext(), 18);
    private float textWidth;
    private int paddingLR;
    private int paddingTB;
    private int width;
    private Path path;


    public TagTextView(Context context) {
        this(context, null);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagTextView);
        try {
            mText = ta.getString(R.styleable.TagTextView_ttv_text);
            if (TextUtils.isEmpty(mText)) {
                mText = "优惠券";
            }
            tagColor = ta.getColor(R.styleable.TagTextView_ttv_tagColor, Color.RED);
            textColor = ta.getColor(R.styleable.TagTextView_ttv_textColor, Color.WHITE);
            mTextSize = ta.getDimensionPixelSize(R.styleable.TagTextView_ttv_textSize, UnitUtils.sp2px(context, 18));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            ta.recycle();
        }

        initPaint();

    }

    private void initPaint() {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setDither(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        mTagShapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTagShapePaint.setDither(true);
        mTagShapePaint.setStyle(Paint.Style.FILL);
        mTagShapePaint.setColor(tagColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //单个字符的高度
        textWidth = mTextPaint.measureText(String.valueOf(mText.charAt(0)));
        paddingLR = getPaddingLeft() + getPaddingRight();
        paddingTB = getPaddingTop() + getPaddingBottom();

        //宽度
        width = (int) (textWidth + paddingLR);
        //高度（测量字体宽度*2）
        int height = (int) (mTextPaint.measureText(mText) * 2) + paddingTB;
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //一个字符的高度
        int textHeight = (getHeight() - paddingTB) / mText.length();
        int centerY = 0;

        if (path == null) {
            path = new Path();
        }
        //1、先画Tag的形状
        //先将画笔移动到原点绘制
        path.moveTo(0, 0);
        path.lineTo(0, getHeight());
        if (mText.length() == 1) {
            path.lineTo(getWidth() >> 1, (int) (textHeight*3 / 4)+getPaddingTop());
        } else {
            path.lineTo(getWidth() >> 1, (int) ((2 * textHeight * mText.length()) / 3) + (textHeight >> 1)+getPaddingTop());
        }
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), 0);
        path.close();
        canvas.drawPath(path, mTagShapePaint);

        //2、绘制字体，字体是竖排绘制
        for (int i = 0; i < mText.length(); i++) {
            int startX = (int) (getWidth() / 2 - mTextPaint.measureText(String.valueOf(mText.charAt(i))) / 2);
            Paint.FontMetricsInt fm = mTextPaint.getFontMetricsInt();
            int offsetY = (fm.bottom - fm.top) / 2 - fm.bottom;
            centerY = (textHeight * i * 2) / 3 + textHeight / 2 + getPaddingTop();
            int baseLine = centerY + offsetY;
            canvas.drawText(String.valueOf(mText.charAt(i)), startX, baseLine, mTextPaint);
        }
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }
}
