package com.ysxsoft.deliverylocker_big.ui.dialog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.ui.activity.MainActivity;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgTableBean;
import com.ysxsoft.deliverylocker_big.ui.adapter.FgVpAdapter;
import com.ysxsoft.deliverylocker_big.ui.fragment.BaseFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogNumbCodeFragment;
import com.ysxsoft.deliverylocker_big.ui.fragment.DialogQrCodeFragment;
import com.ysxsoft.deliverylocker_big.utils.DateTimeUtil;
import com.ysxsoft.deliverylocker_big.utils.DateUtil;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.ViewHolder;
import com.ysxsoft.deliverylocker_big.widget.MyConstraintLayout;
import com.ysxsoft.deliverylocker_big.widget.wheelview.IPickerViewData;
import com.ysxsoft.deliverylocker_big.widget.wheelview.WheelRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ysxsoft.deliverylocker_big.ui.fragment.DialogQrCodeFragment.TYPE_PICKUP;

public class PickerDialog extends BaseDialog {



    private WheelRecyclerView<TimeData> wheelView;
    private TextView tvMiddle;
    private PickerListener listener;

    private int time;
    private String text;

    public PickerDialog(PickerListener listener) {
        this.listener = listener;
    }

    @Override
    public int setUpLayoutId() {
        return R.layout.dialog_picker;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        wheelView = holder.getView(R.id.wheelView);
        tvMiddle = holder.getView(R.id.tvMiddle);

        List<TimeData> list = new ArrayList<>();
        for (int i = 1; i <= 48; i++) {
            list.add(new TimeData(i+"小时"));
        }
        for (int i = 3; i <= 7; i++) {
            list.add(new TimeData(i+"天"));
        }
        wheelView.setData(list);
        wheelView.setOnSelectListener((position, data) -> {
            if (position < 48){
                time = position + 1;
                text = String.format("截止至%s", DateUtil.getTimer(position + 1));
            }else {
                time = (position - 48 + 3)*24;
                text = String.format("截止至%s", DateUtil.getDay( position - 48 + 3));
            }
            tvMiddle.setText(text);
        });
        time = 1;
        text = String.format("截止至%s", DateUtil.getTimer(1));
        tvMiddle.setText(text);
        holder.getView(R.id.tvCancel).setOnClickListener(v -> dismiss());
        holder.getView(R.id.tvConfirm).setOnClickListener(v -> {
            if (listener != null){
                listener.selected(time, text);
            }
            dismiss();
        });
    }

    
    class TimeData implements IPickerViewData{
        
        private String time;

        public TimeData(String time) {
            this.time = time;
        }

        @Override
        public String getPickerViewText() {
            return time;
        }
    }

    public interface PickerListener{
        void selected(int  time, String text);
    }
}
