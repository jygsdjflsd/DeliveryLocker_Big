package com.ysxsoft.deliverylocker_big.api;

public class Url {


    public static final String BASE_URL = "https://iot.modoubox.com/";
//    public static final String BASE_URL = "https://iot.dev.modoubox.com/";//测试服

    public static final String BASE_URL_BIG = "http://xintian.modoubox.com/";//正式服
//    public static final String BASE_URL_BIG = "http://express.admin.modoubox.com/";//测试服
    /**-
     * 工具 ================================================================================
     */
    static final String APP_FACILITY_CANUSE = BASE_URL + "api/control_app/status";//设备注册&获取设备信息

    static final String APP_QRCODE = BASE_URL + "web_wechat/deliver/qrcode";//获取二维码
    static final String APP_TAKECODE = BASE_URL + "cabinet/open_door";//取件码开门
    static final String APP_NET_ONLINE = BASE_URL + "cabinet/tcp_online_check";//假死检测
    static final String APP_ERROR_LOG= BASE_URL + "api/error_report";//错误日志上报


    static final String APP_UPDATA_APP= BASE_URL_BIG + "api_cabinet/base/checkVersion";//更新

    //暂存-发送手机验证码
    static final String APP_SEND_CODE= BASE_URL_BIG + "api_cabinet/base/sendVerify";
    //暂存-手机号码验证-获取临时token
    static final String APP_CODE_TOKEN= BASE_URL_BIG + "api_cabinet/base/getMomentaryToken";
    //暂存-获取当前网点的暂存物品收费，各类型空闲格子数量
    static final String APP_DOOR_PRICE= BASE_URL_BIG + "api_cabinet/company/getDoorPrice";
    //暂存-提前计算价格
    static final String APP_COMPUTEORDERFEE= BASE_URL_BIG + "api_cabinet/order/computeOrderFee";
    //暂存-暂存物品下单，获取支付二维码
    static final String APP_GET_PAYCODE= BASE_URL_BIG + "api_cabinet/order/addOrder";
    //查询支付结果
    static final String APP_CHECKPAID= BASE_URL_BIG + "api_cabinet/order/checkPaid";


    //二维码取件
    static final String APP_PICKBYCODE= BASE_URL_BIG + "api_cabinet/order/pickByCode";
    //查询超时订单支付状态
    static final String APP_PICKBYCODE_OUTTIME= BASE_URL_BIG + "api_cabinet/order/checkSupplementalPaid";
    static final String APP_QUERYORDER = BASE_URL_BIG + "api_cabinet/order/checkPickCode";
    static final String APP_OVERTIME = BASE_URL_BIG + "api_cabinet/Deliverorder/getOvertimeQrcode";
    static final String APP_OVERTIMEQUERY = BASE_URL_BIG + "api_cabinet/Deliverorder/checkSupplementalPaid";

}
