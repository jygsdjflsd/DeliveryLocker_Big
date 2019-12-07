package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;

public class TakeCodeErrorDialog extends BaseDialog implements BaseDialog.OnDissmissListener, Application.ActivityLifecycleCallbacks{


    public static TakeCodeErrorDialog newInstance() {
        TakeCodeErrorDialog dialog = new TakeCodeErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFull", false);
        dialog.setArguments(bundle);
        return dialog;
    }

    private Handler mhandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (--timer > 0){
                mhandler.postDelayed(runnable, 1000);
            }else {
                dismiss();
            }
        }
    };
    private int timer = 15;
    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_takecode_error;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        setOnDissmissListener(this);
        MyApplication.getApplication().registerActivityLifecycleCallbacks(this);
        holder.getView(R.id.btnKnow).setOnClickListener(v -> dismiss());
        mhandler.post(runnable);//开启倒计时
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof MainActivity){
            mhandler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof MainActivity){
            mhandler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onDismiss() {
        MyApplication.getApplication().unregisterActivityLifecycleCallbacks(this);
        mhandler.removeCallbacks(runnable);
    }
}
