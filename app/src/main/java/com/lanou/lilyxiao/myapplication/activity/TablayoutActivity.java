package com.lanou.lilyxiao.myapplication.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.widget.FrameLayout;

import com.lanou.lilyxiao.myapplication.R;
import com.lanou.lilyxiao.myapplication.adapter.BaseFragmentPagerAdapter;
import com.lanou.lilyxiao.myapplication.fragment.BaseFragment;
import com.lanou.lilyxiao.myapplication.fragment.HomeFragment;
import com.lanou.lilyxiao.myapplication.fragment.ShopFragment;
import com.nantaphop.hovertouchview.HoverTouchHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */

public class TablayoutActivity extends BaseActivity {
    @Bind(R.id.tablayout)
    TabLayout mTablayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private BaseFragmentPagerAdapter mAdapter;

    /*ViewPager的左右边界值 当已经滑到最边界时可以做相关跳转*/
    private EdgeEffectCompat leftEdge, rightEdge;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tablayout;
    }

    @Override
    protected void initView() {
        mAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTablayout.setupWithViewPager(mViewPager, true);

        /*拿到ViewPager的边界条EdgeEffectCompat，判断是否到了边界*/
        try {
            Field leftEdgeField = mViewPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = mViewPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(mViewPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(mViewPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setOnClick() {

    }

    @Override
    protected void initData() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new ShopFragment());

        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("商店");

        mAdapter.setFragments(fragments, titles);

    }

    @Override
    protected void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (rightEdge != null && !rightEdge.isFinished()) {//到了最后一张并且还继续拖动，出现蓝色限制边条了
                    showToast("已经到右边界了");
                }

                if (leftEdge != null && !leftEdge.isFinished()){
                    showToast("已经到左边界了");
                }
            }
        });
    }

}
