package com.taopao.fastprinter.base;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

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

    public ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        return null;
    }

    public int getLayoutId() {
        return 0;
    }


    public void initView(@Nullable Bundle savedInstanceState) {

    }

}