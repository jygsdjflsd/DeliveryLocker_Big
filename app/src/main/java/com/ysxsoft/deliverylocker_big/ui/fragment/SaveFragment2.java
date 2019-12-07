package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.utils.TimerUtils;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码页面
 */
public class SaveFragment2 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static SaveFragment2 getInstance(int type, FragmentListener listener) {
        SaveFragment2 fragment = new SaveFragment2(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @BindView(R.id.editTel)
    EditText editTel;
    @BindView(R.id.editCode)
    EditText editCode;
    @BindView(R.id.btnCode)
    Button btnCode;
    @BindView(R.id.btnNext)
    Button btnNext;


    private FragmentListener listener;
    private CountDownTimer timer;
    private int type;
    private boolean isInit;

    private SaveFragment2(FragmentListener listener) {
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
        if (isInit && !isVisibleToUser) {
            //初始化数据
            if (timer != null) timer.cancel();
            timer = null;
            editTel.setText("");
            editCode.setText("");
            btnCode.setClickable(true);
            btnCode.setText("获取验证码");
            btnCode.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            btnCode.setBackgroundResource(R.color.colorMaster);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_2;
    }

    @Override
    protected void initView() {
        isInit = true;
    }

    @OnClick({R.id.btnCode, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCode:
                sendTelCode();
                break;
            case R.id.btnNext:
                getCodeToken();
                break;
        }
    }

    @Override
    public void externalInvoking(String type) {

    }

    /**
     * 发送手机验证码
     */
    private void sendTelCode() {
        String mobile = editTel.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show("请输入手机号！！");
            return;
        }
        btnCode.setClickable(false);
        ApiUtils.sendTelCode(mobile, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        getCode();
                    } else {
                        btnCode.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnCode.setClickable(true);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                btnCode.setClickable(true);
                //只要失败就进入网络重连页面
                Intent intent = new Intent(getActivity(), NetWorkLoseActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 获取临时token
     */
    private void getCodeToken() {
        String mobile = editTel.getText().toString();
        String code = editCode.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.show("请输入手机号！！");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show("请输入验证码！！");
            return;
        }
        ApiUtils.getCodeToken(mobile, code, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        if (listener != null) {
                            if (timer != null) timer.cancel();
                            timer = null;
                            listener.setToken(object.optString("result"));
                            listener.doSomething(type, "");
                        }
                    } else {
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

            }
        });
    }

    /**
     * 验证码倒计时
     */
    private void getCode() {
        btnCode.setBackgroundResource(R.color.colorEBEBEB);
        btnCode.setTextColor(ContextCompat.getColor(mContext, R.color.color282828));
        if (timer != null) timer.cancel();
        timer = null;
        timer = TimerUtils.countDown(60 * 1000, 1000, new TimerUtils.CountDownTimerListener() {
            @Override
            public void onTick(long l) {
                btnCode.setText(String.format("%sS", l));
            }

            @Override
            public void onFinish() {
                btnCode.setClickable(true);
                btnCode.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                btnCode.setBackgroundResource(R.color.colorMaster);
                btnCode.setText("获取验证码");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        timer = null;
    }
}
