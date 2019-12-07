package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgTableBean;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgVpAdapter;
import com.ysxsoft.deliverylocker_big.ui.fragment.BaseFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogNumbCodeFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogQrCodeFragment;
import com.ysxsoft.deliverylocker_big.utils.KeybordUtils;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.TimerUtils;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;
import com.ysxsoft.deliverylocker_big.widget.MyConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class ThrowDialog extends BaseDialog implements BaseDialog.OnDissmissListener, Application.ActivityLifecycleCallbacks {


    private int height;
    private MyConstraintLayout layoutParent;
    private ImageView ivQrCode;
    private TextView tvTimer;

    private int timer = 60;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (timer > 0) {
                tvTimer.setText(String.format("%sS", --timer));
                mHandler.postDelayed(runnable, 1000);
            } else
                dismiss();
        }
    };


    public ThrowDialog(int height) {
        this.height = height;
    }

    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_throw;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        setOnDissmissListener(this);
        MyApplication.getApplication().registerActivityLifecycleCallbacks(this);
        holder.getView(R.id.tvBack).setOnClickListener(v -> dismiss());
        tvTimer = holder.getView(R.id.tvTimer);
        layoutParent = holder.getView(R.id.layoutParent);
        layoutParent.post(()->{
            ViewGroup.LayoutParams params = layoutParent.getLayoutParams();
            params.height = height;
            layoutParent.setLayoutParams(params);
            ivQrCode.post(()->{
                Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(String.format("https://iot.modoubox.com/web_wechat/download_app?cid=%s", DeviceInfo.getIntence().getCompany_id()),
                        ivQrCode.getWidth(), ivQrCode.getHeight());
                ivQrCode.setImageBitmap(bitmap);
            });
        });
        layoutParent.setListener(()-> {
            timer = 60;//重置倒计时
        });
        ivQrCode = holder.getView(R.id.ivQrCode);
        setOnDissmissListener(this);
        mHandler.post(runnable);//开启倒计时
    }



    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof MainActivity){
            mHandler.postDelayed(runnable, 1000);
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
        if (activity instanceof MainActivity) {
            mHandler.removeCallbacks(runnable);
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
        mHandler.removeCallbacks(runnable);
        MyApplication.getApplication().unregisterActivityLifecycleCallbacks(this);
    }
}
