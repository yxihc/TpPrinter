package com.example.chatdemoproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.AiHelper;
import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMCallbacks;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMError;
import com.iflytek.sparkchain.core.LLMEvent;
import com.iflytek.sparkchain.core.LLMResult;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AEE";
    private Button startChatBtn;
    private TextView chatText;
    private EditText inputText;
    // 设定flag，在输出未完成时无法进行发送
    private boolean sessionFinished = true;
    private LinearLayout mLlContent;
    private NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         , "android.permission.MANAGE_EXTERNAL_STORAGE"
        setContentView(R.layout.activity_main);
        XXPermissions.with(this).permission("android.permission.READ_PHONE_STATE"
                , "android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.READ_EXTERNAL_STORAGE"
                , "android.permission.INTERNET"
        ).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG, "SDK获取系统权限成功");
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    Log.e(TAG, "onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(MainActivity.this, denied);
                } else {
                    Log.e(TAG, "onDenied:权限获取失败");
                }
            }
        });
        initView();
        initListener();
        initSDK();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unInitSDK();
    }


    private void initSDK() {
        // 初始化SDK，Appid等信息在清单中配置
        SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
        sparkChainConfig.appID("c6c55084")
                .apiKey("b6a327b8a199d36febd961a858a807d7")
                .apiSecret("ZDkzODhkZWFlMWI2NmZhODc3ZDUxNWI4")//应用申请的appid三元组
                .logLevel(0);

        int ret = SparkChain.getInst().init(getApplicationContext(), sparkChainConfig);
        if (ret == 0) {
            Log.d(TAG, "SDK初始化成功：" + ret);
            showToast(MainActivity.this, "SDK初始化成功：" + ret);
        } else {
            Log.d(TAG, "SDK初始化失败：其他错误:" + ret);
            showToast(MainActivity.this, "SDK初始化失败-其他错误：" + ret);
        }
    }


    private void startChat() {
        StringBuffer mStringBuffer = new StringBuffer();
        String usrInputText = inputText.getText().toString();
        Log.d(TAG, "用户输入：" + usrInputText);

        if (usrInputText.length() >= 1)
            chatText.append("\n输入:\n    " + usrInputText + "\n");

        LLMConfig llmConfig = LLMConfig.builder();
        llmConfig.domain("generalv2")
                .url("");


        ChatItemView childRight = new ChatItemView(this);
        childRight.showRight();
        childRight.setRightText(usrInputText);
        mLlContent.addView(childRight);

        ChatItemView childLeft = new ChatItemView(this);
        childLeft.showLeft();
        mLlContent.addView(childLeft);

        LLM llm = new LLM(llmConfig);
        LLMCallbacks llmCallbacks = new LLMCallbacks() {
            @Override
            public void onLLMResult(LLMResult llmResult, Object usrContext) {
                Log.d(TAG, "onLLMResult\n");
                String content = llmResult.getContent();
                Log.e(TAG, "onLLMResult:" + content);
                int status = llmResult.getStatus();
                if (content != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStringBuffer.append(content);
//                            chatText.append(content);

//                            Markwon build = Markwon.builder(MainActivity.this).build();
////                            Spanned spanned = build.toMarkdown(content);
////                            chatText.append(spanned);
//                            build.setMarkdown(chatText, mStringBuffer.toString());
//                            childLeft.setLeftText(mStringBuffer.toString());
                            childLeft.setLeftTextAppend(content);
                            toend();
                        }
                    });
                }
                if (usrContext != null) {
                    String context = (String) usrContext;
                    Log.d(TAG, "context:" + context);
                }
                if (status == 2) {
                    int completionTokens = llmResult.getCompletionTokens();
                    int promptTokens = llmResult.getPromptTokens();//
                    int totalTokens = llmResult.getTotalTokens();
                    Log.e(TAG, "completionTokens:" + completionTokens + "promptTokens:" + promptTokens + "totalTokens:" + totalTokens);
                    sessionFinished = true;
                }
            }

            @Override
            public void onLLMEvent(LLMEvent event, Object usrContext) {
                Log.d(TAG, "onLLMEvent\n");
                Log.w(TAG, "onLLMEvent:" + " " + event.getEventID() + " " + event.getEventMsg());
            }

            @Override
            public void onLLMError(LLMError error, Object usrContext) {
                Log.d(TAG, "onLLMError\n");
                Log.e(TAG, "errCode:" + error.getErrCode() + "errDesc:" + error.getErrMsg());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatText.append("错误:" + " err:" + error.getErrCode() + " errDesc:" + error.getErrMsg() + "\n");
                    }
                });
                if (usrContext != null) {
                    String context = (String) usrContext;
                    Log.d(TAG, "context:" + context);
                }
                sessionFinished = true;
            }
        };
        llm.registerLLMCallbacks(llmCallbacks);
        String myContext = "myContext";

        mPostInfos.add(new PostInfo("user", usrInputText));
        if (!mStringBuffer.toString().isEmpty()) {
            mPostInfos.add(new PostInfo("assistant", mStringBuffer.toString()));
        }

        String s = GsonUtils.toJson(mPostInfos);

        int ret = llm.arun(s, myContext);
        if (ret != 0) {
            Log.e(TAG, "SparkChain failed:\n" + ret);
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputText.setText("");
                chatText.append("输出:\n    ");
            }
        });

        sessionFinished = false;
        return;
    }

    private ArrayList<PostInfo> mPostInfos = new ArrayList<>();

    private void unInitSDK() {
        SparkChain.getInst().unInit();
    }

    private void initListener() {
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionFinished) {
                    startChat();
                    toend();
                } else {
                    Toast.makeText(MainActivity.this, "Busying! Please Wait", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 监听文本框点击时间,跳转到底部
        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toend();
            }
        });
    }

    private void initView() {
        startChatBtn = findViewById(R.id.chat_start_btn);
        chatText = findViewById(R.id.chat_output_text);
        inputText = findViewById(R.id.chat_input_text);
        mLlContent = findViewById(R.id.ll_content);

        mNestedScrollView = findViewById(R.id.nscroll);

        chatText.setMovementMethod(new ScrollingMovementMethod());

        GradientDrawable drawable = new GradientDrawable();
        // 设置圆角弧度为5dp
        drawable.setCornerRadius(dp2px(this, 5f));
        // 设置边框线的粗细为1dp，颜色为黑色【#000000】
        drawable.setStroke((int) dp2px(this, 1f), Color.parseColor("#000000"));
        inputText.setBackground(drawable);


//        Markwon build = Markwon.builder(this).build();
//        String xx= "# sa";
//        Spanned spanned = build.toMarkdown(xx);
//
//        chatText.append(spanned);
//        build.setMarkdown(chatText,xx);

//        ChatItemView child = new ChatItemView(this);
//        child.showLeft();
//        ChatItemView child2 = new ChatItemView(this);
//        child2.showRight();
//        mLlContent.addView(child);
//        mLlContent.addView(child2);
    }

    private float dp2px(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }

    public static void showToast(final Activity context, final String content) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int random = (int) (Math.random() * (1 - 0) + 0);
                Toast.makeText(context, content, random).show();
            }
        });

    }

    public void toend() {
        mNestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                mNestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
//        int scrollAmount = chatText.getLayout().getLineTop(chatText.getLineCount()) - chatText.getHeight();
//        if (scrollAmount > 0) {
//            chatText.scrollTo(0, scrollAmount + 10);
//        }
    }
}