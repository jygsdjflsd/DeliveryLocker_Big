package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgAdapter;
import com.ysxsoft.deliverylocker_big.ui.fragment.BaseFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment1;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment2;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment3;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment4;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment5;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment6;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment7;
import com.ysxsoft.deliverylocker_big.utils.KeybordUtils;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;
import com.ysxsoft.deliverylocker_big.widget.CustomViewPager;
import com.ysxsoft.deliverylocker_big.widget.MyConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 暂存dialog
 */
public class TSDialog extends BaseDialog implements BaseDialog.OnDissmissListener, ViewPager.OnPageChangeListener, FragmentListener, Application.ActivityLifecycleCallbacks {


    private int height;
    private MyConstraintLayout layoutParent;
    private TextView tvTimer;
    private CustomViewPager viewPager;
    private FgAdapter<BaseFragment> fgVpAdapter;
    private int pagerPosition;

    private Handler mHandler;
    private Runnable runnable;
    private int timer = 60;

    /**
     * 临时数据
     */
    private String token;

    public TSDialog(int height) {
        this.height = height;
    }

    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_ts;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        initView(holder);
        initFragment();
        initHandler();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(ViewHolder holder) {
        setOnDissmissListener(this);
        MyApplication.getApplication().registerActivityLifecycleCallbacks(this);
        layoutParent = holder.getView(R.id.layoutParent);
        tvTimer = holder.getView(R.id.tvTimer);
        viewPager = holder.getView(R.id.viewPager);
        viewPager.setScanScroll(false);

        holder.getView(R.id.tvBack).setOnClickListener(v -> {
            switch (pagerPosition) {
                case 0:
                    dismiss();
                    break;
                case 1:
                    viewPager.setCurrentItem(0);
                    break;
                case 4:
                    viewPager.setCurrentItem(3);
                    break;
                case 5:
                    viewPager.setCurrentItem(0, false);
                    break;
                case 2:
                    viewPager.setCurrentItem(1);
                    break;
                case 3:
                    viewPager.setCurrentItem(2);
                    break;
                case 6:
                    viewPager.setCurrentItem(5);
                    break;
            }
        });
        layoutParent.post(() -> {
            ViewGroup.LayoutParams params = layoutParent.getLayoutParams();
            params.height = height;
            layoutParent.setLayoutParams(params);
        });
        layoutParent.setListener(() -> {
            timer = 60;//重置倒计时
            KeybordUtils.hideSoftKeyboard(layoutParent);
        });
    }

    /**
     * 加载fragment
     */
    private void initFragment() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(SaveFragment1.getInstance(0, this));
        fragments.add(SaveFragment2.getInstance(1, this));
        fragments.add(SaveFragment3.getInstance(2, this));
        fragments.add(SaveFragment4.getInstance(3, this));
        fragments.add(SaveFragment5.getInstance(4, this));
        fragments.add(SaveFragment6.getInstance(5, this));
        fragments.add(SaveFragment7.getInstance(6, this));
        fgVpAdapter = new FgAdapter<>(getChildFragmentManager(), fragments);
        viewPager.setAdapter(fgVpAdapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 倒计时页面
     */
    private void initHandler() {
        mHandler = new Handler();
        runnable = () -> {
            if (--timer == 0) {
                dismiss();
                return;
            }
            tvTimer.setText(String.format("%sS", timer));
            mHandler.postDelayed(runnable, 1000);
        };
        mHandler.post(runnable);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pagerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void doSomething(int position, String type) {
        switch (position) {
            case 0:
                if (type.equals(SaveFragment1.TYPE_THROW)) {//存放
                    viewPager.setCurrentItem(1);
                } else if (type.equals(SaveFragment1.TYPE_GET)) {//取出
                    viewPager.setCurrentItem(5, false);
                }
                break;
            case 1://下一步
                viewPager.setCurrentItem(2);
                break;
            case 2:
                viewPager.setCurrentItem(3);
                JSONObject object;
                try {
                    object = new JSONObject();
                    object.putOpt("token", token);
                    object.putOpt("type", type);
                    fgVpAdapter.getItem(3).externalInvoking(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3://支付页面
                switch (type) {
                    case "stopTimer"://暂停页面关闭计时
                        mHandler.removeCallbacks(runnable);
                        break;
                    case "startTimer"://开启页面计时
                        mHandler.postDelayed(runnable, 1000);
                        break;
                    case "dismiss"://免费 关闭当前页面
                        dismiss();
                        break;
                    default:
                        viewPager.setCurrentItem(4);
                        fgVpAdapter.getItem(4).externalInvoking(type);
                }
                break;
            case 4:
                if (type.equals("dismiss")){
                    dismiss();
                }
                break;
            case 5:
                viewPager.setCurrentItem(6);
                fgVpAdapter.getItem(6).externalInvoking(type);
                break;
            case 6:
                if ( "dismiss".equals(type))
                    dismiss();
                break;
        }
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    //生命周期监听
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof MainActivity) {
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
        if (mHandler != null) mHandler.removeCallbacks(runnable);
        MyApplication.getApplication().unregisterActivityLifecycleCallbacks(this);
    }
}
