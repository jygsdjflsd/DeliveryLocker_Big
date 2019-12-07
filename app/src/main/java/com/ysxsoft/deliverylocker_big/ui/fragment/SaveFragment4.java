package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.PayCodeBean;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.ui.dialog.PickerDialog;
import com.ysxsoft.deliverylocker_big.utils.MyMath;
import com.ysxsoft.deliverylocker_big.utils.TelUtils;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码页面
 */
public class SaveFragment4 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static SaveFragment4 getInstance(int type, FragmentListener listener) {
        SaveFragment4 fragment = new SaveFragment4(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.editTel)
    EditText editTel;
    @BindView(R.id.editTime)
    TextView editTime;
    @BindView(R.id.tvOrderDown)
    TextView tvOrderDown;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.relLoading)
    RelativeLayout relLoading;

    private int type;
    private FragmentListener listener;

    private String flag;//箱子类型
    private String token;//
    private int hours;

    private SaveFragment4(FragmentListener listener) {
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_4;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.editTime, R.id.tvOrderDown})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editTime:
                if (listener != null) {
                    listener.doSomething(type, "stopTimer");
                }
                new PickerDialog((time, text) -> {
                    hours = time;
                    computeOrderFee();
                    editTime.setText(text);
                })
                        .setShowBottom(true)
                        .setDimAmout(0.5f)
                        .setOnDissmissListener(() -> {
                            listener.doSomething(type, "startTimer");
                        })
                        .show(getChildFragmentManager());
                break;
            case R.id.tvOrderDown:
                getPayCode();
                break;
        }
    }

    @Override
    public void externalInvoking(String type) {
        editTel.setText("");
        editTime.setText("");
        tvMoney.setText("");
        try {
            JSONObject object = new JSONObject(type);
            flag = object.optString("type", null);
            token = object.optString("token", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂存物品下单，获取支付二维码
     */
    private void getPayCode() {
        String mobile = editTel.getText().toString();
        if (!TelUtils.isPhoneNumb(mobile)) {
            ToastUtils.show("请填写取件手机号！！");
            return;
        }
        relLoading.setVisibility(View.VISIBLE);
        ApiUtils.getPayCode(DeviceInfo.getIntence().register_key(), token, flag, mobile, String.valueOf(hours), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                PayCodeBean bean = new Gson().fromJson(str, PayCodeBean.class);
                if (bean.getStatus() == 0) {
                    if (listener != null) {
                        if (bean.getResult().getQrcode() == null || TextUtils.isEmpty(bean.getResult().getQrcode())) {
                            //免费
                            listener.doSomething(type, "dismiss");
                            return;
                        }
                        listener.doSomething(type, str);
                    }
                } else {
                    ToastUtils.show("获取支付二维码失败，请查看手机号是否填写正确！！");
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
     * 获取价格
     */
    private void computeOrderFee() {
        Log.e("computeOrderFee", "时间：" + hours);
        ApiUtils.computeOrderFee(DeviceInfo.getIntence().register_key(), flag, hours, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        tvMoney.setText(String.format("￥%s", object.optDouble("result") / 100));
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

}
