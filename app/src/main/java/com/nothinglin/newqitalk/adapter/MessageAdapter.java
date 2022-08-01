package com.nothinglin.newqitalk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.security.keystore.StrongBoxUnavailableException;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.tools.Expression;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class MessageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//动态界面布局
    private List<Message> mData;//消息容器

    private MyClickListener mListener;//自定义事件监听器

    public MessageAdapter(Context context, List<Message> data, MyClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.mData = data;
        mListener = listener;
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

    //获取item的视图类型
    @Override
    public int getItemViewType(int position) {
        //message获取对应的目标消息
        Message message = mData.get(position);

        //判断对于信息我是发送端还是接收端，接收端返回0，发送端返回1
        if (message.getDirect() == MessageDirect.receive) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //message获取对应的目标消息
        final Message message = mData.get(position);

        ViewHolder viewHolder = null;
        //如果不存在视图那么对其进行注册
        if (convertView == null) {
            //如果我是接收端，接收到别人的信息的话使用he视图显示对方的消息
            if (getItemViewType(position) == 0) {
                convertView = mInflater.inflate(R.layout.activity_chat_item_he, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.activity_chat_item_me, parent, false);
            }

            //注册chat_item中的组件，获取其id
            viewHolder = new ViewHolder();
            viewHolder.mDate = (TextView) convertView
                    .findViewById(R.id.tv_chat_item_date);
            viewHolder.mName = (TextView) convertView
                    .findViewById(R.id.tv_chat_item_user);
            viewHolder.mHeader = (ImageView) convertView
                    .findViewById(R.id.img_chat_item_user);
            viewHolder.mText = (TextView) convertView
                    .findViewById(R.id.tv_chat_item_text);
            viewHolder.mImage = (ImageView) convertView
                    .findViewById(R.id.img_chat_item_img);
            viewHolder.mImage2 = (ImageView) convertView
                    .findViewById(R.id.img_chat_item_img2);

            //将上面的组件内容封装到tag中
            convertView.setTag(viewHolder);

            viewHolder.mFL = (FrameLayout) convertView
                    .findViewById(R.id.fl_chat_item);
        } else {
            //如果已经存在了视图，直接调用
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final ViewHolder finalViewHolder = viewHolder;

        //设置消息的事件
        //设置时间的格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //获取消息的发送事件并设置到视图中
        viewHolder.mDate.setText(dateFormat.format(new Date(message.getCreateTime())));

        //用户信息
        UserInfo userInfo = message.getFromUser();
        //用户信息的名称
        if (!TextUtils.isEmpty(userInfo.getNotename())) {
            viewHolder.mName.setText(userInfo.getNotename());
        } else if (!TextUtils.isEmpty(userInfo.getNickname())) {
            viewHolder.mName.setText(userInfo.getNickname());
        } else {
            viewHolder.mName.setText(userInfo.getUserName());
        }

        //用户头像
        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (bitmap != null) {
                    //如果服务器中已经存在了头像,直接设置到视图中
                    finalViewHolder.mHeader.setImageBitmap(bitmap);
                } else {
                    //否则就是用本地的默认头像
                    finalViewHolder.mHeader.setImageResource(R.drawable.ic_header);
                }
            }
        });

        //设置消息文字背景状态
        switch (message.getStatus()) {
            //成功发送消息，接收端也成功接收的话设置背景颜色为默认
            case send_success:
            case receive_success:
                viewHolder.mFL.setBackgroundResource(R.drawable.chat_text_bg_default);
                break;

            case send_going:
            case receive_going:
                viewHolder.mFL.setBackgroundResource(R.drawable.chat_text_bg_processing);
                break;

            case send_fail:
            case receive_fail:
                viewHolder.mFL.setBackgroundResource(R.drawable.chat_text_bg_fail);
                break;
        }


        //消息内容
        MessageContent content = message.getContent();
        //判断消息类型
        switch (message.getContentType()) {
            case text:
                viewHolder.mText.setVisibility(View.VISIBLE);
                viewHolder.mImage.setVisibility(View.GONE);
                TextContent textContent = (TextContent) content;

                //设置文本内容，使用Expression.getSpannableString过滤其中的表情符号，显示表情
                String text = textContent.getText();
                SpannableString ss = Expression.getSpannableString(mInflater.getContext(), text);

                viewHolder.mText.setText(ss);
                break;

            case image:
                viewHolder.mText.setVisibility(View.GONE);
                viewHolder.mImage.setVisibility(View.VISIBLE);

                final ImageContent imageContent = (ImageContent) content;

                //图片缓存加载处理
                //设置图片，刚发的时候收到的图片不是很清楚，没下载，点击过程会进行加载，加载过程使用加载图片，mimage是没下载的图片显示，mimage2是下载了的图片显示
                Glide.with(mInflater.getContext()).load(imageContent.getLocalThumbnailPath()).centerCrop().placeholder(com.yancy.gallerypick.R.mipmap.gallery_pick_photo).into(viewHolder.mImage);

                //mimage2是下载了的图片显示
                viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageContent.downloadOriginImage(message, new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int i, String s, File file) {
                                Glide.with(mInflater.getContext()).load(file).centerCrop().placeholder(com.yancy.gallerypick.R.mipmap.gallery_pick_photo).into(finalViewHolder.mImage2);

                                finalViewHolder.mImage2.setVisibility(View.VISIBLE);
                                finalViewHolder.mImage.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            case eventNotification:
                viewHolder.mText.setVisibility(View.VISIBLE);
                viewHolder.mImage.setVisibility(View.GONE);
                EventNotificationContent eventNotificationContent = (EventNotificationContent) content;
                String eventText = eventNotificationContent.getEventText();
                viewHolder.mText.setText(eventText);
                break;
        }

        viewHolder.mHeader.setOnClickListener(mListener);
        viewHolder.mHeader.setTag(position);


        return convertView;
    }


    //组件监听器
    public abstract static class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }

        public abstract void myOnClick(int p, View w);
    }

    private class ViewHolder {

        //chat_item中的视图
        TextView mDate;
        ImageView mHeader;
        TextView mName;
        TextView mText;
        ImageView mImage;
        ImageView mImage2;
        FrameLayout mFL;
    }
}
