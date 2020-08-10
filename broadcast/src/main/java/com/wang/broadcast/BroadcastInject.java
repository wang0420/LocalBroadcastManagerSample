package com.wang.broadcast;

import android.content.Context;

/**
 * @author wnagwei
 * @date 2019/5/18
 */
public interface BroadcastInject<T> {

    void register(Context context, T t);

    void unregister(T t);
}
