package com.edw.androidcustomviewlibs.demotest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.edw.androidcustomviewlibs.R;
import com.edw.androidcustomviewlibs.widget.IndicatorItemView;


import java.util.ArrayList;
import java.util.List;

public class IndicatorItemActivity extends AppCompatActivity {
    private String[] items = {"视频", "直播", "阅读", "新闻", "军事", "电影"};
    private List<IndicatorItemView> views;
    private ViewPager2 vp;
    private LinearLayout indicatorLayout;
    private static final String TAG = "IndicatorItemActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = new ArrayList<>();
        setContentView(R.layout.activity_indicator_item);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        vp = findViewById(R.id.viewpager);
        //动态添加IndicatorItemView到LinearLayout中
        initIndicator();
        //添加viewpager，与IndicatorItemView联动
        initViewPager();
    }


    private void initViewPager() {
        vp.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return IndicatorFragment.getInstance(items[position]);
            }

            @Override
            public int getItemCount() {
                return items.length;
            }
        });
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.e(TAG, "  position= " + position + "  positionOffset= " + positionOffset + "  positionOffsetPixels= " + positionOffsetPixels);
                //左边的title
                IndicatorItemView left = views.get(position);
                left.setDirection(IndicatorItemView.SHIFT_RIGHT);
                left.setChangeRate(1 - positionOffset);

                if (position <= items.length - 2) { //防止角标越界
                    //右边的title
                    IndicatorItemView right = views.get(position + 1);
                    right.setDirection(IndicatorItemView.SHIFT_LEFT);
                    right.setChangeRate(positionOffset);
                }

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.e(TAG, "onPageSelected position----> " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    private void initIndicator() {
        for (String item : items) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            lp.weight = 1;
            IndicatorItemView idv = new IndicatorItemView(this);
            idv.setTextSize(20);
            idv.setOriginColors(Color.BLACK);
            idv.setChangeColors(Color.RED);
            idv.setText(item);
            idv.setLayoutParams(lp);
            indicatorLayout.addView(idv);
            views.add(idv);
        }

    }


}