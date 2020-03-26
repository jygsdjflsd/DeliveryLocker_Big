package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.bean.DoorPriceBean;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.activity.NetWorkLoseActivity;
import com.ysxsoft.deliverylocker_big.ui.adapter.FeeAdapter;
import com.ysxsoft.deliverylocker_big.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码页面
 */
public class SaveFragment3 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";

    public static SaveFragment3 getInstance(int type, FragmentListener listener) {
        SaveFragment3 fragment = new SaveFragment3(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.tvSupperSmall)
    TextView tvSupperSmall;
    @BindView(R.id.ivSupperSmall)
    LinearLayout ivSupperSmall;
    @BindView(R.id.tvSmall)
    TextView tvSmall;
    @BindView(R.id.ivSmall)
    LinearLayout ivSmall;
    @BindView(R.id.tvMiddle)
    TextView tvMiddle;
    @BindView(R.id.ivMiddle)
    LinearLayout ivMiddle;
    @BindView(R.id.tvBig)
    TextView tvBig;
    @BindView(R.id.ivBig)
    LinearLayout ivBig;
    @BindView(R.id.tvBigger)
    TextView tvBigger;
    @BindView(R.id.ivBigger)
    LinearLayout ivBigger;
    @BindView(R.id.layoutFee)
    LinearLayout layoutFee;
    @BindView(R.id.btnSee)
    Button btnSee;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ivClose)
    ImageView ivClose;

    private FragmentListener listener;
    private int type;
    private DoorPriceBean bean;

    private FeeAdapter adapter;
    private boolean isLoadOver = false;//是否加载过
    private boolean isVisiable = false;//是否当前可见
    private boolean isInitView = false;//是否初始化

    private SaveFragment3(FragmentListener listener) {
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
        isVisiable = isVisibleToUser;
        if (!isLoadOver) {//未加载
            if (isInitView && isVisibleToUser) {//未加载过且已经初始化且当前可见
                initAdapter();
                doorPrice();
                isLoadOver = true;//加载完成
            }
        } else {//已加载
            if (isInitView && isVisibleToUser) {
                initAdapter();
                doorPrice();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_3;
    }

    @Override
    protected void initView() {
        if (isVisiable && !isLoadOver) {
            initAdapter();
            doorPrice();
        }
        isInitView = true;
    }

    //初始化adapter
    private void  initAdapter(){
        adapter = new FeeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.ivSupperSmall, R.id.ivSmall, R.id.ivMiddle, R.id.ivBig, R.id.ivBigger, R.id.btnSee, R.id.ivClose})
    public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.ivSupperSmall:
                    if (bean != null && bean.getStatus() == 0  && bean.getResult().getCount().getSupersmall() > 0) {
                        listener.doSomething(type, "supersmall");
                    }
                    break;
                case R.id.ivSmall:
                    if (bean != null && bean.getStatus() == 0  && bean.getResult().getCount().getSmall() > 0) {
                        listener.doSomething(type, "small");
                    }
                    break;
                case R.id.ivMiddle:
                    if (bean != null && bean.getStatus() == 0 && bean.getResult().getCount().getMiddle() > 0) {
                        listener.doSomething(type, "middle");
                    }
                    break;
                case R.id.ivBig:
                    if (bean != null && bean.getStatus() == 0  && bean.getResult().getCount().getBig() > 0){
                        listener.doSomething(type, "big");
                    }
                    break;
                case R.id.ivBigger:
                    if (bean != null && bean.getStatus() == 0  && bean.getResult().getCount().getSuperbig() > 0){
                        listener.doSomething(type, "superbig");
                    }
                    break;
                case R.id.btnSee://查看收费标准
                    layoutFee.setVisibility(View.VISIBLE);
                    break;
                case R.id.ivClose://关闭收费标准
                    layoutFee.setVisibility(View.GONE);
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
                bean = new Gson().fromJson(str, DoorPriceBean.class);
                if (bean.getStatus() == 0) {
                    tvSmall.setText(String.format("剩余格子：%s", bean.getResult().getCount().getSupersmall()));
                    tvSmall.setText(String.format("剩余格子：%s", bean.getResult().getCount().getSmall()));
                    tvMiddle.setText(String.format("剩余格子：%s", bean.getResult().getCount().getMiddle()));
                    tvBig.setText(String.format("剩余格子：%s", bean.getResult().getCount().getBig()));
                    tvBigger.setText(String.format("剩余格子：%s", bean.getResult().getCount().getSuperbig()));
                    List<FeeDetailBean> list = new ArrayList<>();
                    list.add(new FeeDetailBean("超小格子：", bean.getResult().getPrice().getSupersmall()));
                    list.add(new FeeDetailBean("小格子：", bean.getResult().getPrice().getSmall()));
                    list.add(new FeeDetailBean("中格子：", bean.getResult().getPrice().getMiddle()));
                    list.add(new FeeDetailBean("大格子：", bean.getResult().getPrice().getBig()));
                    list.add(new FeeDetailBean("超大格子：", bean.getResult().getPrice().getSuperbig()));
                    adapter.setNewData(list);
                }else if (bean.getStatus() == 1){
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

    public static class FeeDetailBean{
        private String title;
        private String conetnt;

        public FeeDetailBean(String title, String conetnt) {
            this.title = title;
            this.conetnt = conetnt;
        }

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getConetnt() {
            return conetnt == null ? "" : conetnt;
        }

        public void setConetnt(String conetnt) {
            this.conetnt = conetnt;
        }
    }

}
