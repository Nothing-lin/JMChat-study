package com.nothinglin.newqitalk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nothinglin.newqitalk.R;
import com.nothinglin.newqitalk.view.GroupView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.GroupInfo;

/**
 * 群组信息适配器
 */
public class GroupInfoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//动态视图布局
    private List<GroupInfo> mData;//群信息数据组

    public GroupInfoAdapter(Context context, List<GroupInfo> Data) {
        mInflater = LayoutInflater.from(context);//获取当前的activity的上下文
        this.mData = Data;
    }



    @Override
    public int getCount() {
        return mData.size();//返回群数量
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);//获取对应position上的群信息
    }

    @Override
    public long getItemId(int position) {
        return position;//获取群的position
    }

    //定义群视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GroupInfo groupInfo = mData.get(position);//对应极光的群信息对象，不是自己创建的

        ViewHolder viewHolder = null;

        //如果不存在视图的话，那么就注册group item中的组件
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.fragment_group_item,parent,false);//封装fragment_group_item到群组列表中

            //注册group_item组件
            viewHolder = new GroupInfoAdapter.ViewHolder();
            viewHolder.mImageView = convertView.findViewById(R.id.img_group_list_item);
            viewHolder.mTextView = convertView.findViewById(R.id.tv_group_list_item);
            viewHolder.mImageView1 = convertView.findViewById(R.id.img_group_list_item_1);
            viewHolder.mImageView2 = convertView.findViewById(R.id.img_group_list_item_2);

            //封装注册的group_item组件
            convertView.setTag(viewHolder);
        }else {
            //已经存在视图的话直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置群名称
        viewHolder.mTextView.setText(groupInfo.getGroupName());

        //群组头像处理
        viewHolder.mImageView.setTag(position);//处理特定的群组信息
        final  List<Bitmap> bitmapList = new ArrayList<>();

        final Bitmap mBitmap = BitmapFactory.decodeResource(mInflater.getContext().getResources(),R.drawable.ic_header);

        final int m = groupInfo.getGroupMembers().size();//获取 群成员数量

        if (m==0){
            bitmapList.add(mBitmap); //如果没有群成员，那么设置特定的群头像
            viewHolder.mImageView.setImageBitmaps(bitmapList);
        }else {
            //如果不止一个群成员，那么群头像为多个群成员的头像
            final ViewHolder finalViewHolder = viewHolder;

            //初始化头像列表，容量为5个，均为null
            for (int i = 0; i < m && i < 5; i++) {
                bitmapList.add(null);
            }

            //处理群头像,bitmap是图像存储的一种结构，理解为图片链接
            for (int i = 0; i < m && i < 5; i++) {
                final int p = i;
                groupInfo.getGroupMembers().get(p).getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {

                        if (bitmap != null){
                            //如果远程服务器中存在了群头像直接调用
                            bitmapList.set(p,bitmap);
                        }else {
                            //如果没有的话就是用自定义的默认群头像
                            bitmapList.set(p,mBitmap);
                        }

                        //确定对应群头像不能为空且图片位置要对应
                        if (finalViewHolder.mImageView.getTag() != null && finalViewHolder.mImageView.getTag().equals(position)) {
                            finalViewHolder.mImageView.setImageBitmaps(bitmapList);
                        }
                    }
                });
            }

        }

        return convertView;
    }

    private class ViewHolder {
        GroupView mImageView; //groupview是群组视图显示的一个自定义组件
        TextView mTextView; //群组的显示名称
        ImageView mImageView1;//群组的免打扰图标
        ImageView mImageView2;//群组的黑名单图标
    }
}
