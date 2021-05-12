package com.edw.androidcustomviewlibs.compose;

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
 * Description：    动画的执行方法
 * <p>
 * **************************************************************************************************
 */
public interface AnimationState {
    /**
     * 开始动画
     */
    void start();

    /**
     * 继续动画
     */
    void resume();

    /**
     * 暂停动画
     */
    void paused();

    /**
     * 彻底停止动画
     */
    void stop();

    /**
     * 结束回收动画资源
     */
    void destory();
}
