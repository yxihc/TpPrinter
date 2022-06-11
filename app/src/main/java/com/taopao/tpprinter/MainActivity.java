package com.taopao.tpprinter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.taopao.hulkmvp.base.BaseActivity;
import com.taopao.tpprinter.databinding.ActivityMainBinding;

import java.nio.channels.Pipe;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;

    @Override
    public ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater);
        return mBinding;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBinding.btnLocation.setOnClickListener(v -> {
            applyLocation();
        });

        mBinding.btnBle.setOnClickListener(v -> {
            applyBle();
        });
    }

    private void applyBle() {
        XXPermissions.with(this)
                .permission(Permission.Group.BLUETOOTH)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        showToast("请求权限成功");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {

                    }
                });
    }

    private void applyLocation() {
        boolean granted = XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION);

        if (granted) {

        } else {

        }

        XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        showToast("请求权限成功");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {

                    }
                });

    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}