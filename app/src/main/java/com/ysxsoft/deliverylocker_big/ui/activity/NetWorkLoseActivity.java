package com.ysxsoft.deliverylocker_big.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bus.NetWorkBus;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.receiver.ReceiverOrders;
import com.ysxsoft.deliverylocker_big.tcp.SocketClient;

import org.greenrobot.eventbus.EventBus;

public class NetWorkLoseActivity extends BaseActivity {



    private static final String TAG = "NetWorkLoseActivity";

    private Handler mHandler;
    private Runnable runnable = ReceiverOrders::restartSystem;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_net_work_lose;
    }

    @Override
    protected void initView() {
        //只要进入网络检测页面 就关闭长链接 停止外部网络检测服务 开启当前页面网络检测
        SocketClient.socketClose();
        EventBus.getDefault().post(new NetWorkBus("4G", false));
        mHandler = new Handler();
        mHandler.postDelayed(runnable, 80 * 1000);
        tcpOnLine();//检测网络
    }

    /**
     * 检测网络
     */
    private void tcpOnLine(){
        ApiUtils.netOnline("", new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                Log.e(TAG, "onSuccess:str == "+ str);
                EventBus.getDefault().post(new NetWorkBus("4G", true));
                finish();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e(TAG, "onError:str == "+ response.getException().getMessage());
                new Handler().postDelayed(()-> tcpOnLine(), 5000);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        //重连
        if (SocketClient.isConnectioned())
            SocketClient.socketClose();
        SocketClient.socketMain(DeviceInfo.getIntence().register_key());
    }
}
