package com.taopao.fastprinter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.viewbinding.ViewBinding;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lvrenyang.io.Pos;
import com.taopao.fastprinter.databinding.ActivityMainBinding;
import com.tencent.mmkv.MMKV;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private BluetoothAdapter mDefaultAdapter;
    private ProgressDialog mLoadingDialog;

    @Override
    ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater);
        return mBinding;
    }

    @Override
    void initView(@Nullable Bundle savedInstanceState) {
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
        mBinding.tvPrinter.setOnClickListener(v -> {
            print(0);
        });
        mBinding.tvPrinterblod.setOnClickListener(v -> {
            print(8);
        });
        mBinding.tvPrinternohh.setOnClickListener(v -> {
            printNohh(0);
        });
        mBinding.tvPrinternohhblod.setOnClickListener(v -> {
            printNohh(8);
        });

        mBinding.tvUpdata.setOnClickListener(v -> {
            Intent intent = new Intent();
            // 设置意图动作为打开浏览器
            intent.setAction(Intent.ACTION_VIEW);
            // 声明一个Uri
            String url = "https://spark.appc02.com/62y1";
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            startActivity(intent);
        });
        mBinding.btnDate.setOnClickListener(v -> {
            chooseDate();
        });
        mBinding.btnTime.setOnClickListener(v -> {
            chooseTime();
        });
        showNormal();

        applyBle();
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
                    showToast("连接成功");
                    mBinding.tvCon.setVisibility(View.VISIBLE);
                } else {
                    showToast("连接失败");
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void chooseTime() {
        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String time = timeFormat.format(date);
                mBinding.etTime.setText(time);
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                }).setType(new boolean[]{false, false, false, true, true, true})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .setDividerColor(Color.parseColor("#FF6688"))
                .setTitleText("请选择时间")
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setSubmitColor(Color.parseColor("#FF6688"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#9f9f9f"))//取消按钮文字颜色
                ;


        TimePickerView pvTime = timePickerBuilder.build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
        pvTime.show();
    }

    private void showNormal() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = format.format(date);
        mBinding.etDate.setText(format1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(date);
        mBinding.etTime.setText(time);

        mBinding.etCarnum.setText(MMKV.defaultMMKV().getString("carNum", ""));
    }

    private void chooseDate() {

        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(2022, 6, 0);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String format1 = format.format(date);
                mBinding.etDate.setText(format1);
            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .setDividerColor(Color.parseColor("#FF6688"))
//                .setRangDate(startDate, selectedDate)
                .setTitleText("请选择年月日")
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setSubmitColor(Color.parseColor("#FF6688"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#9f9f9f"))//取消按钮文字颜色
                ;
        TimePickerView pvTime = timePickerBuilder.build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
        pvTime.show();
    }

    public void print(int blackNumber) {
        final String title_str = "称     重     单";
        final String no_str = mBinding.etXh.getText().toString().trim();
        final String date_str = mBinding.etDate.getText().toString().trim();
        final String time_str = mBinding.etTime.getText().toString().trim();
        final String platenumber_str = mBinding.etCarnum.getText().toString().trim();
        final String huonumber_str = mBinding.etHuonum.getText().toString().trim();
        final String roughweight_str = mBinding.etMaoz.getText().toString().trim() + mBinding.etMaozkg.getText().toString().trim();
        final String tareweight_str = mBinding.etPiz.getText().toString().trim() + mBinding.etPizkg.getText().toString().trim();
        final String netweight_str = mBinding.etJingz.getText().toString().trim() + mBinding.etJingzkg.getText().toString().trim();
        Pos mPos = PrinterUtils.getInstance().mPos;
        mPos.POS_S_Align(1);
        mPos.POS_S_TextOut(String.valueOf(title_str) + "\r\n", "GBK", 0, 1, 1, 1, blackNumber);
        mPos.POS_S_Align(0);
        mPos.POS_S_TextOut("序号" + prefixBlankToSpecifyLength(no_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("日期" + prefixBlankToSpecifyLength(date_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("时间" + prefixBlankToSpecifyLength(time_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("车号" + prefixBlankToSpecifyLength(platenumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("货号" + prefixBlankToSpecifyLength(huonumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("毛重" + prefixBlankToSpecifyLength(roughweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("皮重" + prefixBlankToSpecifyLength(tareweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("净重" + prefixBlankToSpecifyLength(netweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("~~~~~~~~~~~~~~~\r\n", "GBK", 0, 2, 2, 0, blackNumber);
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        MMKV.defaultMMKV().putString("carNum", platenumber_str);
    }
    public void printNohh(int blackNumber) {
        final String title_str = "称     重     单";
        final String no_str = mBinding.etXh.getText().toString().trim();
        final String date_str = mBinding.etDate.getText().toString().trim();
        final String time_str = mBinding.etTime.getText().toString().trim();
        final String platenumber_str = mBinding.etCarnum.getText().toString().trim();
        final String huonumber_str = mBinding.etHuonum.getText().toString().trim();
        final String roughweight_str = mBinding.etMaoz.getText().toString().trim() + mBinding.etMaozkg.getText().toString().trim();
        final String tareweight_str = mBinding.etPiz.getText().toString().trim() + mBinding.etPizkg.getText().toString().trim();
        final String netweight_str = mBinding.etJingz.getText().toString().trim() + mBinding.etJingzkg.getText().toString().trim();
        Pos mPos = PrinterUtils.getInstance().mPos;
        mPos.POS_S_Align(1);
        mPos.POS_S_TextOut(String.valueOf(title_str) + "\r\n", "GBK", 0, 1, 1, 1, blackNumber);
        mPos.POS_S_Align(0);
        mPos.POS_S_TextOut("序号" + prefixBlankToSpecifyLength(no_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("日期" + prefixBlankToSpecifyLength(date_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("时间" + prefixBlankToSpecifyLength(time_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("车号" + prefixBlankToSpecifyLength(platenumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
//        mPos.POS_S_TextOut("货号" + prefixBlankToSpecifyLength(huonumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("毛重" + prefixBlankToSpecifyLength(roughweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("皮重" + prefixBlankToSpecifyLength(tareweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("净重" + prefixBlankToSpecifyLength(netweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("~~~~~~~~~~~~~~~\r\n", "GBK", 0, 2, 2, 0, blackNumber);
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        MMKV.defaultMMKV().putString("carNum", platenumber_str);
    }

    private String prefixBlankToSpecifyLength(String paramString, int paramInt) {
        paramInt = 11;
        while (true) {
            if (paramString.length() >= paramInt)
                return paramString;
            paramString = " " + paramString;
        }
    }
}