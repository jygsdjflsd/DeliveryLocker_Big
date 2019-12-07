package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.CodeOvertimerBean;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.TakeCodeBean;
import com.ysxsoft.deliverylocker_big.bus.OverTimeBus;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeErrorDialog;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeSuccessDialog;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.widget.password.PasswordView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 数字码页面
 */
public class DialogNumbCodeFragment extends BaseFragment {


    public static DialogNumbCodeFragment getInstance() {
        return new DialogNumbCodeFragment();
    }

    @BindView(R.id.pwdView)
    PasswordView pwdView;
    @BindView(R.id.overTimerLayout)
    ConstraintLayout overTimerLayout;
    @BindView(R.id.ivQrCode)
    ImageView ivQrCode;

    private boolean isInit;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                overTimeQuery(msg.obj.toString());
            } else if (msg.what == 102) {
                ToastUtils.show("正在开门请稍后");
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialog_numbercode;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        pwdView.setListener(this::takeCode);
        isInit = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isInit) {
            pwdView.clearInput();
        }
    }

    /**
     * 取件码取件（取件页面）
     */
    private void takeCode(String numb) {
        ApiUtils.takeCode(numb, DeviceInfo.getIntence().register_key(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                TakeCodeBean bean = new Gson().fromJson(str, TakeCodeBean.class);
                if (bean.getStatus() == 0) {//success
                    TakeCodeSuccessDialog
                            .newInstance(bean.getResult().getDoor_number())
                            .setSize(DensityUtil.dp2px(mContext, 400), DensityUtil.dp2px(mContext, 205))
                            .show(getChildFragmentManager());
                } else if (bean.getStatus() == 2) {//快件超时

                } else {
                    TakeCodeErrorDialog.newInstance()
                            .setSize(DensityUtil.dp2px(mContext, 400), DensityUtil.dp2px(mContext, 270))
                            .show(getChildFragmentManager());
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
                pwdView.recoverKeybordClickEnable();
            }
        });
    }


    /**
     * 超时订单
     *
     * @param bus
     */
    @Subscribe
    public void overTime(OverTimeBus bus) {
        overTime(bus.getOrderid());
    }

    /**
     * 取件码超时
     */
    private void overTime(String id) {
        ApiUtils.overTime(DeviceInfo.getIntence().register_key(), id, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                CodeOvertimerBean bean = new Gson().fromJson(str, CodeOvertimerBean.class);
                if (bean.getStatus() == 0) {
                    pwdView.setVisibility(View.GONE);
                    overTimerLayout.setVisibility(View.VISIBLE);
                    Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(bean.getResult().getQrcode(),
                            DensityUtil.dp2px(mContext, 450), DensityUtil.dp2px(mContext, 450));
                    ivQrCode.setImageBitmap(bitmap);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(101, id), 5000);
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
     * 取件码超时
     */
    private void overTimeQuery(String id) {
        ApiUtils.overTimeQuery(id, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        JSONObject result = object.optJSONObject("result");
                        if (result.optInt("paid") == 1) {//已经支付
                            mHandler.sendEmptyMessage(102);
                        } else {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(101, id), 3000);
                        }
                    } else {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(101, id), 3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(101, id), 3000);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        if (mHandler != null) mHandler.removeMessages(101);
    }
}
