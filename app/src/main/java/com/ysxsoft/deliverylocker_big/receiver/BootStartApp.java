package com.ysxsoft.deliverylocker_big.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ysxsoft.deliverylocker_big.ui.activity.ScreenActivity;


/**
 * 开机自启动广播
 */
public class BootStartApp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(Intent.ACTION_BOOT_COMPLETED, action)) {
            //启动APP某个Activity即可，或者主activity，也可是其他组件等
            Intent intent1 = new Intent(context, ScreenActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
