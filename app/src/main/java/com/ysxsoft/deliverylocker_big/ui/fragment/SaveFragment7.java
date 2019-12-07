package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeSuccessDialog;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 二维码页面
 */
public class SaveFragment7 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static SaveFragment7 getInstance(int type, FragmentListener listener) {
        SaveFragment7 fragment = new SaveFragment7(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.ivGet)
    ImageView ivGet;
    @BindView(R.id.ivThrow)
    ImageView ivThrow;
    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.ivQrCode)
    ImageView ivQrCode;
    @BindView(R.id.layout2)
    ConstraintLayout layout2;
    @BindView(R.id.relLoading)
    RelativeLayout relLoading;

    private int type;
    private boolean isInit;
    private String number;
    private FragmentListener listener;
    private String orderCode;//订单编号

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101){
                mHanlder.removeCallbacks(runnable);
            }
        }
    };
    private Handler mHanlder = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pickByCode();
            mHanlder.postDelayed(runnable, 5000);
        }
    };

    private SaveFragment7(FragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        type = getArguments() != null ? getArguments().getInt(INTENT_TYPE) : type;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isInit){
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_7;
    }

    @Override
    protected void initView() {
        ivGet.setOnClickListener(v -> {//临时
            takeCodeNumb("1");
        });
        ivThrow.setOnClickListener(v -> {//结束
            takeCodeNumb("2");
        });
        isInit = true;
    }

    @Override
    public void externalInvoking(String type) {
        number = type;

    }

    /**
     * 取件码开门
     *
     * @param todo
     */
    private void takeCodeNumb(String todo) {
        relLoading.setVisibility(View.VISIBLE);
        Log.e("取件码开门", String.valueOf(number));
        ApiUtils.pickByCode(DeviceInfo.getIntence().register_key(), number, todo, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0){
                        JSONObject obj = object.optJSONObject("result");
                        if (obj.optInt("state") == 0){//未超时 正常开门

                        }else if (obj.optInt("state") == 1){//已超时 显示超时二维码
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.VISIBLE);
                            Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(obj.optString("qrcode"),
                                    DensityUtil.dp2px(mContext, 450), DensityUtil.dp2px(mContext, 450));
                            ivQrCode.setImageBitmap(bitmap);
                            orderCode = obj.optString("order_code");
                            mHanlder.post(runnable);//开启订单支付查询
                        }else {

                        }
                    }else {
                        ToastUtils.show(object.optString("msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                //只要失败就进入网络重连页面
                Intent intent = new Intent(getActivity(), NetWorkLoseActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFinish() {
                relLoading.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 查询超时订单支付状态
     */
    private void pickByCode(){
        ApiUtils.pickByCode(DeviceInfo.getIntence().register_key(), orderCode == null ? "" : orderCode, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0 ){
                        JSONObject object1 = object.optJSONObject("result");
                        if (object1.optInt("paid") == 1){//支付成功
                            TakeCodeSuccessDialog
                                    .newInstance(object1.optString("door_number"))
                                    .setSize(DensityUtil.dp2px(mContext, 400), DensityUtil.dp2px(mContext, 205))
                                    .setOnDissmissListener(()->{
                                        if (listener != null ){
                                            listener.doSomething(type, "dismiss");
                                        }
                                    })
                                    .show(getChildFragmentManager());
                            handler.sendEmptyMessage(101);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHanlder != null ) mHanlder.removeCallbacks(runnable);
    }
}
