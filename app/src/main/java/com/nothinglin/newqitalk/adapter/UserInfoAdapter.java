package com.nothinglin.newqitalk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nothinglin.newqitalk.R;

import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class UserInfoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//动态界面组件布局
    private List<UserInfo> mData; //用户信息列表

    //用户信息构造器
    public UserInfoAdapter(Context context,List<UserInfo> Data){
        mInflater = LayoutInflater.from(context);//mInflater获取上下文内容中的一些内容，不太清楚
        this.mData = Data;//装载传入的用户信息列表
    }



    //获取好友的个数
    @Override
    public int getCount() {
        return mData.size();
    }

    //获取特定位置的好友
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    //获取特定位置的好友位置
    @Override
    public long getItemId(int position) {
        return 0;
    }

    //好友列表中每个item的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserInfo userInfo = mData.get(position);//获取特定的好友信息

        //ViewHolder通常出现在适配器里，为的是listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
        ViewHolder viewHolder = null;

        //如果查到好友列表中有信息的话，也就是有好友的话，就要把好友显示出来
        if (convertView ==null){
            convertView = mInflater.inflate(R.layout.fragment_friend_item,parent,false);//将好友列表item封装到parent整体列表中

            viewHolder =new ViewHolder();
            //注册好友列表item上的组件
            viewHolder.mImageView = convertView.findViewById(R.id.img_user_list_item);
            viewHolder.mTextView = convertView.findViewById(R.id.tv_user_list_item);
            viewHolder.mImageView1 = convertView.findViewById(R.id.img_user_list_item_1);
            viewHolder.mImageView2 = convertView.findViewById(R.id.img_user_list_item_2);

        }else {
            //Adapter 有个getView方法，可以使用setTag把查找的view缓存起来方便多次重用
            viewHolder = (ViewHolder) convertView.getTag();
        }


        String name = "";
        if (!TextUtils.isEmpty(userInfo.getNotename())){
            name = userInfo.getNotename(); //如果存在笔名，那么name为笔名
        }else if (!TextUtils.isEmpty(userInfo.getNotename())){
            name = userInfo.getNickname(); //如果昵称存在name为昵称
        }else {
            name = userInfo.getUserName();//都没有就用用户名
        }

        //设置特定的好友列表显示中的用户名称
        viewHolder.mTextView.setText(name);

        //设置特定的好友列表显示中的用户头像
        viewHolder.mImageView.setTag(position);//找到对应的用户给他设置头像


        final ViewHolder finalViewHolder = viewHolder;
        //从JMessage上获取头像，开线程
        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {

                //如果用户头像存在且等于它的位置的话
                if (finalViewHolder.mImageView.getTag() !=null && finalViewHolder.mImageView.getTag().equals(position)){

                    if (bitmap != null){
                        //存在远程存储的头像的话
                        finalViewHolder.mImageView.setImageBitmap(bitmap);
                    }else {
                        //不存在的话就在本地去找
                        finalViewHolder.mImageView.setImageResource(R.drawable.ic_header);
                    }
                }
            }
        });

        return convertView;
    }


    //定义一个视图持有者的内部类
    private final class ViewHolder {

        //这个是好友列表item布局文件上的id组件
        ImageView mImageView;
        TextView mTextView;
        ImageView mImageView1;
        ImageView mImageView2;
    }
}
