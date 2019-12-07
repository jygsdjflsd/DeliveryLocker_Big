package com.ysxsoft.deliverylocker_big.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.ysxsoft.deliverylocker_big.app.MyApplication;

public class TelUtils {

    /**
     * 判断手机号
     *
     * @param pNumb
     * @return
     */
    public static boolean isPhoneNumb(String pNumb) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188,198
         * 联通号段: 130,131,132,145,155,156,166,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189,199
         * @param str
         * @return 待检测的字符串
         */
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(16[5,6])|(18[0-9])|(19[1,8,9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(pNumb))
            return false;
        else
            return pNumb.matches(telRegex);
    }

    /**
     * 判断字符串是否是正整数
     *
     * @param pNumb
     * @return
     */
    public static boolean isNumber(String pNumb) {
        for (int i = 0; i < pNumb.length(); i++) {
            if (!Character.isDigit(pNumb.charAt(i))){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断字符串是是否为数值，包括小数和负数
     *
     * @param pNumb
     * @return
     */
    public static boolean isNumberAll(String pNumb) {
        Boolean strResult = pNumb.matches("-[0-9]+(.[0-9]+)?|[0-9]+(.[0-9]+)?");
        if(strResult) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 拨打电话，无需权限
     */
    public static void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getApplication().startActivity(intent);
    }




}
