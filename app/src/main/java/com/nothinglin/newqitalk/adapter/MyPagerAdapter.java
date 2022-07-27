package com.nothinglin.newqitalk.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
    //初始化变量
    private String[] titles;
    private List<View> viewList;

    //初始化构造器
    public MyPagerAdapter(List<View> viewList, String[] titles) {
        this.viewList = viewList;
        this.titles = titles;
    }

    //获取viewList的数量
    @Override
    public int getCount() {
        return viewList.size();
    }

    //删除具体的页面组件
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }


    //判断页面是不是来自于我们的组件页面
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    //初始化界面view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);

    }

    //获取页面标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
