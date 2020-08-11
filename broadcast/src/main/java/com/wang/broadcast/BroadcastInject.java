package com.wang.broadcast;

import android.content.Context;

/**
 * 1.0.0
 *
 * @author wnagwei
 * @date 2019/5/18
 */
public interface BroadcastInject<T> {

    void register(Context context, T t);

    void unregister(T t);
}
