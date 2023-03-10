package com.taopao.fastprinter.templates;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.lvrenyang.io.Pos;
import com.taopao.fastprinter.base.BaseActivity;
import com.taopao.fastprinter.utils.PrinterUtils;
import com.taopao.fastprinter.SearchBTActivity;
import com.taopao.fastprinter.databinding.ActivityTemplate1Binding;
import com.taopao.fastprinter.utils.AlertUtils;
import com.taopao.fastprinter.utils.StringUtils;
import com.tencent.mmkv.MMKV;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Template1Activity extends BaseActivity {
    private ActivityTemplate1Binding mBinding;

    @Override
    public ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        mBinding = ActivityTemplate1Binding.inflate(layoutInflater);
        return mBinding;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setBleStatues();
        mBinding.tvCon.setOnClickListener(v -> {
            ActivityUtils.startActivity(this, SearchBTActivity.class);
        });
        mBinding.tvPrinter.setOnClickListener(v -> {
            print(0);
        });
        mBinding.tvPrinterblod.setOnClickListener(v -> {
            print(8);
        });
        mBinding.btnAddline.setOnClickListener(v -> {
            addLine();
        });
        mBinding.btnAddline2.setOnClickListener(v -> {
            addLine2();
        });
        mBinding.btnDate.setOnClickListener(v -> {
            AlertUtils.chooseDate(this, new AlertUtils.OnSelectListener() {
                @Override
                public void text(String text) {
                    mBinding.etDate.setText(text);
                }
            });
        });
        mBinding.btnTime.setOnClickListener(v -> {
            AlertUtils.chooseTime(this, new AlertUtils.OnSelectListener() {
                @Override
                public void text(String text) {
                    mBinding.etTime.setText(text);
                }
            });
        });
        showNormal();
    }

    private void setBleStatues() {
        boolean isOpened = PrinterUtils.getInstance().mBtPrinting.IsOpened();
        if (isOpened) {
            mBinding.tvCon.setText("打印机已连接");
        } else {
            mBinding.tvCon.setText("打印机为连接-点击去连接");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBleStatues();
    }

    String line = "-";

    private void addLine() {
        String carNum = mBinding.etCarnum.getText().toString().trim();
        boolean result = StringUtils.isStringMadeOf(carNum, line);
        if (result) {
            carNum = carNum + "-";
        } else {
            carNum = "-";
        }
        mBinding.etCarnum.setText(carNum);
        mBinding.etCarnum.setSelection(carNum.length());
    }

    private void addLine2() {
        String carNum = mBinding.etHuonum.getText().toString().trim();
        boolean result = StringUtils.isStringMadeOf(carNum, line);
        if (result) {
            carNum = carNum + "-";
        } else {
            carNum = "-";
        }
        mBinding.etHuonum.setText(carNum);
        mBinding.etHuonum.setSelection(carNum.length());
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
        mPos.POS_S_TextOut("序号" + StringUtils.prefixBlankToSpecifyLength(no_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("日期" + StringUtils.prefixBlankToSpecifyLength(date_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("时间" + StringUtils.prefixBlankToSpecifyLength(time_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("车号" + StringUtils.prefixBlankToSpecifyLength(platenumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("货号" + StringUtils.prefixBlankToSpecifyLength(huonumber_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("毛重" + StringUtils.prefixBlankToSpecifyLength(roughweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("皮重" + StringUtils.prefixBlankToSpecifyLength(tareweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("净重" + StringUtils.prefixBlankToSpecifyLength(netweight_str, 11) + "\r\n", "GBK", 0, 1, 1, 0, blackNumber);
        mPos.POS_S_TextOut("~~~~~~~~~~~~~~~\r\n", "GBK", 0, 2, 2, 0, blackNumber);
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        mPos.POS_FeedLine();
        MMKV.defaultMMKV().putString("carNum", platenumber_str);
    }

}