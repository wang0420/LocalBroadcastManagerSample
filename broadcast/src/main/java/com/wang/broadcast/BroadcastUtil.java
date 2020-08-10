package com.wang.broadcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author wnagwei
 * @date 2019/5/18
 */
public class BroadcastUtil {

    private static final String SUFFIX = "$$BroadcastInject";

    private static SparseArray<BroadcastInject> proxyArray = new SparseArray<>();

    public static void register(Fragment fragment) {
        register(fragment.getActivity(), fragment);
    }

    public static void register(Activity activity) {
        register(activity, activity);
    }

    public static void register(Context context, Object host) {
        try {
            Class proxy = getProxyClass(host);
            if (proxy == null) {
                return;
            }
            BroadcastInject inject = findProxy(proxy);
            if (inject == null) {
                inject = (BroadcastInject) proxy.newInstance();
            }
            inject.register(context, host);
            proxyArray.append(host.hashCode(), inject);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void unregister(Object host) {
        Class proxy = getProxyClass(host);
        if (proxy == null) {
            return;
        }
        BroadcastInject inject = findProxy(host);
        if (inject == null) {
            return;
        }
        inject.unregister(host);
        proxyArray.remove(host.hashCode());
    }

    public static void sendBroadcast(Context context, String... actions) {
        sendBroadcast(context, null, actions);
    }

    public static void sendBroadcast(Context context, Bundle bundle, String... actions) {
        for (String action : actions) {
            Intent intent = new Intent(action);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static Class getProxyClass(Object host) {
        try {
            return Class.forName(host.getClass().getName() + SUFFIX);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BroadcastInject findProxy(Object host) {
        if (proxyArray!= null && proxyArray.size() != 0 && host != null) {
            return proxyArray.get(host.hashCode());
        }
        return null;
    }

    private static BroadcastInject findProxy(Class proxy) {
        for (int i = 0; i < proxyArray.size(); i++) {
            if (proxyArray.valueAt(i).getClass().getCanonicalName().equals(proxy.getCanonicalName())) {
                return proxyArray.valueAt(i);
            }
        }
        return null;
    }
}
