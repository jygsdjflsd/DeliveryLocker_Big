package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgTableBean;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgVpAdapter;
import com.ysxsoft.deliverylocker_big.ui.fragment.BaseFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogNumbCodeFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogQrCodeFragment;
import com.ysxsoft.deliverylocker_big.utils.TimerUtils;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;
import com.ysxsoft.deliverylocker_big.widget.MyConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.ysxsoft.deliverylocker_big.ui.fragment.DialogQrCodeFragment.TYPE_PICKUP;

public class PickUpDialog extends BaseDialog implements BaseDialog.OnDissmissListener, Application.ActivityLifecycleCallbacks {


    private int height;
    private MyConstraintLayout layoutParent;
    private ImageView ivQrCode;
    private TextView tvTimer;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String[] tabs = {"二维码取件", "取件码取件"};

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

    public PickUpDialog(int height) {
        this.height = height;
    }

    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_pickup;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        setOnDissmissListener(this);
        MyApplication.getApplication().registerActivityLifecycleCallbacks(this);
        holder.getView(R.id.tvBack).setOnClickListener(v -> dismiss());
        layoutParent = holder.getView(R.id.layoutParent);
        layoutParent.post(() -> {
            ViewGroup.LayoutParams params = layoutParent.getLayoutParams();
            params.height = height;
            layoutParent.setLayoutParams(params);
        });
        layoutParent.setListener(()-> timer = 60);
        ivQrCode = holder.getView(R.id.ivQrCode);
        tvTimer = holder.getView(R.id.tvTimer);
        tabLayout = holder.getView(R.id.tabLayout);
        viewPager = holder.getView(R.id.viewPager);
        initFragemnt();
        setOnDissmissListener(this);
        mHandler.post(runnable);//开启倒计时
    }

    /**
     * 加载fragment
     */
    private void initFragemnt() {
        List<FgTableBean<BaseFragment>> informfragments = new ArrayList<>();
        informfragments.add(new FgTableBean<>(DialogQrCodeFragment.getInstance(TYPE_PICKUP), tabs[0], 0));
        informfragments.add(new FgTableBean<>(DialogNumbCodeFragment.getInstance(), tabs[1], 1));
        FgVpAdapter<BaseFragment> fgVpAdapter = new FgVpAdapter<>(getChildFragmentManager(), informfragments);
        viewPager.setAdapter(fgVpAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    //activity 生命周期
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
