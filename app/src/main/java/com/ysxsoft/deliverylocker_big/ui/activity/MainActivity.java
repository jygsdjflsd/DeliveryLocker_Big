package com.ysxsoft.deliverylocker_big.ui.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
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
    @BindView(R.id.tvMiddle)
    TextView tvMiddle;
    @BindView(R.id.ivGet)
    ImageView ivGet;
    @BindView(R.id.ivThrow)
    ImageView ivThrow;
    @BindView(R.id.ivZancun)
    ImageView ivZancun;

    private int bottomSize;//底部视图高度

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
        SophixManager.getInstance().queryAndLoadNewPatch();//热更新检查更新

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

        upDateErrorLog();
    }

    /**
     * 初始化ui
     */
    private void initUi(){
        NetWorkUtil.getPhoneState(this, size -> tvNetWork.setText(String.format("4G/%s", size)));
        tvTop.setText(String.format("%s%s\u3000客服电话：%s", DeviceInfo.getIntence().getProperty(), DeviceInfo.getIntence().getTag(), DeviceInfo.getIntence().getService_tel()));
//        GlideUtils.setBackgroud(ivLogo, DeviceInfo.getIntence().getLogo());
        GlideUtils.setBackgroud(ivLogo, R.mipmap.icon_logo_top);
    }

    @OnClick({R.id.ivGet, R.id.ivThrow, R.id.ivZancun, R.id.tvNetWork})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivGet:
                new PickUpDialog(bottomSize).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
                break;
            case R.id.ivThrow:
                new ThrowDialog(bottomSize).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
                break;
            case R.id.ivZancun:
//                ToastUtils.show("功能完善中，敬请期待...");
                new TSDialog(bottomSize).setShowBottom(true).setDimAmout(0.0f).show(getSupportFragmentManager());
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
        List<String> list = new ArrayList<>();
        for (DeviceBean.ResultBean.AdsBean bannerBean : bannerList) {
            list.add(bannerBean.getUrl());
        }
        banner.update(list);
    }

    private void initBanner(List<DeviceBean.ResultBean.AdsBean> adsBean) {
        List<String> list = new ArrayList<>();
        for (DeviceBean.ResultBean.AdsBean bannerBean : adsBean) {
            list.add(bannerBean.getUrl());
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
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }



    /**
     * 上传错误日志
     */
    private void upDateErrorLog(){
        String error_log = ACacheHelper.getString(CrashHandler.ERROR_LOG, "");

        Log.e("error_log", "log: "+ error_log);
        if (TextUtils.isEmpty(error_log)){
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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        EventBus.getDefault().unregister(this);
    }
}
