package com.ysxsoft.deliverylocker_big.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ysxsoft.deliverylocker_big.ActivityManager;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        ActivityManager.getAppManager().addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        init();
        initView();
    }

    protected abstract int getLayoutId();

    protected void init(){}

    protected abstract void initView();

    /**
     * 吐司
     *
     * @param tost
     */
    protected void toast(String tost) {
        ToastUtils.show(tost);
    }

    /**
     * 显示状态栏（可拉出）
     */
    protected void showStatusBar() {
        // 显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏状态栏(可拉出)
     */
    protected void hideStatusBar() {
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏虚拟按键，并且全屏

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }*/

}
