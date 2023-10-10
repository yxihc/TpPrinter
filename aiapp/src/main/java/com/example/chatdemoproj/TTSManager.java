package com.example.chatdemoproj;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.blankj.utilcode.util.LogUtils;

import java.util.Locale;

public class TTSManager {

    private static class SingletonHolder {
        private static TTSManager instance = new TTSManager();
    }

    public static TTSManager getInstance() {
        return SingletonHolder.instance;
    }

    private TTSManager() {
    }

    private TextToSpeech mTts;

    private boolean isInitSuccess;

    // 初始化TTS引擎
    public void init(Context context) {
        mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TTS引擎初始化成功
                    Locale language = Locale.getDefault(); // 获取当前系统语言
                    int result = mTts.setLanguage(language); // 设置TTS引擎语言
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // 不支持当前语言
                        LogUtils.e("Language not supported");
                    } else {
                        // TTS引擎初始化成功，可以进行后续操作
                        LogUtils.i("TTS engine initialized");
                        isInitSuccess = true;
                    }
                } else {
                    // TTS引擎初始化失败
                    LogUtils.i("TTS engine initialization failed");
                }
            }
        });
    }

    public void convertTextToSpeech(String text) {
        if (!isInitSuccess) return;
        if (mTts != null && !mTts.isSpeaking()) {
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void shutdown() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }
}
