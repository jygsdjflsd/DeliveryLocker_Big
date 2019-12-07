package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.quiescent.KeySet;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.QrCodeUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;
import com.ysxsoft.deliverylocker_big.utils.cache.ACacheHelper;
import com.ysxsoft.deliverylocker_big.utils.glide.GlideUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;

import butterknife.BindView;

/**
 * 二维码页面
 */
public class DialogQrCodeFragment extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static final String TYPE_PICKUP = "type_pickup";
    public static final String TYPE_TS = "type_ts";

    public static DialogQrCodeFragment getInstance(String type) {
        DialogQrCodeFragment fragment = new DialogQrCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.ivQrCode)
    ImageView ivQrCode;
    private String type;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        type = getArguments() != null ? getArguments().getString(INTENT_TYPE, "") : "";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialog_pickup;
    }

    @Override
    protected void initView() {
        if (type.equals(TYPE_PICKUP)) {//取件
            getQrCode();
        } else if (TYPE_TS.equals(type)) {//暂存 取件

        }
    }


    /**
     * 获取二维码
     */
    private void getQrCode() {
        String qrCode = ACacheHelper.getString(KeySet.KEY_QRCODE, "");
        if (!TextUtils.isEmpty(qrCode)) {
            Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(qrCode,
                    DensityUtil.dp2px(mContext, 450), DensityUtil.dp2px(mContext, 450));
            ivQrCode.setImageBitmap(bitmap);
        } else {
            ApiUtils.getQrCode(DeviceInfo.getIntence().register_key(), new AbsPostJsonStringCb() {
                @Override
                public void onSuccess(String str, String data) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.optInt("status") == 0) {
                            ACacheHelper.putString(KeySet.KEY_QRCODE, object.optString("result"));
                            if (ivQrCode != null) {//加载图片
                                Bitmap bitmap = QrCodeUtil.getQrCodeWidthPic(qrCode,
                                        DensityUtil.dp2px(mContext, 450), DensityUtil.dp2px(mContext, 450));
                                ivQrCode.setImageBitmap(bitmap);
                            }
                        } else {
                            ToastUtils.show("获取二维码失败，请关闭页面重新获取！！");
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
    }



}
