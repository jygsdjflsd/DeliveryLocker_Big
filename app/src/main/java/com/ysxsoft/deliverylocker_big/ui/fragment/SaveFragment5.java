package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.PayCodeBean;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.ui.dialog.PickerDialog;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeSuccessDialog;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.MyMath;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.TelUtils;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码页面
 */
public class SaveFragment5 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static SaveFragment5 getInstance(int type, FragmentListener listener) {
        SaveFragment5 fragment = new SaveFragment5(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.ivQrCode)
    ImageView ivQrCode;
    @BindView(R.id.layoutRight)
    ConstraintLayout layoutRight;

    private int type;
    private FragmentListener listener;
    private PayCodeBean payCodeBean;
    private final int HANDLER_CLOSE = 101;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLER_CLOSE) {
                mHandler.removeCallbacks(runnable);
            }
        }
    };
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkPayType();
            mHandler.postDelayed(runnable, 5000);
        }
    };

    private SaveFragment5(FragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        type = getArguments() != null ? getArguments().getInt(INTENT_TYPE) : type;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_5;
    }

    @Override
    protected void initView() {
    }

    @Override
    public void externalInvoking(String type) {
        payCodeBean = new Gson().fromJson(type, PayCodeBean.class);
        Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(payCodeBean.getResult().getQrcode(),
                DensityUtil.dp2px(mContext, 450), DensityUtil.dp2px(mContext, 450));
        ivQrCode.setImageBitmap(bitmap);
        mHandler.postDelayed(runnable, 5000);
    }

    /**
     * 查询支付结果
     */
    private void checkPayType() {
        ApiUtils.getPayType(payCodeBean.getResult().getOrder_code(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                PayTypeBean bean = new Gson().fromJson(str, PayTypeBean.class);
                if (bean.getStatus() == 0) {
                    if (bean.getResult().getPaid() == 1) {
                        //支付完成
                        handler.sendEmptyMessage(HANDLER_CLOSE);
                        TakeCodeSuccessDialog
                                .newInstance(String.valueOf(bean.getResult().getDoor_number()), ()->{
                                    if (listener != null) {
                                        listener.doSomething(type, "dismiss");
                                    }
                                })
                                .setSize(DensityUtil.dp2px(mContext, 400), DensityUtil.dp2px(mContext, 205))
                                .show(getChildFragmentManager());
                    }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null && runnable != null) mHandler.removeCallbacks(runnable);
    }

    class PayTypeBean {

        /**
         * status : 0
         * msg : 订单支付成功，取件码将以短信的形式发送至取件人手机。
         * result : {"paid":1}
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

        public class ResultBean {
            /**
             * paid : 1
             */

            private int paid;
            private int door_number;
            private String fee;

            public int getPaid() {
                return paid;
            }

            public void setPaid(int paid) {
                this.paid = paid;
            }

            public int getDoor_number() {
                return door_number;
            }

            public void setDoor_number(int door_number) {
                this.door_number = door_number;
            }

            public String getFee() {
                return fee == null ? "" : fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }
        }
    }
}
