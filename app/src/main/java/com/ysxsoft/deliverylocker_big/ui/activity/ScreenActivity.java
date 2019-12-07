package com.ysxsoft.deliverylocker_big.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceBean;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.receiver.ReceiverOrders;
import com.ysxsoft.deliverylocker_big.service.DaemonService;
import com.ysxsoft.deliverylocker_big.service.MySystemService;
import com.ysxsoft.deliverylocker_big.utils.MD5Util;
import com.ysxsoft.deliverylocker_big.utils.PingUtil;
import com.ysxsoft.deliverylocker_big.utils.SystemAudioUtil;
import com.ysxsoft.deliverylocker_big.utils.SystemLumUtil;
import com.ysxsoft.deliverylocker_big.utils.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

public class ScreenActivity extends BaseActivity {

    private final int HANDLER_NETWORK_ON = 101;//网络畅通
    private final int HANDLER_NETWORK_OFF = 102;//网络不通
    private int pingTime;//重试次数

    @BindView(R.id.tvTop)
    TextView tvTop;
    @BindView(R.id.tvBottom)
    TextView tvBottom;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_NETWORK_ON://网络畅通
                    getService();//获取设备信息
                    break;
                case HANDLER_NETWORK_OFF://网络不通
                    ReceiverOrders.restartSystem();
                    Log.e("runnable", "pingTime = " + pingTime + "网络不通，重启");
                    break;
            }
        }
    };

    private Runnable serviceRunnable = this::getService;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (PingUtil.isNetworkOnline(String.valueOf(System.currentTimeMillis()))) {//网络通
                        mHandler.sendEmptyMessage(HANDLER_NETWORK_ON);
                    } else {//网络不通
                        pingTime++;
                        if (pingTime == 10) {
                            mHandler.sendEmptyMessage(HANDLER_NETWORK_OFF);
                            return;
                        }
                        mHandler.postDelayed(this, 5000);//重新屏
                        Log.e("runnable", "pingTime = " + pingTime);
                    }
                }
            }.start();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen;
    }

    @Override
    protected void initView() {
        systemSetting();
        startService(new Intent(mContext, DaemonService.class));
        mHandler.post(runnable);//查询网络
        ReceiverOrders.hideNavigation();//隐藏导航栏
    }

    /**
     * 设置
     */
    private void systemSetting() {
        SystemAudioUtil.getInstance(mContext).setMediaVolume(SystemAudioUtil.getInstance(mContext).getMediaMaxVolume());
        SystemAudioUtil.getInstance(mContext).setSystemVolume(SystemAudioUtil.getInstance(mContext).getSystemMaxVolume());
        SystemLumUtil.setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL);
        Intent intent = new Intent(mContext, MySystemService.class);
        startService(intent);
    }

    /**
     * 获取设备注册信息
     */
    private void getService() {
//        tvTop.setText("请求网络获取设备信息");
        String device_id = MD5Util.md5Decode32(SystemUtil.getImei() + "iot");
        Log.e("device_id", device_id);
        ApiUtils.getFacility(device_id, SystemUtil.getSimId(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject obj = new JSONObject(str);
                    int status = obj.optInt("status");
                    if (status == 0) {//设备未绑定 需要注册
                        JSONObject result = obj.optJSONObject("result");
                        tvTop.setText(String.format("设备激活ID：%s", result.optString("active_code")));
                        tvBottom.setText("请在运营后台添加设备，如有疑问请联系经销商");
                        mHandler.removeCallbacks(serviceRunnable);
                        mHandler.postDelayed(serviceRunnable, 5000);//5秒检测一次
                    } else if (status == 1) {//设备不合法

                    } else if (status == 2) {//设备合法 进入取件界面
                        DeviceBean device = new Gson().fromJson(str, DeviceBean.class);
                        DeviceInfo.getIntence().setDeviceBean(device);
                        MainActivity.newIntent(mContext);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ReceiverOrders.restartSystem();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ReceiverOrders.restartSystem();
            }

            @Override
            public void onFinish() {
            }
        });
    }
}
