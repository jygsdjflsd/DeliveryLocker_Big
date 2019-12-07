package com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment;


import android.os.Bundle;

import androidx.annotation.LayoutRes;


/**
 * 通用样式
 */
public class CommonDialog extends BaseDialog {
    private ViewConvertListener convertListener;


    public static CommonDialog newInstance( boolean isFull) {
        CommonDialog dialog = new CommonDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFull", isFull);
        dialog.setArguments(bundle);
        return dialog;
    }


    /**
     * 设置Dialog布局
     *
     * @param layoutId
     * @return
     */
    public CommonDialog setLayoutId(@LayoutRes int layoutId) {
        this.mLayoutResId = layoutId;
        return this;
    }

    @Override
    public int setUpLayoutId() {
        return mLayoutResId;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        if (convertListener != null) {
            convertListener.convertView(holder, dialog);
        }
    }

    public CommonDialog setConvertListener(ViewConvertListener convertListener) {
        this.convertListener = convertListener;
        return this;
    }

    public interface ViewConvertListener{
        void convertView(ViewHolder holder, BaseDialog dialog);
    }
}
