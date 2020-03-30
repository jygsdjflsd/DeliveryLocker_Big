package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.DoorPriceBean;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码页面
 */
public class SaveFragment1 extends BaseFragment {
    public static final String TYPE_THROW = "type_throw";
    public static final String TYPE_GET = "type_get";
    private static final String INTENT_TYPE = "intent_type";


    public static SaveFragment1 getInstance(int type, FragmentListener listener) {
        SaveFragment1 fragment = new SaveFragment1(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.tvNumb)
    TextView tvNumb;

    private FragmentListener listener;
    private int type;

    private boolean isLoadOver = false;//是否加载过
    private boolean isVisiable = false;//是否当前可见
    private boolean isInitView = false;//是否初始化

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisiable = isVisibleToUser;
        if (!isLoadOver) {//未加载
            if (isInitView && isVisibleToUser) {//未加载过且已经初始化且当前可见
                doorPrice();
                isLoadOver = true;//加载完成
            }
        } else {//已加载
            if (isInitView && isVisibleToUser) {
                doorPrice();
            }
        }
    }

    private SaveFragment1(FragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        type = getArguments() != null ? getArguments().getInt(INTENT_TYPE) : type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_1;
    }

    @Override
    protected void initView() {
        if (isVisiable && !isLoadOver) {
            doorPrice();
        }
        isInitView = true;
    }


    @OnClick({R.id.iv1, R.id.iv2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv1:
                if (listener != null) {
                    listener.doSomething(type, TYPE_THROW);
                }
                break;
            case R.id.iv2:
                if (listener != null) {
                    listener.doSomething(type, TYPE_GET);
                }
                break;
        }
    }

    /**
     * 获取当前网点的暂存物品收费，各类型空闲格子数量
     */
    private void doorPrice() {
        ApiUtils.doorPrice(DeviceInfo.getIntence().register_key(), new AbsPostJsonStringCb() {
            @Override
            public void onSuccess(String str, String data) {
                DoorPriceBean bean = new Gson().fromJson(str, DoorPriceBean.class);
                if (bean.getStatus() == 0) {
                    DoorPriceBean.ResultBean.CountBean cBean = bean.getResult().getCount();
                    if (tvNumb != null)
                        tvNumb.setText(String.valueOf(cBean.getBig() + cBean.getMiddle()
                                + cBean.getSmall()+ cBean.getSuperbig()+cBean.getSupersmall()));
                } else if (bean.getStatus() == 1) {
                    ToastUtils.show(bean.getMsg());
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
                isLoadOver = true;//加载完成
            }
        });
    }
}
