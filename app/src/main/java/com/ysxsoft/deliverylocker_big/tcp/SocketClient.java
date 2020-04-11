package com.ysxsoft.deliverylocker_big.tcp;

import android.text.TextUtils;
import android.util.Log;


import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bus.OverTimeBus;
import com.ysxsoft.deliverylocker_big.bus.TcpServerBus;
import com.ysxsoft.deliverylocker_big.utils.SerialPortUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Create By 胡
 * on 2019/11/8 0008
 */
public class SocketClient {

    public static final String TCP_HOST = "iot.tcp.modoubox.com";
//    public static final String TCP_HOST = "iot.dev.modoubox.com";//测试
    public static final int TCP_PORT = 8091;
    private static Socket socket;
    private static InetAddress address;
    private static InputStream is;
    private static OutputStream os;
    private static PrintWriter pw;
    private static BufferedReader br;

    private static String receiverStr = "";

    public static void socketMain(String register_key) {
        Log.e("socketMain", "register_key = " + register_key);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    address = InetAddress.getByName(TCP_HOST);
                    //和服务器创建连接
                    socket = new Socket();
                    socket.setKeepAlive(true);
                    socket.connect(new InetSocketAddress(address.getHostAddress().toString(), TCP_PORT));
                    try {
                        socket.sendUrgentData(0);
                    } catch (Exception e) {
                        socket.connect(new InetSocketAddress(address.getHostAddress().toString(), TCP_PORT));
                    }
                    //发送给服务器的信息
                    os = socket.getOutputStream();
                    pw = new PrintWriter(os);
                    sendMsg(register_key);

                    //从服务器获取信息
                    while (socket.isConnected()) {
                        is = socket.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        char[] buffer = new char[1024];
                        int len;
                        while ((len = br.read(buffer, 0, buffer.length)) > 0) {
                            String text = "";
                            for (int i = 0; i < len; i++) {
                                text = String.format("%s%s", text, buffer[i]);
                            }
                            JSONObject object = new JSONObject(text);
                            if (object.optString("type").equals("stc:heartbeat")){//服务器发来的心跳
                                EventBus.getDefault().post(new TcpServerBus(object.optLong("time")));
                            }else if (object.optString("type").equals("stc:overtime_pay")){//取件码超时
                                EventBus.getDefault().post(new OverTimeBus(object.optString("captcha_id")));
                            }else {
                                SerialPortUtil.addOrderList(text);
                            }
                            Log.e("socketMain", "orderResult=====>>>>" + text);
                        }
                    }
                    Log.e("socket", "close");
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                    if (pw != null)
                        pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException ignored){
                    ignored.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 向服务器发送信息
     *
     * @param msg
     */
    public static void sendMsg(String msg) {
        if (msg != null && !TextUtils.isEmpty(msg)) {
            if (pw != null) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        pw.write(msg);
                        pw.flush();
                    }
                }.start();
            }else {//重连tcp
                socketClose();
                socketMain(DeviceInfo.getIntence().register_key());
            }
        }
    }

    /**
     * 查看长链接是否链接
     *
     * @return
     */
    public static boolean isConnectioned() {
        try {
            socket.sendUrgentData(0);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 断开连接
     */
    public static void socketClose() {
        try {
            if (socket != null)
                socket.close();
            Log.e("socketMain", "socket=====断开");
            if (is != null)
                is.close();
            if (os != null)
                os.close();
            if (pw != null)
                pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("socketMain", "socket=====断开失败");
        }
    }
}
