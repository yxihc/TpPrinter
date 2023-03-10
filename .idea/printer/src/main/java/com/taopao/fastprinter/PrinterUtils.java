package com.taopao.fastprinter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.Pos;
import com.taopao.fastprinter.myprinter.Global;

public class PrinterUtils {
    public Pos mPos;
    public BTPrinting mBtPrinting;
    public BluetoothAdapter mDefaultAdapter;
    private MyLeScanCallback mCallback;

    private PrinterUtils() {
        if (mPos == null || mBtPrinting == null) {
            mPos = new Pos();
            mBtPrinting = new BTPrinting();
            mPos.Set(mBtPrinting);
        }
    }

    private static volatile PrinterUtils sInstance;

    public static PrinterUtils getInstance() {
        if (sInstance == null) {
            synchronized (PrinterUtils.class) {
                if (sInstance == null) {
                    sInstance = new PrinterUtils();
                }
            }
        }
        return sInstance;
    }

    private void printer() {

    }

    private Pos getPos() {
        return mPos;
    }

    @SuppressLint("MissingPermission")
    public void autoConnect(String name, onConnectListener listener) {
        mListener = listener;
        mDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
        mDefaultAdapter.startDiscovery();
        mDefaultAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
                String deviceName = device.getAddress();
                if (deviceName != null) {
                    if (deviceName.equals(name)) {
                        boolean discovery = mDefaultAdapter.cancelDiscovery();
                        mDefaultAdapter.stopLeScan(this);
                        senMessage(device.getAddress());
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        mDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
        mDefaultAdapter.startDiscovery();
        mCallback = new MyLeScanCallback();
        mDefaultAdapter.startLeScan(leScanCallback);
    }

    public void connectPrinterDevice(BluetoothDevice device) {
//        boolean discovery = mDefaultAdapter.cancelDiscovery();
//        mDefaultAdapter.stopLeScan(mCallback);
        mBtPrinting.Open(device.getAddress());
        boolean isOpened = mBtPrinting.IsOpened();
        if (isOpened) {
//            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        } else {
//            Log.e(TAG, "连接失败: " + new Date().getTime());
        }
    }

    public void connectPrinterDevice(String address) {
        mBtPrinting.Open(address);
        boolean isOpened = mBtPrinting.IsOpened();
    }

    class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        @SuppressLint("MissingPermission")
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            if (name != null) {
                if (device.getName().toString().indexOf("MPT-II") > -1) {
                    connectPrinterDevice(device);
                }
            }
        }
    }

    public interface onConnectListener {
        void onConnectChange(boolean isConnect);
    }


    public static final int MSG_WORKTHREAD_HANDLER_CONNECTBT = 1000001;

    private void senMessage(String BTAddress) {
        Message msg = mHandler
                .obtainMessage(MSG_WORKTHREAD_HANDLER_CONNECTBT);
        msg.obj = BTAddress;
        mHandler.sendMessage(msg);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_WORKTHREAD_HANDLER_CONNECTBT: {

                    String BTAddress = (String) msg.obj;
                    mBtPrinting.Open(BTAddress);
                    boolean isOpened = mBtPrinting.IsOpened();
                    if (isOpened) {
                        if (mListener != null)
                            mListener.onConnectChange(true);
                    } else {
                        if (mListener != null)
                            mListener.onConnectChange(false);
                    }
                    break;
                }
            }
        }
    };

    private onConnectListener mListener;


}
