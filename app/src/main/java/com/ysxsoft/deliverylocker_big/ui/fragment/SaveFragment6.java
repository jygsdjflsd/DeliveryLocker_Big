package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.api.ApiUtils;
import com.ysxsoft.deliverylocker_big.bean.DeviceInfo;
import com.ysxsoft.deliverylocker_big.connecter.FragmentListener;
import com.ysxsoft.deliverylocker_big.network.AbsPostJsonStringCb;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgTableBean;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgVpAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 二维码页面
 */
public class SaveFragment6 extends BaseFragment {

    private static final String INTENT_TYPE = "intent_type";
    public static SaveFragment6 getInstance(int type, FragmentListener listener) {
        SaveFragment6 fragment = new SaveFragment6(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.relBottom)
    RelativeLayout relBottom;
    @BindView(R.id.tvBottom)
    TextView tvBottom;


    private String[] tabs = {/*"二维码取件", */"取件码取件"};
    private int type;
    private FragmentListener listener;

    private SaveFragment6(FragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        type = getArguments() != null ? getArguments().getInt(INTENT_TYPE) : type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ts_6;
    }

    @Override
    protected void initView() {
        tvBottom.setText("取件码取件");
        initFragemnt();
    }

    /**
     * 加载fragment
     */
    private void initFragemnt() {
        List<FgTableBean<BaseFragment>> informfragments = new ArrayList<>();
//        informfragments.add(new FgTableBean<>(DialogQrCodeFragment.getInstance(DialogQrCodeFragment.TYPE_TS), tabs[0], 0));
        informfragments.add(new FgTableBean<>(SaveNumbCodeFragment.getInstance(listener, type), tabs[0], 0));
        FgVpAdapter<BaseFragment> fgVpAdapter = new FgVpAdapter<>(getChildFragmentManager(), informfragments);
        viewPager.setAdapter(fgVpAdapter);
//        tabLayout.setupWithViewPager(viewPager);
    }

}
