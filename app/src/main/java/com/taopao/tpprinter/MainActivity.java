package com.taopao.tpprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.taopao.hulkmvp.base.BaseActivity;
import com.taopao.tpprinter.databinding.ActivityMainBinding;
import com.taopao.tpprinter.printer.PrinterUtils;
import com.taopao.tpprinter.printer.PrinterWriter;
import com.taopao.tpprinter.printer.PrinterWriter58mm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;
    private BluetoothAdapter mDefaultAdapter;

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

        mBinding.btngetBle.setOnClickListener(v -> {
            autoCon();
        });
        mBinding.btnPrinter.setOnClickListener(v -> {
            btnPrinter();
        });
    }

    private void btnPrinter() {
        try {
            PrinterWriter printer = new PrinterWriter58mm();
            printer.init();
            printer.print("2323232");
            Ble.getInstance().writeByUuid(mDevice, printer.getDataAndClose(),UUID.fromString(mDevice.getBleName()),UUID.fromString(mDevice.getBleName()),new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    showToast("e");
                }

                @Override
                public void onWriteFailed(BleDevice device, int failedCode) {
                    super.onWriteFailed(device, failedCode);
                    showToast("错误："+failedCode+device.getBleName());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    BleDevice mDevice;

    private void autoCon() {
        Ble.getInstance().startScan(new BleScanCallback<BleDevice>() {
            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                Log.e("======", "onLeScan: " + device.getBleName());
                if (device.getBleName() == null) return;
                if (device.getBleName().indexOf("MPT-II") > -1) {
                    Ble.getInstance().stopScan();
                    mDevice = device;
                    Ble.getInstance().connect(device, new BleConnectCallback<BleDevice>() {
                        @Override
                        public void onConnectionChanged(BleDevice device) {
                            showToast("链接成功");
                        }
                    });
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
                .permission(Permission.ACCESS_COARSE_LOCATION)
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