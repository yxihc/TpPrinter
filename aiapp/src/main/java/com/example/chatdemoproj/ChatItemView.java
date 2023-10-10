package com.example.chatdemoproj;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.Markwon;

public class ChatItemView extends FrameLayout {
    private View mRootView;
    private LinearLayout mLlLeft, mLlRight;
    private TextView mTvLeft;
    private TextView mTvRight;

    public ChatItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ChatItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private Context mContext;

    private void init(Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(R.layout.item_chat, this, true);
        mLlLeft = mRootView.findViewById(R.id.ll_left);
        mLlRight = mRootView.findViewById(R.id.ll_right);

        mTvLeft = mRootView.findViewById(R.id.tv_left);
        mTvRight = mRootView.findViewById(R.id.tv_right);

        mTvLeft.setOnClickListener(view -> {
            speed();
        });
    }

    private void speed() {
        TTSManager.getInstance().convertTextToSpeech(mTvLeft.getText().toString().trim());
    }

    public void showLeft() {
        if (mRootView == null) return;
        mLlLeft.setVisibility(VISIBLE);
        mLlRight.setVisibility(GONE);
    }

    public void showRight() {
        if (mRootView == null) return;
        mLlLeft.setVisibility(GONE);
        mLlRight.setVisibility(VISIBLE);
    }

    public void setLeftText(String text) {
        if (mTvLeft == null) return;
        if (mContext == null) return;
        Markwon build = Markwon.builder(mContext).build();
        build.setMarkdown(mTvLeft, text);
    }

    public void setRightText(String text) {
        if (mTvRight == null) return;
        mTvRight.setText(text);
    }

    public void setLeftTextAppend(String content) {
        if (mTvLeft == null) return;
        if (mContext == null) return;
        mTvLeft.append(content);
    }
}

