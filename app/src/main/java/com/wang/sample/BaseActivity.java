package com.wang.sample;

import android.os.Bundle;
import android.util.Log;

import com.wang.broadcast.BroadcastUtil;
import com.wang.broadcast.annotation.Action;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author wangwei
 * @date 2021/4/6.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("TAG", "-2-->" + this);
        BroadcastUtil.register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtil.unregister(this);
    }


    @Action("base_activity")
    public void basemethos() {
        // 参数测试
        Log.w("TAG", "-----basemethos-----");

    }


}
