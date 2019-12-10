package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.bean.CodeOvertimerBean;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 超时弹窗
 */
public class OverTimeDialog extends BaseDialog implements BaseDialog.OnDissmissListener {

    public static OverTimeDialog newInstance(String orderId) {
        OverTimeDialog dialog = new OverTimeDialog(orderId);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFull", false);
        dialog.setArguments(bundle);
        return dialog;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mHanlder.removeCallbacks(runnable);
                mHanlder.removeMessages(101);
                mHanlder = null;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101://
                    overTimeQuery();
                    break;
                case 102:
                    tvTitle.setVisibility(View.GONE);
                    tvSuccess.setVisibility(View.VISIBLE);
                    ivQrCode.setImageDrawable(ContextCompat.getDrawable(MyApplication.getApplication(), R.mipmap.icon_duihao));
                    break;
            }
        }
    };
    private Runnable runnable = this::dismiss;

    private String orderId;//订单id
    private ImageView ivQrCode;
    private TextView tvTitle;
    private TextView tvSuccess;

    public OverTimeDialog(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_overtime;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        holder.getView(R.id.ivDismiss).setOnClickListener(v -> dismiss());
        ivQrCode = holder.getView(R.id.ivQrCode);
        tvTitle = holder.getView(R.id.tvTitle);
        tvSuccess = holder.getView(R.id.tvSuccess);
        setOnDissmissListener(this);
        mHanlder.postDelayed(runnable, 30 * 1000);
        overTime();
    }

    @Override
    public void onDismiss() {
        handler.sendEmptyMessage(1);
    }

    /**
     * 取件码超时
     */
    private void overTime() {
        ApiUtils.overTime(DeviceInfo.getIntence().register_key(), orderId, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                CodeOvertimerBean bean = new Gson().fromJson(str, CodeOvertimerBean.class);
                if (bean.getStatus() == 0) {
                    Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(bean.getResult().getQrcode(),
                            DensityUtil.dp2px(MyApplication.getApplication(), 450), DensityUtil.dp2px(MyApplication.getApplication(), 450));
                    ivQrCode.setImageBitmap(bitmap);
                    tvTitle.setText(String.format("存件超时，需支付超时费用￥%s元", bean.getResult().getFee() / 100));
                    if (mHanlder != null)
                        mHanlder.sendEmptyMessageDelayed(101, 3000);
                } else {
                    Intent intent = new Intent(getActivity(), NetWorkLoseActivity.class);
                    startActivity(intent);
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
    private void overTimeQuery() {
        ApiUtils.overTimeQuery(orderId, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        JSONObject result = object.optJSONObject("result");
                        if (result.optInt("paid") == 1) {//已经支付
                            if (mHanlder != null)
                                mHanlder.sendEmptyMessage(102);
                        } else {
                            if (mHanlder != null)
                                mHanlder.sendEmptyMessageDelayed(101, 3000);
                        }
                    } else {
                        if (mHanlder != null)
                            mHanlder.sendEmptyMessageDelayed(101, 3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mHanlder != null)
                        mHanlder.sendEmptyMessageDelayed(101, 3000);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                //只要失败就进入网络重连页面
                dismiss();
                Intent intent = new Intent(getActivity(), NetWorkLoseActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFinish() {

            }
        });
    }

}
