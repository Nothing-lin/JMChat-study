package com.nothinglin.newqitalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

public class SingleChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;//聊天界面标题栏
    private ListView mList;//聊天界面中的item
    private MessageAdapter mAdapter;//聊天界面消息适配器
    private List<Message> mData;//消息容器
    private EditText mEt_input;//聊天界面中的输入框
    private Button mBt_send;//聊天界面中的消息发送按钮
    private String username;

    //数据传递监听器
    private MessageAdapter.MyClickListener mListener = new MessageAdapter.MyClickListener(){
        @Override
        public void myOnClick(int p, View w) {
            String u = mData.get(p).getFromUser().getUserName();//获取对应位置上的用户名
            Intent i = new Intent();

            i.putExtra("u",u);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册极光的接收事务
//        JMessageClient.registerEventReceiver(this);

        //注册视图
        intiView();

        //注册数据
        initData();

    }

    private void initData() {
        setSupportActionBar(mToolbar);
        //标题栏的title
        getSupportActionBar().setTitle("");

        //数据传递
        Intent i = getIntent();
        username = i.getStringExtra("username");//用户名

        String n = i.getStringExtra("name");
        mToolbar.setTitle(n);//显示对话用户名称

        mData = new ArrayList<>();
        mAdapter = new MessageAdapter(this,mData,mListener);//自动监听信息变化mlistener
        mList.setAdapter(mAdapter);//封装适配的消息布局到内容列表上

        //获取历史聊天记录
        getHistory();

        //获取对应的聊天对话内容
        JMessageClient.enterSingleConversation(username);



    }

    private void getHistory() {
        Conversation conversation = JMessageClient.getSingleConversation(username,null);

        if (conversation == null){
            return;//如果没有聊天消息直接返回
        }

        //获取过去近50条记录信息
        List<Message> messageList = conversation.getMessagesFromOldest(conversation.getLatestMessage().getId()-50,50);

        for (Message message : messageList){
            mData.add(message);
            mAdapter.notifyDataSetChanged();//通知适配器信息内容发生了改变、动态刷新
            mList.setSelection(mAdapter.getCount());
        }

    }


    private void intiView() {
        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        mList = (ListView) findViewById(R.id.lv_chat);
        mEt_input = (EditText) findViewById(R.id.et_chat_input);
        mBt_send = (Button) findViewById(R.id.bt_chat_send);
    }
}
