package com.edw.androidcustomviewlibs.utils;

import android.content.Context;

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
 * Description：    ToDo
 * <p>
 * **************************************************************************************************
 */
public class UnitUtils {

    /**
     * dp转换px
     *
     * @param mC 上下文
     * @param dp dp
     * @return px
     */
    public static int dp2px(Context mC, float dp) {
        float scale = mC.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转换dp
     *
     * @param mC 上下文
     * @param px px
     * @return dp
     */
    public static int px2dp(Context mC, float px) {
        float scale = mC.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param mC 上下文
     * @param sp sp
     * @return px
     */
    public static int sp2px(Context mC, int sp) {
        float scale = mC.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp / scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param mC 上下文
     * @param px px
     * @return sp
     */
    public static int px2sp(Context mC, int px) {
        float scale = mC.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px * scale + 0.5f);
    }

    /**
     * 取手机屏幕宽
     *
     * @param mC 上下文
     * @return 宽
     */
    public static float getScreenWidth(Context mC) {
        return mC.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 取手机屏幕高
     *
     * @param mC 上下文
     * @return 高
     */
    public static float getScreenHeight(Context mC) {
        return mC.getResources().getDisplayMetrics().heightPixels;
    }
}
