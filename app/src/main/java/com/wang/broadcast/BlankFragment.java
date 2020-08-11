package com.wang.broadcast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wang.broadcast.annotation.Action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BlankFragment extends Fragment {

    private static final String ARG_PARAM1 = "color";
    private int mColor;

    private TextView mTextView;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(int color) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColor = getArguments().getInt(ARG_PARAM1);
        }

        BroadcastUtil.register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(mColor);

        mTextView = view.findViewById(R.id.text_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtil.unregister(this);
    }

    @Action(MainActivity.ACTION_TEST)
    public void setText(Bundle bundle) {
        String str = bundle.getString("text");
        mTextView.append(str + " - " + this.toString() + " - " + str + "\n");
    }

    @Action("HAHAHHHA")
    public void HAHAHHHA(Bundle bundle) {
        String str = bundle.getString("text");
        mTextView.append(str + " - " + this.toString() + " - " + str + "\n");
    }
    @Action("test111")
    public void test11() {
        // 参数测试

    }
    @Action("test11111")
    public void test1111() {
        // 参数测试

    }
    @Action("test1")
    public void test1() {
        // 参数测试

    }
}
