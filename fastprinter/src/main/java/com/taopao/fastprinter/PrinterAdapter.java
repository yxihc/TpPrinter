package com.taopao.fastprinter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.taopao.fastprinter.bean.PrinterBean;
import com.taopao.fastprinter.databinding.RecycleItemPrinterBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrinterAdapter extends BaseQuickAdapter<PrinterBean, PrinterAdapter.VH> {
    @Override
    protected void onBindViewHolder(@NonNull VH vh, int i, @Nullable PrinterBean item) {
        vh.binding.etContent.setText(item.content);
        vh.binding.tvTitle.setText(item.getTitle());
        vh.binding.cbPrinter.setChecked(item.isPrinter);
        vh.binding.cbPrinter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setPrinter(b);
            }
        });
        // 0字符串 1数字 2日期 3时间 4end 5标题 6自定义
        switch (item.getType()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                vh.binding.btnDate.setVisibility(View.VISIBLE);
                break;
            case 3:
                vh.binding.btnTime.setVisibility(View.VISIBLE);
                break;
            case 4:
                break;
            case 5:
                vh.binding.etContent.setGravity(Gravity.CENTER);
                break;
        }
        vh.binding.btnDate.setOnClickListener(v -> {
            chooseDate(vh.binding.etContent);
        });
        vh.binding.btnTime.setOnClickListener(v -> {
            chooseTime(vh.binding.etContent);
        });
    }

    @NonNull
    @Override
    protected VH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        RecycleItemPrinterBinding binding = RecycleItemPrinterBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new VH(binding.getRoot(), binding);
    }

    class VH extends RecyclerView.ViewHolder {
        public RecycleItemPrinterBinding binding;

        public VH(@NonNull View itemView) {
            super(itemView);
        }

        public VH(@NonNull View itemView, RecycleItemPrinterBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    private void chooseDate(EditText editText) {

        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(2022, 6, 0);

        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String format1 = format.format(date);
                editText.setText(format1);
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

    private void chooseTime(EditText editText) {
        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String time = timeFormat.format(date);
                editText.setText(time);
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
}
