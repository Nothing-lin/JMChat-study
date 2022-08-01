package com.nothinglin.newqitalk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.activity.SingleChatActivity;
import com.nothinglin.newqitalk.adapter.ConversationAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class MessageFragment extends Fragment {

    //声明组件变量
    private SwipeRefreshLayout mSRL;//消息列表刷新
    private ListView mList;//消息列表容器
    private TextView mTextView;

    private List<Conversation> mData, mConversationList;
    private ConversationAdapter mAdapter;
    private Handler handler = new Handler();
//    private MyRunnable myRunnable = new MyRunnable();


    //initview
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message,container,false);

        //初始化消息列表的组件
        mSRL = (SwipeRefreshLayout) view.findViewById(R.id.srl_fg1);
        mList = (ListView) view.findViewById(R.id.lv_fg1);
        mTextView = (TextView) view.findViewById(R.id.tv_fg1);

        //注册消息菜单的上下文，这样才能体现出列表的功能吧？
        this.registerForContextMenu(mList);

        return view;
    }

    //oncreate方法
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化数据
        initData();

        //初始化点击监听器
        initListener();
    }

    private void initListener() {

        //监听消息刷新列表中的数据
        mSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSRL.setRefreshing(true);

                mData.clear();
                //获取远程服务器中的对话列表
                //ConversationList是对话列表，Conversation是对话列表item
                mConversationList = JMessageClient.getConversationList();
                for (Conversation conversation : mConversationList){
                    mData.add(conversation);
                }

                //通知消息更新
                mAdapter.notifyDataSetChanged();
                //列表消息状态提示
                if (mData.size()>0){
                    mTextView.setText("");
                }else {
                    mTextView.setText("没有对话列表消息");
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //提示刷新成功
                        Toast.makeText(getActivity(),"消息列表数据刷新成功",Toast.LENGTH_SHORT).show();
                        //关闭刷新状态
                        mSRL.setRefreshing(false);
                    }
                },500);

            }
        });


        //消息列表item点击事件
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mData.get(position).resetUnreadCount();//点击时候设置该条信息为已读

                //动态刷新消息列表的内容
                mAdapter.notifyDataSetChanged();

                Intent i = new Intent();
                //传递数据到SingleChatActivity.class页面
                switch (mData.get(position).getType()){
                    case single:
                        //获取目标用户信息
                        UserInfo userInfo = (UserInfo) mData.get(position).getTargetInfo();

                        i.putExtra("username",userInfo.getUserName());
                        if (!TextUtils.isEmpty(userInfo.getNotename())) {
                            i.putExtra("name", userInfo.getNotename());
                        } else if (!TextUtils.isEmpty(userInfo.getNickname())) {
                            i.putExtra("name", userInfo.getNickname());
                        } else {
                            i.putExtra("name", userInfo.getUserName());
                        }

                        i.setClass(getActivity(), SingleChatActivity.class);
                        startActivity(i);
                        System.out.println("kk");
                        break;
                }

            }
        });

    }

    private void initData() {
        //初始化用户数据容器
        mData = new ArrayList<>();
        //初始化消息列表容器
        mConversationList = new ArrayList<>();

        mAdapter = new ConversationAdapter(getActivity(),mData);

        mList.setAdapter(mAdapter);//封装群信息列表到视图中

    }



}
