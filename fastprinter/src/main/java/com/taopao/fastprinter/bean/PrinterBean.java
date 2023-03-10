package com.taopao.fastprinter.bean;

public class PrinterBean {
    public String title;
    public String content;
    public int blackNumber;//0默认 8加黑
    public boolean isPrinter;
    public int type = 0;// 0字符串 1数字 2日期 3时间 4end 5标题 6自定义

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int align;

    public PrinterBean(String title, String content, int blackNumber, boolean isPrinter, int type) {
        this.title = title;
        this.content = content;
        this.blackNumber = blackNumber;
        this.isPrinter = isPrinter;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String printerContent) {
        this.content = printerContent;
    }

    public int getBlackNumber() {
        return blackNumber;
    }

    public void setBlackNumber(int blackNumber) {
        this.blackNumber = blackNumber;
    }

    public boolean isPrinter() {
        return isPrinter;
    }

    public void setPrinter(boolean printer) {
        isPrinter = printer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
