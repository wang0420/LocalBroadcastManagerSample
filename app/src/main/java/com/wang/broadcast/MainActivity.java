package com.wang.broadcast;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wang.broadcast.annotation.Action;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_TEST = "action_test";

    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastUtil.register(this);

        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.button);
        mTextView = findViewById(R.id.text);
        mViewPager = findViewById(R.id.view_pager);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("text", "广播发送的内容");
                BroadcastUtil.sendBroadcast(MainActivity.this, bundle, ACTION_TEST);
            }
        });


        // 同类不同对象测试
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            int[] colors = new int[]{Color.RED, Color.BLUE, Color.GREEN};

            @Override
            public Fragment getItem(int position) {
                return BlankFragment.newInstance(colors[position]);
            }

            @Override
            public int getCount() {
                return colors.length;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtil.unregister(this);
    }


    @Action("main_activity")
    public void mainaa() {
        // 参数测试

    }


}
