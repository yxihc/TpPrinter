package com.taopao.fastprinter;

public class SpUtils {


    private SpUtils() {
    }

    private static volatile SpUtils sInstance;

    public static SpUtils getInstance() {
        if (sInstance == null) {
            synchronized (SpUtils.class) {
                if (sInstance == null) {
                    sInstance = new SpUtils();
                }
            }
        }
        return sInstance;
    }


    public void putString(){
    }



}
