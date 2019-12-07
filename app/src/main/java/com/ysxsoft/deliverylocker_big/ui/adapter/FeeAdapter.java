package com.ysxsoft.deliverylocker_big.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.ui.fragment.SaveFragment3;

public class FeeAdapter extends BaseQuickAdapter<SaveFragment3.FeeDetailBean, BaseViewHolder> {



    public FeeAdapter() {
        super(R.layout.item_fee);
    }

    @Override
    protected void convert(BaseViewHolder helper, SaveFragment3.FeeDetailBean item) {

        helper.setText(R.id.tvTitle, item.getTitle());
        helper.setText(R.id.tvItem, item.getConetnt());
    }
}
