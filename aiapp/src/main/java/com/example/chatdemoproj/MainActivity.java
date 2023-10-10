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
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AEE";
    private Button startChatBtn;
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
        TTSManager.getInstance().init(this);
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

        SpeechUtility.createUtility(this, "appid=c6c55084");
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        Setting.setShowLog(true);

        initView();
        initListener();
        initSDK();

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);
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
            }
        });
        sessionFinished = false;
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
        inputText = findViewById(R.id.chat_input_text);
        mLlContent = findViewById(R.id.ll_content);

        findViewById(R.id.chat_start_btn_voice).setOnClickListener(v -> {
            startVoice();
        });

        mNestedScrollView = findViewById(R.id.nscroll);

        GradientDrawable drawable = new GradientDrawable();
        // 设置圆角弧度为5dp
        drawable.setCornerRadius(dp2px(this, 5f));
        // 设置边框线的粗细为1dp，颜色为黑色【#000000】
        drawable.setStroke((int) dp2px(this, 1f), Color.parseColor("#000000"));
        inputText.setBackground(drawable);
    }

    private StringBuffer buffer = new StringBuffer();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private String language = "zh_cn";

    private void startVoice() {
        buffer.setLength(0);
        inputText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        // 显示听写对话框
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        // 返回结果
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        // 识别回调错误
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }
    };

    /**
     * 显示结果
     */
    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        inputText.setText(resultBuffer.toString());
        inputText.setSelection(inputText.length());

        if (isLast)
            startChat();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {

        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav.
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                getExternalFilesDir("msc").getAbsolutePath() + "/iat.wav");
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    private void showTip(final String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
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
    }
}