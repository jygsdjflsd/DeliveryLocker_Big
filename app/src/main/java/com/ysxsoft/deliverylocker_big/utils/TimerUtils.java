package com.ysxsoft.deliverylocker_big.utils;

import android.os.CountDownTimer;

public class TimerUtils {

    public static final int SECOND = 1000;
    public static final int MINUTE = 60*1000;
    public static final int HOUR = 60*60*1000;
    public static final int DAY = 24*60*60*1000;
    public static final int COUNTDOWNTIME = 5000;
    /**
     * 倒计时
     * @param millisInfuture 倒计时时长
     * @param countDownInterval 时间间隔
     * @return
     */
    public static CountDownTimer countDown(int millisInfuture, int countDownInterval, CountDownTimerListener listener){
       return new CountDownTimer(millisInfuture, countDownInterval) {
            @Override
            public void onTick(long l) {
                if (listener != null)
                    listener.onTick(l/SECOND);
            }

            @Override
            public void onFinish() {
                if (listener != null)
                    listener.onFinish();
            }
        }.start();
    }
    /**
     * 倒计时
     * @param millisInfuture
     * @param countDownInterval
     * @return
     */
    public static CountDownTimer countDown(long millisInfuture, int countDownInterval, CountDownTimerListener listener){
       return new CountDownTimer(millisInfuture, countDownInterval) {
            @Override
            public void onTick(long l) {
                if (listener != null)
                    listener.onTick(l/SECOND);
            }

            @Override
            public void onFinish() {
                if (listener != null)
                    listener.onFinish();
            }
        }.start();
    }


    public interface CountDownTimerListener{
        void onTick(long l);
        void onFinish();
    }
}
