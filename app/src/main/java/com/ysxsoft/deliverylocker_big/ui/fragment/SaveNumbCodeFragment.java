package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.TakeCodeBean;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeErrorDialog;
import com.ysxsoft.deliverylocker_big.ui.dialog.TakeCodeSuccessDialog;
import com.ysxsoft.deliverylocker_big.utils.DensityUtil;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;
import com.ysxsoft.deliverylocker_big.widget.password.PasswordView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 数字码页面
 */
public class SaveNumbCodeFragment extends BaseFragment {


    public static SaveNumbCodeFragment getInstance(FragmentListener listener, int type) {
        return new SaveNumbCodeFragment(listener, type);
    }

    @BindView(R.id.passwordView)
    PasswordView passwordView;

    private FragmentListener listener;
    private int type;

    private SaveNumbCodeFragment(FragmentListener listener, int type) {
        this.listener = listener;
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialogsave_numbercode;
    }

    @Override
    protected void initView() {
        passwordView.setListener(this::queryOrder);
    }

    /**
     * 查询订单
     */
    private void queryOrder(String numb) {
        ApiUtils.queryOrder(DeviceInfo.getIntence().register_key(), numb, new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.optInt("status") == 0) {
                        //TODO  根据返回信息判定当前取出码是否失效
                        if (listener != null) listener.doSomething(type, numb);
                    } else {//失效弹出错误提示
                        TakeCodeErrorDialog.newInstance()
                                .setSize(DensityUtil.dp2px(mContext, 400), DensityUtil.dp2px(mContext, 270))
                                .show(getChildFragmentManager());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {//恢复键盘
                passwordView.recoverKeybordClickEnable();
            }
        });
    }

}
