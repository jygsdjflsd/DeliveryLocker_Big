package com.ysxsoft.deliverylocker_big.bean;

public class PayCodeBean {

    /**
     * status : 0
     * msg : 创建订单成功，请扫描二维码支付。
     * result : {"qrcode":"XXXXXXXXX","order_code":"XXXXXXXXX"}
     */

    private int status;
    private String msg;
    private ResultBean result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * qrcode : XXXXXXXXX
         * order_code : XXXXXXXXX
         */

        private String qrcode;
        private String order_code;

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getOrder_code() {
            return order_code;
        }

        public void setOrder_code(String order_code) {
            this.order_code = order_code;
        }
    }
}
