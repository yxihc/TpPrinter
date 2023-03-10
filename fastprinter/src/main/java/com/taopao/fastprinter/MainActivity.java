package com.taopao.fastprinter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.taopao.fastprinter.base.BaseActivity;
import com.taopao.fastprinter.databinding.ActivityMainBinding;
import com.taopao.fastprinter.templates.Template1Activity;
import com.taopao.fastprinter.templates.Template2Activity;
import com.taopao.fastprinter.utils.FirimUtils;
import com.taopao.fastprinter.utils.PrinterUtils;
import com.tencent.mmkv.MMKV;

import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;

    @Override
    public ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater);
        return mBinding;
    }

    private ProgressDialog mLoadingDialog;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage("正在自动连接");
        mBinding.search.setOnClickListener(v -> {
            XXPermissions.with(this)
                    .permission(Permission.Group.BLUETOOTH)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            startActivity(new Intent(MainActivity.this, SearchBTActivity.class));
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                        }
                    });
        });

        mBinding.tvUpdata.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            // 设置意图动作为打开浏览器
//            intent.setAction(Intent.ACTION_VIEW);
//            // 声明一个Uri
//            String url = "https://spark.appc02.com/62y1";
//            Uri uri = Uri.parse(url);
//            intent.setData(uri);
//            startActivity(intent);
            FirimUtils.getAppinfo();
        });

//        applyBle();

        mBinding.tvTemplate1.setOnClickListener(v -> {
            ActivityUtils.startActivity(this, Template1Activity.class);
        });
        mBinding.tvTemplate2.setOnClickListener(v -> {
            ActivityUtils.startActivity(this, Template2Activity.class);
        });
        mBinding.tvUpdateapp.setOnClickListener(v -> {
            FirimUtils.getAppinfo();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean b = PrinterUtils.getInstance().mBtPrinting.IsOpened();
        if (b) {
            mBinding.tvCon.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvCon.setVisibility(View.GONE);
        }
    }

    private void autoConnect() {
        String blpaddress = MMKV.defaultMMKV().getString("blpaddress", "");
        if (blpaddress.isEmpty()) {
            return;
        }
        mLoadingDialog.show();
        PrinterUtils.getInstance().autoConnect(blpaddress, new PrinterUtils.onConnectListener() {
            @Override
            public void onConnectChange(boolean isConnect) {
                mLoadingDialog.dismiss();
                if (isConnect) {
                    ToastUtils.showShort("连接成功");
                    mBinding.tvCon.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.showShort("连接失败");
                    mBinding.tvCon.setVisibility(View.GONE);
                }
            }
        });
    }

    private void applyBle() {
        XXPermissions.with(this)
                .permission(Permission.Group.BLUETOOTH)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        autoConnect();
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                    }
                });
    }


}