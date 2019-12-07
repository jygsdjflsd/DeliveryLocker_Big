package com.ysxsoft.deliverylocker_big.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    protected View mView;
    Unbinder unbinder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView  == null){
            mView = inflater.inflate( getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, mView);
        }
        initView();
        return mView;
    }

    protected abstract int getLayoutId();
    protected abstract void initView();

    public void externalInvoking(String type){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
