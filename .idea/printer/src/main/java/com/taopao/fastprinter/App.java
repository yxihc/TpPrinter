package com.taopao.fastprinter;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String rootDir = MMKV.initialize(this);
    }
}
