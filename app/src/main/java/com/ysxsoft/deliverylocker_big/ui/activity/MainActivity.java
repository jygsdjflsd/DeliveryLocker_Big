package com.ysxsoft.deliverylocker_big.ui.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lzy.okgo.model.Response;
import com.taobao.sophix.SophixManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceBean;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bus.DeviceRefreshBus;
import com.ysxsoft.deliverylocker_big.exceptionhandler.CrashHandler;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.network.NetWorkUtil;
import com.ysxsoft.deliverylocker_big.receiver.ReceiverOrders;
import com.ysxsoft.deliverylocker_big.service.TimerService;
import com.ysxsoft.deliverylocker_big.tcp.OkHttpSocketClient;
import com.ysxsoft.deliverylocker_big.tcp.SocketClient;
import com.ysxsoft.deliverylocker_big.ui.dialog.PickUpDialog;
import com.ysxsoft.deliverylocker_big.ui.dialog.TSDialog;
import com.ysxsoft.deliverylocker_big.ui.dialog.ThrowDialog;
import com.ysxsoft.deliverylocker_big.utils.GlideImageLoader;
import com.ysxsoft.deliverylocker_big.utils.MD5Util;
import com.ysxsoft.deliverylocker_big.utils.SerialPortUtil;
import com.ysxsoft.deliverylocker_big.utils.SystemUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;
import com.ysxsoft.deliverylocker_big.utils.TtsUtil;
import com.ysxsoft.deliverylocker_big.utils.cache.ACacheHelper;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.glide.GlideUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.tvNetWork)
    TextView tvNetWork;
    @BindView(R.id.tvTop)
    TextView tvTop;
    @BindView(R.id.layoutTop)
    ConstraintLayout layoutTop;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.bannerScreen)
    Banner bannerScreen;
    @BindView(R.id.tvMiddle)
    TextView tvMiddle;
    @BindView(R.id.ivGet)
    ImageView ivGet;
    @BindView(R.id.ivThrow)
    ImageView ivThrow;
    @BindView(R.id.ivZancun)
    ImageView ivZancun;

    private int bottomSize;//底部视图高度
    private int fillTimer;//全屏轮播图
    private int imgNumb;//全拼给轮播图个数


    private Handler mHandler;//计时器
    private Runnable runnable;//页面计时器

    private TimerService timerService;//心跳服务
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((TimerService.MyBinder) service).getService();
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
//        SophixManager.getInstance().queryAndLoadNewPatch();//热更新检查更新

        ReceiverOrders.openDog();//打开看门狗
        SocketClient.socketMain(DeviceInfo.getIntence().register_key());//开启长链接
        bindService(new Intent(mContext, TimerService.class), conn, Context.BIND_AUTO_CREATE);//绑定启动服务
        SerialPortUtil.init_receive_serial();//开启串口
    }

    @Override
    protected void initView() {
        tvMiddle.post(() -> {//获取底部高度
            bottomSize = layoutBottom.getBottom() - tvMiddle.getTop();
        });

        TtsUtil.getInstance().speak("欢迎使用心甜智能柜");
        initUi();
        initBanner(DeviceInfo.getIntence().getDeviceBean().getResult().getAds());
        initTouchTimer();
        mHandler.post(runnable);//开启页面计时

        upDateErrorLog();
    }

    private void initTouchTimer(){
        mHandler = new Handler();
        runnable = () -> {
            fillTimer++;
            Log.e("fillTimer", "timer = "+ fillTimer);
            if (fillTimer == 60 && imgNumb > 0) {
                banner.stopAutoPlay();
                bannerScreen.setVisibility(View.VISIBLE);
                bannerScreen.startAutoPlay();
            }
            mHandler.postDelayed(runnable, 1000);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillTimer = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化ui
     */
    private void initUi() {
        NetWorkUtil.getPhoneState(this, (size, text) ->
                tvNetWork.setText(String.format("%s/%s", text, size)));
        tvTop.setText(String.format("%s%s\u3000客服电话：%s", DeviceInfo.getIntence().getProperty(), DeviceInfo.getIntence().getTag(), DeviceInfo.getIntence().getService_tel()));
        if (TextUtils.isEmpty(DeviceInfo.getIntence().getLogo())){
            GlideUtils.setBackgroud(ivLogo, R.mipmap.icon_logo_top);
        }else {
            GlideUtils.setBackgroud(ivLogo, DeviceInfo.getIntence().getLogo());
        }
    }

    @OnClick({R.id.ivGet, R.id.ivThrow, R.id.ivZancun, R.id.tvNetWork})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivGet:
                mHandler.removeCallbacks(runnable);
                new PickUpDialog(bottomSize, ()-> mHandler.post(runnable)).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
                break;
            case R.id.ivThrow:
                mHandler.removeCallbacks(runnable);
                new ThrowDialog(bottomSize, ()-> mHandler.post(runnable)).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
                break;
            case R.id.ivZancun:
                mHandler.removeCallbacks(runnable);
                new TSDialog(bottomSize, ()-> mHandler.post(runnable)).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
                break;
            case R.id.tvNetWork:
                toast(String.format("当前版本%s", SystemUtil.getVerName(mContext)));
                break;
        }
    }


    /**
     * 刷新轮播图（）
     *
     * @param bus
     */
    @Subscribe
    public void refreshDevice(DeviceRefreshBus bus) {
        List<DeviceBean.ResultBean.AdsBean> bannerList = DeviceInfo.getIntence().getDeviceBean().getResult().getAds();
        List<String> listFill = new ArrayList<>();
        List<String> list = new ArrayList<>();
        imgNumb = 0;
        for (DeviceBean.ResultBean.AdsBean bannerBean : bannerList) {
            switch (bannerBean.getPosition()) {
                case "main-top-21":
                    list.add(bannerBean.getUrl());
                    break;
                case "full-screen-21":
                    imgNumb++;
                    listFill.add(bannerBean.getUrl());
                    break;
            }
        }
        runOnUiThread(() -> {
            tvTop.setText(String.format("%s%s\u3000客服电话：%s", DeviceInfo.getIntence().getProperty(), DeviceInfo.getIntence().getTag(), DeviceInfo.getIntence().getService_tel()));
            if (TextUtils.isEmpty(DeviceInfo.getIntence().getLogo())){
                GlideUtils.setBackgroud(ivLogo, R.mipmap.icon_logo_top);
            }else {
                GlideUtils.setBackgroud(ivLogo, DeviceInfo.getIntence().getLogo());
            }
            banner.update(list);
            bannerScreen.update(listFill);
            if (fillTimer >= 60 && imgNumb > 0){
                bannerScreen.setVisibility(View.VISIBLE);
            }else {
                bannerScreen.stopAutoPlay();
                bannerScreen.setVisibility(View.GONE);
            }
        });
    }

    private void initBanner(List<DeviceBean.ResultBean.AdsBean> adsBean) {
        List<String> list = new ArrayList<>();
        List<String> listFill = new ArrayList<>();
        imgNumb = 0;
        for (DeviceBean.ResultBean.AdsBean bannerBean : adsBean) {
            switch (bannerBean.getPosition()) {
                case "main-top-21":
                    list.add(bannerBean.getUrl());
                    break;
                case "full-screen-21":
                    imgNumb++;
                    listFill.add(bannerBean.getUrl());
                    break;
            }
        }
        //设置banner样式(这里有5个样式，有三个必须设置title)
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(list);
        //设置banner动画效果 FlipHorizontal(翻页)　CubeOut（立方体）CubeIn（还行与Accordion效果类似）
        banner.setBannerAnimation(Transformer.Accordion);
//        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(20 * 1000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();


        //设置banner样式(这里有5个样式，有三个必须设置title)
        bannerScreen.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        bannerScreen.setImageLoader(new GlideImageLoader());
        //设置图片集合
        bannerScreen.setImages(listFill);
        //设置banner动画效果 FlipHorizontal(翻页)　CubeOut（立方体）CubeIn（还行与Accordion效果类似）
        bannerScreen.setBannerAnimation(Transformer.Accordion);
//        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        bannerScreen.isAutoPlay(true);
        //设置轮播时间
        bannerScreen.setDelayTime(20 * 1000);
        //设置指示器位置（当banner模式中有指示器时）
        bannerScreen.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        bannerScreen.start();
    }


    /**
     * 上传错误日志
     */
    private void upDateErrorLog() {
        String error_log = ACacheHelper.getString(CrashHandler.ERROR_LOG, "");

        Log.e("error_log", "log: " + error_log);
        if (TextUtils.isEmpty(error_log)) {
            return;
        }
        String device_id = MD5Util.md5Decode32(SystemUtil.getImei() + "iot");
        ApiUtils.errorLog(device_id, SystemUtil.getVerName(mContext), error_log, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                //上传成功清空 log日志
                ACacheHelper.putString(CrashHandler.ERROR_LOG, "");
                Log.e("error_log", str);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("error_log", "error");
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (fillTimer >= 60){
                banner.startAutoPlay();
            }
            fillTimer = 0;
            bannerScreen.setVisibility(View.GONE);
            bannerScreen.stopAutoPlay();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        EventBus.getDefault().unregister(this);
    }
}
