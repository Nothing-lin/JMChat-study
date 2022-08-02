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
import cn.jpush.im.android.api.event.MessageEvent;
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
    //事件处理
    private Handler handler = new Handler();
    private MyRunnable myRunnable = new MyRunnable();


    //initview
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        //初始化消息列表的组件
        mSRL = (SwipeRefreshLayout) view.findViewById(R.id.srl_fg1);
        mList = (ListView) view.findViewById(R.id.lv_fg1);
        mTextView = (TextView) view.findViewById(R.id.tv_fg1);

        //注册消息菜单的上下文，通知SDK消息内容出现更新
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
                for (Conversation conversation : mConversationList) {
                    mData.add(conversation);
                }

                //通知消息更新
                mAdapter.notifyDataSetChanged();
                //列表消息状态提示
                if (mData.size() > 0) {
                    mTextView.setText("");
                } else {
                    mTextView.setText("没有对话列表消息");
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //提示刷新成功
                        Toast.makeText(getActivity(), "消息列表数据刷新成功", Toast.LENGTH_SHORT).show();
                        //关闭刷新状态
                        mSRL.setRefreshing(false);
                    }
                }, 500);

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
                switch (mData.get(position).getType()) {
                    case single:
                        //获取目标用户信息
                        UserInfo userInfo = (UserInfo) mData.get(position).getTargetInfo();

                        i.putExtra("username", userInfo.getUserName());
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

        mAdapter = new ConversationAdapter(getActivity(), mData);

        mList.setAdapter(mAdapter);//封装群信息列表到视图中

    }


    //全局监听器，如果没有这个方法，那么JMessageClient.registerEventReceiver(this)会报错，因为无法监听收到的消息更新
    public void onEvent(MessageEvent event) {
        handler.post(myRunnable);
    }



    //以下都是activity启动后，自动初始化消息列表中的数据

    //动态调用极光服务器，联网需要线程，多个线程共享资源
    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            mData.clear();//清空原有的消息列表数据
            mConversationList = JMessageClient.getConversationList();//重新获取对话列表

            //遍历每个对话列表的item到mData中
            for (Conversation conversation : mConversationList){
                mData.add(conversation);
            }

            mAdapter.notifyDataSetChanged();//通知全局数据发生改变

            if (mData.size() > 0) {
                mTextView.setText("");
            } else {
                mTextView.setText(R.string.no_chat);
            }
        }
    }


    //程序正常启动时：onCreate()->onStart()->onResume();
    // App用到一半 有事Home键切出去了 在回来时调onResume
    //应该是监听消息内容的变化
    @Override
    public void onResume() {
        super.onResume();
        //当sdk收到某些后台下发的数据，或者发生了某些需要上层关注的事件时，sdk会上抛事件对象通知给上层，例如，在线消息事件、会话刷新事件、用户下线事件等。
        JMessageClient.registerEventReceiver(this);
        System.out.println("11");
    }

    @Override
    public void onStart() {
        super.onStart();
        //界面启动后自动加载和初始化消息列表中的数据
        handler.post(myRunnable);
    }


    //比如在使用音乐软件时，中途来了电话切换出去，这时音乐软件调用onpause方法,但重新回来时会调用onresume方法
    @Override
    public void onPause() {
        super.onPause();
        //既然切换出去了，就不应该还在调用消息接收的资源，节省不必要的资源浪费，回来时再重新启动
        JMessageClient.unRegisterEventReceiver(this);
    }

    //长按交互功能
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //长按消息列表item后显示：已阅读、删除聊天
        menu.add(1,0,1,getResources().getString(R.string.change_isRead));
        menu.add(1,1,1,getResources().getString(R.string.delete_message));
    }

    //对于长按显示出来的菜单对每一项进行点击事件处理，呼应上面的
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //获取焦点选项，就是监听我们点击了哪一项
       AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //获取我们单击了的选项的位置
       int p = menuInfo.position;

       switch (item.getItemId()){
           //上面设置了itemId == 0是将消息转为已读
           case 0:
               int un = mData.get(p).getUnReadMsgCnt();//获取未读消息数量

               //如果存在未读消息
               if (un>0){
                   //如果存在未读消息，将未读消息数量清空，设为已读
                   mData.get(p).resetUnreadCount();
                   //通知数据变化
                   mAdapter.notifyDataSetChanged();
               }else {
                    //如果没有未读消息还设为已读，那么就提示1条消息未读
                   mData.get(p).setUnReadMessageCnt(1);
                   mAdapter.notifyDataSetChanged();
               }
               break;

           case 1:
               //删除选中的对话框中全部内容
               mConversationList.get(p).deleteAllMessage();
               //判断要删除的对话框是单聊还是群聊
               switch (mConversationList.get(p).getType()){
                   case single:
                       //获取聊天的目标对象且删除和其的对话消息
                       UserInfo userInfo = (UserInfo) mConversationList.get(p).getTargetInfo();
                       JMessageClient.deleteSingleConversation(userInfo.getUserName());
                       break;
                   case group:
                       GroupInfo groupInfo =(GroupInfo) mConversationList.get(p).getTargetInfo();
                       JMessageClient.deleteGroupConversation(groupInfo.getGroupID());
                       break;
               }

               //删除消息列表中的目标对象
               mData.remove(p);
               mAdapter.notifyDataSetChanged();
               //如果没有对话框了的话就设置提示还没有聊天
               if (mData.size() == 0){
                   mTextView.setText(R.string.no_chat);
               }
       }

        return super.onContextItemSelected(item);
    }
}
