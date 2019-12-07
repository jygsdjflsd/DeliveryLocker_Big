package com.ysxsoft.deliverylocker_big.api;



import android.util.Log;

import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.network.OkGoUtils;

import java.util.HashMap;

public class ApiUtils {

    /**
     * 设备注册&获取设备信息
     *
     * @param device_id           imei
     * @param absPostJsonStringCb 回调
     */
    public static void getFacility(String device_id, String sim_iccid, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("device_id", device_id);
        option.params.put("sim_iccid", sim_iccid);
        option.isNormalDeal = false;
        option.url = Url.APP_FACILITY_CANUSE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 获取二维码
     *
     * @param register_key        key
     * @param absPostJsonStringCb 回调
     */
    public static void getQrCode(String register_key, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("from", "cabinet");
        option.params.put("type", "qrcode_content");
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_QRCODE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 取件码开门
     *
     * @param register_key        key
     * @param absPostJsonStringCb 回调
     */
    public static void takeCode(String code, String register_key, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("from", "code-user");
        option.params.put("type", "get_by_code");
        option.params.put("code", code);
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_TAKECODE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 长链接假死检测
     *
     * @param absPostJsonStringCb 回调
     */
    public static void netOnline(String register_key, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_NET_ONLINE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 错误上报
     *
     * @param absPostJsonStringCb 回调
     */
    public static void errorLog(String device_id, String version, String err_msg, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("device_id", device_id);
        option.params.put("version", version);
        option.params.put("err_msg", err_msg);
        option.isNormalDeal = false;
        option.url = Url.APP_ERROR_LOG;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 更新app
     *
     * @param absPostJsonStringCb 回调
     */
    public static void updataApp(String register_key, String version, String version_code, AbsPostJsonStringCb absPostJsonStringCb) {
        Log.e("uptataApp", "register_key:"+ register_key+ " version:"+ version);
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.params.put("version", version);
        option.params.put("version_code", version_code);
        option.isNormalDeal = false;
        option.url = Url.APP_UPDATA_APP;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }

    /**
     * 暂存-发送手机验证码
     *
     * @param absPostJsonStringCb 回调
     */
    public static void sendTelCode(String mobile, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("mobile", mobile);
        option.isNormalDeal = false;
        option.url = Url.APP_SEND_CODE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存-发送手机验证码
     *
     * @param absPostJsonStringCb 回调
     */
    public static void getCodeToken(String mobile, String verify, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("mobile", mobile);
        option.params.put("verify", verify);
        option.isNormalDeal = false;
        option.url = Url.APP_CODE_TOKEN;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 获取当前网点的暂存物品收费，各类型空闲格子数量
     *
     * @param absPostJsonStringCb 回调
     */
    public static void doorPrice(String register_key, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_DOOR_PRICE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 提前计算价格
     *
     * @param absPostJsonStringCb 回调
     */
    public static void computeOrderFee(String register_key, String type, int hours,AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.params.put("type", type);
        option.params.put("hours", String.valueOf(hours));
        option.isNormalDeal = false;
        option.url = Url.APP_COMPUTEORDERFEE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 暂存物品下单，获取支付二维码
     *
     * @param absPostJsonStringCb 回调
     */
    public static void getPayCode(String register_key,String token, String type, String pick_mobile, String hours, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.params.put("token", token);
        option.params.put("type", type);
        option.params.put("pick_mobile", pick_mobile);
        option.params.put("hours", hours);
        option.isNormalDeal = false;
        option.url = Url.APP_GET_PAYCODE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 查询支付结果
     *
     * @param absPostJsonStringCb 回调
     */
    public static void getPayType(String order_code, AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("order_code", order_code);
        option.isNormalDeal = false;
        option.url = Url.APP_CHECKPAID;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }


    /**
     * 暂存- 取件码取件
     * @param register_key
     * @param code
     * @param todo 1=临时开门；2=取件，结束使用，取件码失效
     * @param absPostJsonStringCb 回调
     */
    public static void pickByCode(String register_key, String code, String todo,  AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("register_key", register_key);
        option.params.put("code", code);
        option.params.put("do", todo);
        option.isNormalDeal = false;
        option.url = Url.APP_PICKBYCODE;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 查询超时订单支付状态
     * @param order_code
     * @param absPostJsonStringCb 回调
     */
    public static void pickByCode(String register_key, String order_code,  AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("order_code", order_code);
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_PICKBYCODE_OUTTIME;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 暂存- 查询订单是否可用
     * @param code
     * @param absPostJsonStringCb 回调
     */
    public static void queryOrder(String register_key, String code,  AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("code", code);
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_QUERYORDER;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }

    /**
     * 快递相关- 超时收费获取二维码
     * @param captcha_id 订单id
     * @param absPostJsonStringCb 回调
     */
    public static void overTime(String register_key, String captcha_id,  AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("captcha_id", captcha_id);
        option.params.put("register_key", register_key);
        option.isNormalDeal = false;
        option.url = Url.APP_OVERTIME;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }
    /**
     * 快递相关- 超时收费获取二维码
     * @param captcha_id 订单id
     * @param absPostJsonStringCb 回调
     */
    public static void overTimeQuery(String captcha_id,  AbsPostJsonStringCb absPostJsonStringCb) {
        OkGoUtils.RequestOption option = new OkGoUtils.RequestOption();
        option.params = new HashMap<>();
        option.params.put("captcha_id", captcha_id);
        option.isNormalDeal = false;
        option.url = Url.APP_OVERTIMEQUERY;
        option.iPostJsonStringCb = absPostJsonStringCb;
        OkGoUtils.postJsonStringCallback(option);
    }


}
