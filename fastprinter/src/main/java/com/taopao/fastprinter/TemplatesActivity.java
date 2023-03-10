package com.taopao.fastprinter;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.taopao.fastprinter.base.BaseActivity;
import com.taopao.fastprinter.bean.PrinterBean;
import com.taopao.fastprinter.databinding.ActivityTemplatesBinding;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;

public class TemplatesActivity extends BaseActivity {
    private ActivityTemplatesBinding mBinding;
    private ArrayList<PrinterBean> mPrinterBeans;
    private PrinterAdapter mPrinterAdapter;

    @Override
    public ViewBinding obtainViewBinding(@Nullable LayoutInflater layoutInflater) {
        mBinding = ActivityTemplatesBinding.inflate(layoutInflater);
        return mBinding;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPrinterBeans = new ArrayList<>();

        addData();
        mPrinterAdapter = new PrinterAdapter();
        mPrinterAdapter.setItems(mPrinterBeans);
        mBinding.rv.setAdapter(mPrinterAdapter);
    }

    private void addData() {
        @Language("JSON") String json = "[\n" +
                "  {\n" +
                "\"title\":\"货号\",\n" +
                " \"printerContent\": \"\"," +
                " \n" +
                "  \"blackNumber\":1,\n" +
                "  \"isPrinter\":true,\n" +
                "  \"type\": 0\n" +
                "}\n" +
                "\n" +
                "]";
        mPrinterBeans.add(new PrinterBean("标题", "称     重     单", 0, true, 1));
        mPrinterBeans.add(new PrinterBean("序号", "", 0, true, 1));
        mPrinterBeans.add(new PrinterBean("日期", "", 0, true, 2));
        mPrinterBeans.add(new PrinterBean("时间", "", 0, true, 3));
        mPrinterBeans.add(new PrinterBean("车号", "", 0, true, 0));
        mPrinterBeans.add(new PrinterBean("货号", "", 0, true, 1));
        mPrinterBeans.add(new PrinterBean("毛重", "", 0, true, 1));
        mPrinterBeans.add(new PrinterBean("皮重", "", 0, true, 1));
        mPrinterBeans.add(new PrinterBean("净重", "", 0, true, 1));
    }
}