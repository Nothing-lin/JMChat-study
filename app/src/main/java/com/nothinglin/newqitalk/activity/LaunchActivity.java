package com.nothinglin.newqitalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.nothinglin.newqitalk.MainActivity;
import com.nothinglin.newqitalk.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * 启动页面功能
 */
public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //启动界面延迟2s,执行界面跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断启动界面启动后是跳转到登陆界面还是主界面
                initAfterLaunch();
            }
        },2000);



    }

    private void initAfterLaunch() {
        Intent intent  = new Intent();
        //到极光服务器中获取用户信息看能不能获取得到，获取得到说明已经登录过了
        UserInfo myinfo = JMessageClient.getMyInfo();

        //已经登录过了的就跳转到主界面，没登陆过需要跳转到登陆界面
        if (myinfo != null){
            intent.setClass(LaunchActivity.this, MainActivity.class);
        }else {
            intent.setClass(LaunchActivity.this,LoginActivity.class);
        }

        startActivity(intent);
        //启动界面进行销毁
        finish();
    }
}
