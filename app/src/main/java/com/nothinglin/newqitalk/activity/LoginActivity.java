package com.nothinglin.newqitalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nothinglin.newqitalk.MainActivity;
import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class LoginActivity extends AppCompatActivity {
    //组件声明
    //content
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    //login
    private EditText mEd_login_username;
    private EditText mEd_login_password;
    private CheckBox mCh_save;
    private Button mBt_forget;
    private Button mBt_login;
    //register
    private EditText mEd_register_username;
    private EditText mEd_register_password;
    private EditText mEd_register_password2;
    private Button mBt_register;
    //save
    /**
     * 数据存储方式共分为5种，分别为：SharedPreferences、内部存储（Internal Storage）、外部存储（External Storage）、SQLite数据库存储、网络存储
     * SharedPreferences 是 Android 系统提供的一个通用的数据持久化框架，用于存储和读取 key-value 类型的原始基本数据对。
     * SharedPreferences 主要用于存储系统的配置信息，类似于 Windows 下常用的 .ini 文件。
     * SharedPreferences介绍. 在Android开发中，经常需要将少量简单类型数据保存在本地，如：用户设置。. 这些需要保存的数据可能一两个字符串，像这样的数据一般选择使用SharedPreferences来保存。
     */
    private SharedPreferences sha;
    private SharedPreferences.Editor ed;
    private ProgressDialog mProgressDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化页面
        initView();

        //初始化数据
        initData();

        //初始化监听器
        initListener();
    }


    private void initView() {
        //初始化设置界面 -- 登录、注册切换的主界面
        setContentView(R.layout.activity_login_main_switch);

        //获取界面需要操作的组件id
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        mTabLayout = (TabLayout) findViewById(R.id.tab_login);
        mViewPager = (ViewPager) findViewById(R.id.vp_login);

        /**
         * 在实际开发会用到LayoutInflater这个类,它的作用类似于 findViewById()。
         * LayoutInflater是用来找layout下xml布局文件，并且实例化，而findViewById()是找具体xml下的具体 widget控件(如:Button,TextView等)。
         * 对于一个没有被载入或者想要动态加载的界面，都需要使用LayoutInflater.inflate()来载入。
         * 在应用中有时候需要通过某些点击效果动态地添加布局，而不是直接加载完整的xml布局文件，就需要动态载入。
         * https://blog.csdn.net/panxianhao_/article/details/78383733
         */

        //动态界面加载组件对象
        LayoutInflater inflater = this.getLayoutInflater();
        //登录界面和注册界面需要动态加载
        View view1 = inflater.inflate(R.layout.activity_login_login, null);
        View view2 = inflater.inflate(R.layout.activity_login_register, null);

        //封装动态view
        List<View> viewList = new ArrayList<View>();
        viewList.add(view1);
        viewList.add(view2);

        //获取动态view的标题
        String[] titles = new String[]{
                getResources().getString(R.string.login), getResources().getString(R.string.register)
        };
        //对动态view进行适配,适配mViewPager
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(viewList, titles);
        mViewPager.setAdapter(pagerAdapter);

        //初始化登陆界面中的可操作组件
        //login
        mEd_login_username = (EditText) view1.findViewById(R.id.ed_login_username);
        mEd_login_password = (EditText) view1.findViewById(R.id.ed_login_password);
        mCh_save = (CheckBox) view1.findViewById(R.id.ch_login_save);
        mBt_forget = (Button) view1.findViewById(R.id.bt_login_forget);
        mBt_login = (Button) view1.findViewById(R.id.bt_login);

        //初始化注册界面中的可操作组件
        //register
        mEd_register_username = (EditText) view2.findViewById(R.id.ed_register_username);
        mEd_register_password = (EditText) view2.findViewById(R.id.ed_register_password);
        mEd_register_password2 = (EditText) view2.findViewById(R.id.ed_register_password2);
        mBt_register = (Button) view2.findViewById(R.id.bt_register);


    }


    private void initData() {
        //设置页面标题栏
        setSupportActionBar(mToolbar);

        //让bar和view进行联动，tablayout封装有对应的bar，我们把它和页面进行联系
        /**
         * https://blog.csdn.net/knight1996/article/details/79018054#:~:text=%E6%8A%8ATabLayo,wPager%E8%81%94%E5%8A%A8
         */
        mTabLayout.setupWithViewPager(mViewPager);

        /**
         * getSharedPreferences有两个参数 这两个参数的代表的是什么呢？
         * 第一个参数getSharedPreferences（第一个参数，第二个参数）;
         * 第一个参数是存储时的名称，第二个参数则是文件的打开方式~
         *  两个参数，第一个参数是preferece的名称(比如：MyPref),第二个参数是打开的方式（一般选择private方式）
         *
         *  除了SQLite数据库外，SharedPreferences也是一种轻型的数据存储方式，它的本质是基于XML文件存储key-value键值对数据，通常用来存储一些简单的配置信息
         */
        sha = getSharedPreferences("log", Activity.MODE_PRIVATE);
        ed = sha.edit();

        //如果is_S是false的话执行
        //保存登录信息
        if (sha.getBoolean("is_S", false)) {
            //清空界面输入框中的数据
            mEd_login_username.setText(sha.getString("username", ""));
            mEd_login_password.setText(sha.getString("password", ""));
            mCh_save.setChecked(true);
        } else {
            mCh_save.setChecked(false);
        }
    }


    private void initListener() {

        //?????????????????
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //忘记密码点击监听
        mBt_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "功能未开发", Toast.LENGTH_SHORT).show();
            }
        });

        //登录监听
        mBt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 在登录的过程中，等待的过程中提示正在登录，触碰空白处取消
                 */
                mProgressDialog = ProgressDialog.show(LoginActivity.this, "提示：", "正在载入中...");
                mProgressDialog.setCanceledOnTouchOutside(true);

                //获取输入的账号、密码信息
                final String username = mEd_login_username.getText().toString();
                final String password = mEd_login_password.getText().toString();

                //判断是否记住账号
                if (mCh_save.isChecked()) {
                    ed.putString("username", username);
                    ed.putString("password", password);
                    ed.putBoolean("is_S", true);
                } else {
                    ed.putBoolean("is_S", false);
                }

                //保存到本地
                ed.commit();


                //访问JMessage服务器进行登录
                JMessageClient.login(username, password, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {

                        //登陆成功
                        if (i == 0) {
                            JMessageClient.getUserInfo(username, null, new GetUserInfoCallback() {
                                @Override
                                public void gotResult(int i, String s, UserInfo userInfo) {
                                    if (i == 0) {
                                        //如果登录成功，取消弹出提示的正在登录
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();

                                        //界面跳转
                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "登陆失败" + s, Toast.LENGTH_LONG).show();
                                        JMessageClient.logout();
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"登录失败" + ":" + s,Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });



        //注册的事件监听
        mBt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取输入的数值
                final String username = mEd_register_username.getText().toString();
                final String password = mEd_register_password.getText().toString();
                final String password2 = mEd_register_password2.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this,
                            "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password2.equals(password)) {
                    Toast.makeText(LoginActivity.this,
                            "密码不一致",
                            Toast.LENGTH_SHORT).show();
                    mEd_register_password2.setText("");
                    return;
                }

                mProgressDialog = ProgressDialog.show(LoginActivity.this,
                        "提示：",
                        "正在加载中...");


                //访问极光服务器进行账号注册
                JMessageClient.register(username, password, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {

                        //注册成功
                        if (i == 0){
                            //关闭加载窗口
                            if (mProgressDialog != null && mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }
                            //赋值给登录界面的输入框，以免再次输入
                            mEd_login_username.setText(username);
                            mEd_login_password.setText(password);

                            Toast.makeText(getApplicationContext(),
                                   "注册成功",
                                    Toast.LENGTH_LONG).show();

                            //将页面返回登录界面
                            mViewPager.setCurrentItem(0);

                        }else {
                            //注册失败
                            if (mProgressDialog != null && mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }

                            Toast.makeText(getApplicationContext(),
                                    "注册失败"+s,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });



            }
        });

    }
}
