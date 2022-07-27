package com.nothinglin.newqitalk.application;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化JMessage SDK
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
    }
}
