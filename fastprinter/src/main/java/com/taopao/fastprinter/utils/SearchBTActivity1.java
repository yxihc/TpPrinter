package com.taopao.fastprinter.utils;//package com.taopao.fastprinter.utils;
//
//import android.app.ProgressDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.viewbinding.ViewBinding;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.taopao.fastprinter.base.BaseActivity;
//import com.taopao.fastprinter.databinding.ActivitySearchBTBinding;
//import com.taopao.fastprinter.myprinter.Global;
//import com.taopao.fastprinter.myprinter.WorkService;
//
//import java.lang.ref.WeakReference;
//
//public class SearchBTActivity1 extends BaseActivity {
//    private ActivitySearchBTBinding mBinding;
//    private static Handler mHandler = null;
//    private BroadcastReceiver broadcastReceiver = null;
//    private ProgressDialog dialog;
//    private IntentFilter intentFilter;
//
//    @Override
//    ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
//        mBinding = ActivitySearchBTBinding.inflate(layoutInflater);
//        return mBinding;
//    }
//
//    @Override
//    void initView(@Nullable Bundle savedInstanceState) {
//        dialog = new ProgressDialog(this);
//        mBinding.search.setOnClickListener(v -> {
//            searchBt();
//        });
//
//        if (null == WorkService.workThread) {
//            Intent intent = new Intent(this, WorkService.class);
//            startService(intent);
//            mHandler = new MHandler(this);
//            WorkService.addHandler(mHandler);
//            initBroadcast();
//        }else{
//            mHandler = new MHandler(this);
//            WorkService.addHandler(mHandler);
//            initBroadcast();
//        }
//
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        WorkService.delHandler(mHandler);
//        mHandler = null;
//        uninitBroadcast();
//    }
//
//    public void searchBt() {
//        Log.e("=====", "searchBt: ");
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        if (adapter == null) {
//            finish();
//        }
//
//        if (!adapter.isEnabled()) {
//            if (adapter.enable()) {
//                while (!adapter.isEnabled());
//            } else {
//                finish();
//                return;
//            }
//        }
//        if(null != WorkService.workThread)
//        {
//            WorkService.workThread.disconnectBt();
//            TimeUtils.WaitMs(10);
//        }
//        adapter.cancelDiscovery();
//        mBinding.ll.removeAllViews();
//        TimeUtils.WaitMs(10);
//        adapter.startDiscovery();
//    }
//
//
//
//
//    private void initBroadcast() {
//        broadcastReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                // TODO Auto-generated method stub
//                String action = intent.getAction();
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    if (device == null)
//                        return;
//                    final String address = device.getAddress();
//                    String name = device.getName();
//                    if (name == null)
//                        name = "BT";
//                    else if (name.equals(address))
//                        name = "BT";
//                    Button button = new Button(context);
//                    button.setText(name + ": " + address);
//                    button.setGravity(Gravity.CENTER_VERTICAL
//                            | Gravity.LEFT);
//                    button.setOnClickListener(new View.OnClickListener() {
//
//                        public void onClick(View arg0) {
//                            // TODO Auto-generated method stub
//                            WorkService.workThread.disconnectBt();
//                            // 只有没有连接且没有在用，这个才能改变状态
//                            dialog.setMessage(Global.toast_connecting + " "
//                                    + address);
//                            dialog.setIndeterminate(true);
//                            dialog.setCancelable(false);
//                            dialog.show();
//                            WorkService.workThread.connectBt(address);
//                            Log.e("====", "address: "+address);
//                        }
//                    });
//                    button.getBackground().setAlpha(100);
//                    mBinding.ll.addView(button);
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
//                        .equals(action)) {
//                    mBinding.progress.setIndeterminate(true);
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
//                        .equals(action)) {
//                    mBinding.progress.setIndeterminate(false);
//                }
//
//            }
//
//        };
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(broadcastReceiver, intentFilter);
//    }
//
//    private void uninitBroadcast() {
//        if (broadcastReceiver != null)
//            unregisterReceiver(broadcastReceiver);
//    }
//
//    static class MHandler extends Handler {
//
//        WeakReference<SearchBTActivity1> mActivity;
//
//        MHandler(SearchBTActivity1 activity) {
//            mActivity = new WeakReference<SearchBTActivity1>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            SearchBTActivity1 theActivity = mActivity.get();
//            switch (msg.what) {
//                /**
//                 * DrawerService 的 onStartCommand会发送这个消息
//                 */
//
//                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
//                    int result = msg.arg1;
//                    Toast.makeText(
//                            theActivity,
//                            (result == 1) ? Global.toast_success
//                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
//                    theActivity.dialog.cancel();
//                    if (1 == result) {
//                        PrintTest();
//                    }
//                    break;
//                }
//
//            }
//        }
//
//        void PrintTest() {
//
//            if (true)return;
//
////            String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n";
//            String str = "净重                       kg";
//
//
//            byte[] tmp1 = { 0x1b, 0x40, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA,
//                    (byte) 0xD4, (byte) 0xD2, (byte) 0xB3, 0x0A };
//            byte[] tmp2 = { 0x1b, 0x21, 0x01 };
//            byte[] tmp3 = { 0x0A, 0x0A, 0x0A, 0x0A };
////            byte[] buf = DataUtils.byteArraysToBytes(new byte[][] { tmp1,
////                    str.getBytes(), tmp2, str.getBytes(), tmp3 });
//
//
//            byte[] buf = DataUtils.byteArraysToBytes(new byte[][] {
//                    str.getBytes() });
//
//
//            if (WorkService.workThread.isConnected()) {
//                Bundle data = new Bundle();
//                data.putByteArray(Global.BYTESPARA1, buf);
//                data.putInt(Global.INTPARA1, 0);
//                data.putInt(Global.INTPARA2, buf.length);
//                WorkService.workThread.handleCmd(Global.CMD_WRITE, data);
//            } else {
//                Toast.makeText(mActivity.get(), Global.toast_notconnect,
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}