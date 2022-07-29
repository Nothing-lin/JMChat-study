package com.nothinglin.newqitalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.nothinglin.newqitalk.activity.LoginActivity;
import com.nothinglin.newqitalk.fragment.ContactFragment;
import com.nothinglin.newqitalk.fragment.FindFregment;
import com.nothinglin.newqitalk.fragment.MessageFragment;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //注册组件 --后面再获取对应的id
    //nav
    //private CircleImageView img_header;
    private TextView tv_username;
    private TextView tv_sign;
    private LinearLayout ll_night;
    private ImageView img_night;
    private TextView tv_night;
    private LinearLayout ll_end;
    //content
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mData;
    //tab
    private LinearLayout ll_tab_1;
    private LinearLayout ll_tab_2;
    private LinearLayout ll_tab_3;
    private ImageView img_tab_1;
    private ImageView img_tab_2;
    private ImageView img_tab_3;
    private TextView tv_tab_1;
    private TextView tv_tab_2;
    private TextView tv_tab_3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册极光SDK，不知道是不是多余的，因为在application中注册过了
        initSDK();
        System.out.println("SDK错误");

        //初始化界面
        initView();
        System.out.println("view有错");

        //初始化数据
        initData();

        //初始化点击事件监听器
        initListen();




    }

    private void initListen() {
        //监听底部菜单栏是否被点击
        ll_tab_1.setOnClickListener(this);
        ll_tab_2.setOnClickListener(this);
        ll_tab_3.setOnClickListener(this);

        //底部菜单栏fragment切换监听
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //设置底部菜单栏选中样式
                switch (position) {
                    case 0:
                        img_tab_1.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_1));
                        img_tab_2.setImageDrawable(getResources().getDrawable(R.drawable.tab_2_8));
                        img_tab_3.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_8));
                        break;
                    case 1:
                        img_tab_1.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_8));
                        img_tab_2.setImageDrawable(getResources().getDrawable(R.drawable.tab_2_1));
                        img_tab_3.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_8));
                        break;
                    case 2:
                        img_tab_1.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_8));
                        img_tab_2.setImageDrawable(getResources().getDrawable(R.drawable.tab_2_8));
                        img_tab_3.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_1));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //监听底部菜单栏哪个被点击了，如果被点击了设置对应fragment为主显示，也就是点击切换fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_main_tab_1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_main_tab_2:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_main_tab_3:
                mViewPager.setCurrentItem(2);
                break;

        }
    }









    private void initData() {
        //初始化底部导航
        setSupportActionBar(mToolbar);

        //获取成功登录后的用户JMessage的数据信息
        setInfo(JMessageClient.getMyInfo());

        /**
         * 主页面是有抽屉弹窗列表的需要用到ActionBarDrawerToggle
         * 封装本界面的activity、主抽屉界面、界面标题栏、成功1、失败2
         */
        @SuppressLint("ResourceType")
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,mDrawer,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        //封装抽屉界面功能
        mDrawer.setDrawerListener(toggle);
        //对抽屉界面进行状态同步
        toggle.syncState();

        //对弹窗列表的选项进行监听
        mNavigationView.setNavigationItemSelectedListener(this);

        //设置底部菜单栏的首选样式(选中状态)
        img_tab_1.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_1));


        //mData是封装界面fragment的容器，对应fragment对应底部操作栏的页面
        mData = new ArrayList<Fragment>();
        mData.add(new MessageFragment());
        mData.add(new ContactFragment());
        mData.add(new FindFregment());

        //fragment页面适配器,对fragment进行封装
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            //获取对应fragment的位置
            public Fragment getItem(int position) {
                return mData.get(position);
            }

            @Override
            //获取有多少个fragment
            public int getCount() {
                return mData.size();
            }
        };

        //整合封装好的fragment反馈回主页面内容现实中
        mViewPager.setAdapter(mAdapter);


    }

    /**
     * 这个设置用户信息是侧边弹窗中的用户信息数据
     * @param userInfo
     */
    private void setInfo(UserInfo userInfo) {
        //userInfo是极光的bean类
        try {
            //判断用户是否有昵称
            /**
             * 有的话给对方的用户名写上nick，没有的话用用户的用户名进行
             * 侧边弹窗的用户信息显示
             */
            if (!TextUtils.isEmpty(userInfo.getNickname())) {
                tv_username.setText(userInfo.getNickname());
            } else {
                tv_username.setText(userInfo.getUserName());
            }

            //判断用户的个性签名是否有在极光服务器中注册
            if (!TextUtils.isEmpty(userInfo.getSignature())) {
                tv_sign.setText(userInfo.getSignature());
            } else {
                tv_sign.setText("没有个性签名");
            }


            /**
             * 头像获取步骤，暂时不做这个
             */
//            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
//                @Override
//                public void gotResult(int i, String s, Bitmap bitmap) {
//                    if (bitmap != null) {
//                        img_header.setImageBitmap(bitmap);
//                    } else {
//                        img_header.setImageResource(R.drawable.ic_header);
//                    }
//                }
//            });

        }catch(Exception e) {
            System.out.println("获取用户信息时出错："+e);
        }
    }

    private void initView() {
        //获取组件id
        //content 主题内容
        mToolbar = (Toolbar) findViewById(R.id.toolbar);//页面标题栏
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        //tab 底部导航
        ll_tab_1 = (LinearLayout) findViewById(R.id.ll_main_tab_1);
        ll_tab_2 = (LinearLayout) findViewById(R.id.ll_main_tab_2);
        ll_tab_3 = (LinearLayout) findViewById(R.id.ll_main_tab_3);
        img_tab_1 = (ImageView) findViewById(R.id.img_main_tab_1);
        img_tab_2 = (ImageView) findViewById(R.id.img_main_tab_2);
        img_tab_3 = (ImageView) findViewById(R.id.img_main_tab_3);
        tv_tab_1 = (TextView) findViewById(R.id.tv_main_tab_1);
        tv_tab_2 = (TextView) findViewById(R.id.tv_main_tab_2);
        tv_tab_3 = (TextView) findViewById(R.id.tv_main_tab_3);
        //nav 侧边弹窗
        View headerView = mNavigationView.getHeaderView(0);
//        img_header = (CircleImageView) headerView.findViewById(R.id.img_main_head);
        tv_username = (TextView) headerView.findViewById(R.id.tv_main_username);
        tv_sign = (TextView) headerView.findViewById(R.id.tv_main_sign);


    }

    private void initSDK() {
//        JMessageClient.registerEventReceiver(this);
//        JMessageClient.setDebugMode(true);
//        JMessageClient.init(this);
    }





    //抽屉窗口列表点击监听事件,对应的id资源在res中menu下
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){

            //用户退出账号登录
            case R.id.nav_8:
                JMessageClient.logout();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }



        return true;
    }



}