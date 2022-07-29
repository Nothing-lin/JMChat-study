package com.nothinglin.newqitalk.tools;

import java.util.Comparator;

import cn.jpush.im.android.api.model.UserInfo;

/**
 * 朋友比较器
 */
public class FriendComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        UserInfo a = (UserInfo) o1;
        UserInfo b = (UserInfo) o2;
        return (int) (b.getUserID() - a.getUserID());
    }
}
