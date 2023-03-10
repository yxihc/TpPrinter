package com.taopao.fastprinter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.viewbinding.ViewBinding;

/**
 * @Author: TaoPao
 * @Date: 5/18/22 8:23 PM
 * @Description: java类作用描述
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//          两个都有的话优先使用viewbinding
            ViewBinding viewBinding = obtainViewBinding(getLayoutInflater());
            if (viewBinding != null) {
                setContentView(viewBinding.getRoot());
            } else {
                int layoutResID = getLayoutId();
                //如果getLayoutRes返回0,框架则不会调用setContentView()
                if (layoutResID != 0) {
                    setContentView(layoutResID);
                }
            }
        } catch (Exception e) {
            if (e instanceof InflateException) throw e;
            e.printStackTrace();
        }
        initView(savedInstanceState);
    }

    ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        return null;
    }

    int getLayoutId() {
        return 0;
    }


    void initView(@Nullable Bundle savedInstanceState) {

    }

}