package com.nothinglin.newqitalk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.adapter.UserInfoAdapter;
import com.nothinglin.newqitalk.tools.FriendComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class ContactFragment extends Fragment {


    private SwipeRefreshLayout mSRL_2; //好友列表刷新
    private LinearLayout ll_2;//好友列表标题栏
    private ListView mList_f;//好友列表


    private UserInfoAdapter mAdapter_f;//用户信息适配器

    public static List<UserInfo> mData_f; //装载用户信息的列表容器

    private int s = 0; //控制列表是否展开

    //初始化界面
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact,container,false);
        //注册界面组件
        mSRL_2 = (SwipeRefreshLayout) view.findViewById(R.id.srl_fg2_f);
        mList_f = (ListView) view.findViewById(R.id.lv_fg2_f);
        ll_2 = (LinearLayout) view.findViewById(R.id.ll_fg2_2);

        return view;
    }


    //oncreate方法
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化联系人列表的数据
        initData();

        //初始化事件监听
        initListener();

    }

    private void initListener() {
        //好友列表模块的标题展开关闭监听
        ll_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s==1){
                    showView(0);
                    s=0;
                    return;
                }
                if (s==0){
                    showView(2);
                    if (mData_f.size() == 0){
                        Toast.makeText(getActivity(),
                                "您还没有好友",
                                Toast.LENGTH_SHORT).show();
                    }
                    s=1;
                }
            }
        });

    }

    private void initData() {

        //列表展示
        showView(0);

        //初始化好友列表数据
        mData_f = new ArrayList<>();
        mAdapter_f = new UserInfoAdapter(getActivity(),mData_f);//封装好用户的信息
        mList_f.setAdapter(mAdapter_f);//用户信息列表对适配器适配好的内容进行显示

        //加载好友数据，要有这个才能显示好友数据
        getFriendList();

    }








    private void getFriendList() {
        new Thread(){
            @Override
            public void run() {
                mData_f.clear();
                //下面这些是极光服务器自带的类
                ContactManager.getFriendList(new GetUserInfoListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {

                        if (i == 0){
                            if (list == null) return;

                            Comparator comparator = new FriendComparator();
                            Collections.sort(list,comparator);

                            for (UserInfo userInfo : list){
                                mData_f.add(userInfo);
                            }

                            mAdapter_f.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getActivity(),
                                    "好友加载失败",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });




            }
        }.start();

    }


    //设置群组、好友列表的展开与关闭
    private void showView(int i) {

        switch (i){
            case 0:
//                mSRL_1.setVisibility(View.GONE);//群组列表关闭
                mSRL_2.setVisibility(View.GONE);//好友列表关闭
                break;
            case 1:
//                mSRL_1.setVisibility(View.VISIBLE);//群组列表展开
                mSRL_2.setVisibility(View.GONE);//好友列表关闭
            case 2:
//                mSRL_1.setVisibility(View.GONE);//群组列表关闭
                mSRL_2.setVisibility(View.VISIBLE);//好友列表展开
                break;
        }
    }
}
