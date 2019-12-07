package com.ysxsoft.deliverylocker_big.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.OnlineBean;
import com.ysxsoft.deliverylocker_big.bus.NetWorkBus;
import com.ysxsoft.deliverylocker_big.bus.TcpServerBus;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.quiescent.KeySet;
import com.ysxsoft.deliverylocker_big.receiver.ReceiverOrders;
import com.ysxsoft.deliverylocker_big.tcp.SocketClient;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.SystemUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;
import com.ysxsoft.deliverylocker_big.utils.UpdataAPP;
import com.ysxsoft.deliverylocker_big.utils.cache.ACacheHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

public class TimerService extends Service {

    private final String TAG = "TimerService";

    private Handler mHandler;
    private Runnable dogRunnable; //看门狗
    private Runnable soketRunnable; // 长连接
    private Runnable onLineRunnable; // 长连接检测
    private Runnable serverRunnable;//服务端长链接检测

    private Runnable qrCodeRunnable; // 二维码更新
    private Runnable deviceInfoRunnable;// 广告获取
    private Runnable updataAppRunnable;// 更新（每小时检查一次）

    private long serverTimer = 0;//服务端长链接检测

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initHandler();

    }

    private void initHandler() {
        mHandler = new Handler();
        dogRunnable = () -> { //看门狗10秒
            ReceiverOrders.feedDog();//喂养看门狗
            if (!SocketClient.isConnectioned()){//如果长链接未连接直接进入断网逻辑
                Intent intent = new Intent(MyApplication.getApplication(), NetWorkLoseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            mHandler.postDelayed(dogRunnable, 5 * 1000);
        };
        soketRunnable = () -> {//长链接心跳 没1分钟检测一次
            if (SocketClient.isConnectioned()) {// 长链接-连接中
                SocketClient.sendMsg("cabinet_heartbeat");
            }
            mHandler.postDelayed(soketRunnable, 60 * 1000);
        };
        onLineRunnable = () -> {//长链接检测每1分钟检测一次(合并网络检测逻辑)
            Log.e(TAG, "onLineRunnable: running");
            netOnline();
            mHandler.postDelayed(onLineRunnable, 60 * 1000);
        };
        serverRunnable = () -> {//服务器端检测每30s检测一次 超时进入网络检测逻辑
            Log.e(TAG, "serverRunnable: running");
            if (serverTimer > 0 && System.currentTimeMillis() - serverTimer > 40 * 1000) {//服务器假死 进入断网逻辑
                Intent intent = new Intent(this, NetWorkLoseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            mHandler.postDelayed(serverRunnable, 30 * 1000);
        };
        deviceInfoRunnable = () -> {//广告获取 每一小时1次
            DeviceInfo.getIntence().refreshDeviceInfo();
            mHandler.postDelayed(deviceInfoRunnable, 60 * 60 * 1000);
        };
        qrCodeRunnable = () -> {//二维码获取 每10分钟刷新1次
            upDateQrCode();
            mHandler.postDelayed(qrCodeRunnable, 10 * 60 * 1000);
        };
        updataAppRunnable = () -> {
            upDateAPP();
            mHandler.postDelayed(updataAppRunnable, 60 * 60 * 1000);
        };


        mHandler.post(dogRunnable);//看门狗
        mHandler.post(soketRunnable);//长链接检测
        mHandler.postDelayed(onLineRunnable, 60 * 1000);//在线检测
        mHandler.postDelayed(serverRunnable, 30 * 1000);//在线检测
        mHandler.post(deviceInfoRunnable);//更新广告
        mHandler.post(updataAppRunnable);//更新app
        mHandler.post(qrCodeRunnable);//更新二维码
    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    /**
     * 网络超时检测
     *
     * @param bus
     */
    @Subscribe
    public void startNetWorkDetection(NetWorkBus bus) {
        if (bus.isNetIsLink()) {//网络已连接
            mHandler.postDelayed(onLineRunnable, 60 * 1000);
            mHandler.postDelayed(serverRunnable, 30 * 1000);
        } else {
            mHandler.removeCallbacks(onLineRunnable);//停止ping网络
            mHandler.removeCallbacks(serverRunnable);//停止服务端心跳检测
            serverTimer = 0;
        }
    }

    /**
     * 检测长链接
     */
    private void netOnline() {
        ApiUtils.netOnline(DeviceInfo.getIntence().register_key(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                OnlineBean bean = new Gson().fromJson(str, OnlineBean.class);
                if (0 == bean.getStatus()) {
                    if (System.currentTimeMillis() - bean.getResult().getLast_heartbeat() > 120 * 1000) {
                        //长链接断开超过120秒，重启长链接
                        if (SocketClient.isConnectioned())
                            SocketClient.socketClose();//关闭长链接重新开启
                        SocketClient.socketMain(DeviceInfo.getIntence().register_key());
                    }
                } else {
                    //接口请求失败
                    if (SocketClient.isConnectioned())//长链接已断开
                        SocketClient.socketClose();//关闭长链接重新开启
                    SocketClient.socketMain(DeviceInfo.getIntence().register_key());
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                //接口请求失败
                Intent intent = new Intent(MyApplication.getApplication(), NetWorkLoseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 服务端心跳
     */
    @Subscribe
    public void serverOnline(TcpServerBus bus) {
        serverTimer = bus.getTimeStamp();
        Log.e(TAG, "timer:" + serverTimer);
    }

    /**
     * 更新二维码
     */
    private void upDateQrCode() {
        ApiUtils.getQrCode(DeviceInfo.getIntence().register_key(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        ACacheHelper.putString(KeySet.KEY_QRCODE, object.optString("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                //只要失败就进入网络重连页面
                Intent intent = new Intent(MyApplication.getApplication(), NetWorkLoseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 更新app
     */
    private void upDateAPP() {
        String register_key = DeviceInfo.getIntence().register_key();
        if (register_key == null) {
            new Handler().postDelayed(this::upDateAPP, 5000);
            return;
        }
        Log.e("TimerService", "resterkey = " + register_key);
        ApiUtils.updataApp(register_key, SystemUtil.getVerName(MyApplication.getApplication()), String.valueOf(SystemUtil.getVersionCode(MyApplication.getApplication())), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        new UpdataAPP(MyApplication.getApplication()).updateAPP(object.getString("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
