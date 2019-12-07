package com.ysxsoft.deliverylocker_big.tcp;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkHttpSocketClient {

    private static final String TAG = "OkHttpSocketClient";
    private static WebSocket mSocket;

    public static void start() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .retryOnConnectionFailure(true)
                .build();

        new Thread(){
            @Override
            public void run() {
                super.run();
//                    String url = InetAddress.getByName("iot.tcp.modoubox.com").getHostAddress() + ":8091";
                    Request request = new Request.Builder().url("https://iot.tcp.modoubox.com:8091?"+ DeviceInfo.getIntence().register_key()).build();

                    EchoWebSocketListener socketListener = new EchoWebSocketListener();
                    mOkHttpClient.newWebSocket(request, socketListener);
                    mOkHttpClient.dispatcher().executorService().shutdown();
            }
        }.start();
    }



    private static final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
            //连接成功后，发送登录信息
//            mSocket.send(DeviceInfo.getIntence().register_key());
            output("连接成功！");

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            output("receive bytes:" + bytes.hex());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            output("receive text:" + text);
            //收到服务器端发送来的信息后，每隔25秒发送一次心跳包
            final String message = "{\"type\":\"heartbeat\",\"user_id\":\"heartbeat\"}";
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mSocket.send(message);
                }
            },25000);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            output("closed:" + reason);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            output("closing:" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            output("failure:" + t.getMessage());
        }
    }

    private static void output(final String text) {
        new Handler(Looper.getMainLooper()).post(()->{
            Log.e(TAG, text);
            ToastUtils.show(text);
        });
    }
}
