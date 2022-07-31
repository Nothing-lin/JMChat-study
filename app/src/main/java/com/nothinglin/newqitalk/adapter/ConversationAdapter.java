package com.nothinglin.newqitalk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.tools.Expression;
import com.nothinglin.newqitalk.view.GroupView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * 消息列表适配器
 */
public class ConversationAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//动态视图布局
    private List<Conversation> mData;//消息列表数据组

    public ConversationAdapter(Context context, List<Conversation> data) {
        mInflater = LayoutInflater.from(context);//获取动态布局的上下文
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    //消息列表视图适配器
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //极光包含的对象，不用自己去创建
        Conversation conversation = mData.get(position);//获取对应的聊天窗口
        ViewHolder viewHolder = null;

        if (convertView == null) {
            //如果没有初始化视图组件的话，那么就进行声明

            //将message_item封装进动态视图中去
            convertView = mInflater.inflate(R.layout.fragment_message_item, parent, false);

            //注册message_item组件
            viewHolder = new ViewHolder();
            viewHolder.mImage = (GroupView) convertView
                    .findViewById(R.id.img_chat_list_item);
            viewHolder.mFirst = (TextView) convertView
                    .findViewById(R.id.tv_chat_list_item_0);
            viewHolder.mName = (TextView) convertView
                    .findViewById(R.id.tv_chat_list_item_name);
            viewHolder.mText = (TextView) convertView
                    .findViewById(R.id.tv_chat_list_item_text);
            viewHolder.mCount = (TextView) convertView
                    .findViewById(R.id.tv_chat_list_item_count);

            //封装组件
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取最新的消息
        Message latesMessage = conversation.getLatestMessage();

        //获取未阅读信息
        int unReadMsgCnt = conversation.getUnReadMsgCnt();

        //初始化消息列表的一些常用变量
        String text = "", cout = "", name = "", lastname = "";

        //如果有最新消息的话就执行
        if (latesMessage != null) {
            MessageContent content = latesMessage.getContent();//获取最新消息的内容

            //判断获取到的内容是图片还是文字或是通知类型
            switch (content.getContentType()) {
                case text:
                    TextContent stringExtra = (TextContent) content;
                    text = stringExtra.getText();//封装获取到的最新消息内容到text变量中
                    break;
                case image:
                    text = "[图片]";
                    break;
                case eventNotification:
                    EventNotificationContent eventNotificationContent = (EventNotificationContent) content;
                    text = eventNotificationContent.getEventText();
                    break;
            }
        }

        //如果存在未读信息的话
        if (unReadMsgCnt > 0) {
            cout = "" + unReadMsgCnt;
            viewHolder.mCount.setText(cout);//设置有多少条未读信息
            viewHolder.mCount.setBackgroundResource(R.drawable.hip);//未读消息的背景圆圈
        } else {
            viewHolder.mCount.setBackgroundResource(R.color.colorClarity);//设置null背景
        }

        //如果存在最新消息的话
        if (latesMessage != null) {
            if (latesMessage.getFromUser().getUserName() == JMessageClient.getMyInfo().getUserName()) {
                //如果发信息给我的是我自己
                lastname = "";
            } else if (!TextUtils.isEmpty(latesMessage.getFromUser().getNotename())) {
                //给我发信息的有没有notename，如果有的话就显示他的notename
                lastname = latesMessage.getFromUser().getNotename();
            } else if (!TextUtils.isEmpty(latesMessage.getFromUser().getNickname())) {
                //判断发信息给我的有没有nickname
                lastname = latesMessage.getFromUser().getNickname();
            } else {
                //如果上面的都没有就使用username来显示对方名称
                lastname = lastname = latesMessage.getFromUser().getUserName();
            }
        }

        //设置显示发给我信息的用户名称
        viewHolder.mText.setText("lastname:");

        //获取mtext信息长度， 东榆：回来吃饭了
        int size = (int) viewHolder.mText.getTextSize();

        //设置表情，text-->emoji  长度和宽度为:5x5
        SpannableString ss = Expression.getSpannableString(mInflater.getContext(), text, size + 5, size + 5);
        viewHolder.mText.append(ss);


        //获取默认头像资源
        final Bitmap mbitmap = BitmapFactory.decodeResource(mInflater.getContext().getResources(), R.drawable.ic_header);

        //判断对话是单聊还是群聊
        switch (conversation.getType()) {

            //如果聊天类型为单聊的话
            case single:
                UserInfo info = (UserInfo) conversation.getTargetInfo();//获取对话的用户信息
                //获取对方用户的用户名
                if (!TextUtils.isEmpty(info.getNotename())) {
                    name = info.getNotename();
                } else if (!TextUtils.isEmpty(info.getNickname())) {
                    name = info.getNickname();
                } else {
                    name = info.getUserName();
                }

                //显示聊天对象的用户名
                viewHolder.mName.setText(name);

                //设置聊天对象的头像
                viewHolder.mImage.setTag(position);

                final List<Bitmap> bitmapList = new ArrayList<Bitmap>();

                final ConversationAdapter.ViewHolder finalViewHolder = viewHolder;

                info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {

                        //如果远程服务器中有头像了，那么就调用，如果没有就调用本地默认的头像
                        if (bitmap != null) {
                            bitmapList.add(bitmap);
                        } else {
                            bitmapList.add(mbitmap);
                        }

                        //设置message_item的头像
                        finalViewHolder.mImage.setImageBitmaps(bitmapList);
                    }
                });

                break;

            //如果聊天类型为群组的话
            case group:
                //JMessage的类对象
                GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
                //设置群名称
                viewHolder.mName.setText(groupInfo.getGroupName());
                //设置群头像，找到对应的操作对象
                viewHolder.mImage.setTag(position);

                final List<Bitmap> bitmapList1 = new ArrayList<Bitmap>();
                final ConversationAdapter.ViewHolder finalViewHolder1 = viewHolder;

                //获取群成员的数量
                final int m = groupInfo.getGroupMembers().size();

                for (int i = 0;i < m && i < 5;i++){
                    //初始化群头像列表，最多五个
                    bitmapList1.add(null);
                }

                //设置群头像
                for (int i = 0;i < m && i < 5;i++){

                    final int p = i;
                    groupInfo.getGroupMembers().get(p).getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int i, String s, Bitmap bitmap) {
                            //如果存在远程服务器中设置过的头像直接调用，如果没有就使用本地的默认头像
                            if (bitmap != null) {
                                bitmapList1.set(p, bitmap);
                            } else {
                                bitmapList1.set(p, mbitmap);
                            }

                            //设置头像
                            finalViewHolder1.mImage.setImageBitmaps(bitmapList1);
                        }
                    });
                }
                break;
        }

        return convertView;
    }


    //声明message_item的组件
    private class ViewHolder {
        GroupView mImage;
        TextView mFirst;
        TextView mName;
        TextView mText;
        TextView mCount;
    }
}
