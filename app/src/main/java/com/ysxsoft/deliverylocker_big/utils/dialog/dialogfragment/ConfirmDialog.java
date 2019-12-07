package com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ysxsoft.deliverylocker_big.R;


/**
 * 确认样式1
 */
public class ConfirmDialog extends BaseDialog {

    private String type;

    public static ConfirmDialog newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        type = bundle.getString("type");
    }

    @Override
    public int setUpLayoutId() {
//        return R.layout.dialog_confirm;
        return 1;
    }

    @Override
    public void convertView(ViewHolder holder, final BaseDialog dialog) {
        if ("1".equals(type)) {
            holder.setText(R.id.title, "提示");
            holder.setText(R.id.message, "您已支付成功!");
        } else if ("2".equals(type)) {
            holder.setText(R.id.title, "警告");
            holder.setText(R.id.message, "您的帐号已被冻结!");
        }
    }
}
