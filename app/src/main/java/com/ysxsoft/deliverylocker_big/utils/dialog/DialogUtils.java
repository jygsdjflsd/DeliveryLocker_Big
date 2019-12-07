package com.ysxsoft.deliverylocker_big.utils.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ysxsoft.deliverylocker_big.ActivityManager;
import com.ysxsoft.deliverylocker_big.R;
import com.ysxsoft.deliverylocker_big.app.MyApplication;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.BaseDialog;
import com.ysxsoft.deliverylocker_big.utils.dialog.dialogfragment.CommonDialog;
import com.ysxsoft.deliverylocker_big.utils.glide.GlideUtils;

import java.util.Objects;

public class DialogUtils {



    /**
     * 通用样式dialogfragment(全部默认)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, false, true, listener);
    }

    /**
     * 通用样式dialogfragment(设置左右边距)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, int margin, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, false, true, false, margin, listener);
    }

    /**
     * 通用样式dialogfragment(只设置昏暗度)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, float amout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, amout, false, true, listener);
    }

    /**
     * 通用样式dialogfragment(设置昏暗度和是否允许外部点击)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, float amout, boolean enable, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, amout, false, enable, listener);
    }

    /**
     * 通用样式dialogfragment(只设置是否在底部)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, boolean bottom, int layout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, bottom, true, listener);
    }

    /**
     * 通用样式dialogfragment(是否在底部,昏暗度)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, boolean bottom, int layout, float amout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, amout, bottom, true, listener);
    }

    /**
     * 通用样式dialogfragment(只设置外部点击)
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, boolean enable, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, false, enable, listener);
    }

    /**
     * 通用样式dialogfragment(是否全屏)
     *
     * @param manager
     */
    public static BaseDialog showDialog(boolean full, FragmentManager manager, int layout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, false, true, full, 50, listener);
    }

    public static BaseDialog showDialog(boolean full, int margin, FragmentManager manager, int layout, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, 0.5f, false, true, full, margin, listener);
    }

    /**
     * 通用样式dialogfragment
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, float amout, boolean bottom, boolean enable, CommonDialog.ViewConvertListener listener) {
        return showDialog(manager, layout, amout, bottom, enable, false, 0, listener);
    }

    /**
     * 通用样式dialogfragment
     *
     * @param manager
     */
    public static BaseDialog showDialog(FragmentManager manager, int layout, float amout, boolean bottom, boolean enable, boolean isFull, int margin, CommonDialog.ViewConvertListener listener) {
        //全屏参数isfull 和 是否显示在底部参数bottom  在bottom 为false 时生效
        return CommonDialog.newInstance(!bottom && isFull)
                .setLayoutId(layout)
                .setConvertListener(listener)
                .setShowBottom(bottom)
                .setSize(0, 0)
                .setDimAmout(amout)//设置背景昏暗度//默认0.5f
                .setOutCancel(enable)
                .setMargin(margin)
                .show(manager);
    }
    public static <T extends BaseDialog>T showDialog(FragmentManager manager,  boolean bottom, boolean isFull, float amout, boolean enable){
        BaseDialog dialog = CommonDialog.newInstance(!bottom && isFull)
                .setShowBottom(bottom)
                .setSize(0, 0)
                .setDimAmout(amout)//设置背景昏暗度//默认0.5f
                .setOutCancel(enable)
                .show(manager);
        return (T)dialog;
    }


    /**
     * 在控件下方展示的dialog
     *
     * @param view
     * @param layout
     * @param listener
     */
    public static void menuDialog(View view, Context mContext, int layout, MenuDialogListener listener) {
        menuDialog(true, view, mContext, layout, 0.5f, Gravity.TOP, listener);
    }
    public static void menuDialog(boolean match,View view, Context mContext, int layout, MenuDialogListener listener) {
        menuDialog(match, view, mContext, layout, 0.5f, Gravity.TOP, listener);
    }

    public static void menuDialog(View view, Context mContext, int layout, int gravity, MenuDialogListener listener) {
        menuDialog(true, view, mContext, layout, 0.5f, gravity, listener);
    }
    public static void menuDialog(boolean match,View view, Context mContext, int layout, int gravity, MenuDialogListener listener) {
        menuDialog(match, view, mContext, layout, 0.5f, gravity, listener);
    }
    public static void menuDialog(boolean match,View view, Context mContext, int layout, int gravity, float dimamout, MenuDialogListener listener) {
        menuDialog(match, view, mContext, layout, dimamout, gravity, listener);
    }

    public static void menuDialog(View view, Context mContext, int layout, float dimamout, MenuDialogListener listener) {
        menuDialog(true, view, mContext, layout, dimamout, Gravity.TOP, listener);
    }
    public static void menuDialog(boolean match, View view, Context mContext, int layout, float dimamout, MenuDialogListener listener) {
        menuDialog(match, view, mContext, layout, dimamout, Gravity.TOP, listener);
    }

    public static void menuDialog(boolean match, View view, Context mContext, int layout, float dimamout, int gravity, MenuDialogListener listener) {
        Dialog dialog = new Dialog(mContext, R.style.custom_dialog2);
        View inflate = LayoutInflater.from(mContext).inflate(layout, null);
        //弹窗点击周围空白处弹出层自动消失弹窗消失(false时为点击周围空白处弹出层不自动消失)
        dialog.setCanceledOnTouchOutside(true);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window window = dialog.getWindow();
        window.setDimAmount(dimamout);
        WindowManager.LayoutParams wlp = window.getAttributes();
        //获取通知栏高度  重要的在这，获取到通知栏高度
        int notificationBar = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        //获取控件 textview 的绝对坐标,( y 轴坐标是控件上部到屏幕最顶部（不包括控件本身）)
        //location [0] 为x绝对坐标;location [1] 为y绝对坐标
        int[] location = new int[2];
        view.getLocationInWindow(location); //获取在当前窗体内的绝对坐标
        view.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        wlp.x = 0; //对 dialog 设置 x 轴坐标
        wlp.y = location[1] + view.getHeight() - notificationBar; //对dialog设置y轴坐标
        wlp.gravity = gravity;
        if (match)
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        else
            wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();//显示对话框
        if (null != listener) {
            listener.showDialog(inflate, dialog);
        }
    }



    /**
     * design库最新的Bottomdialog 底部dialog最简单实现方式
     *
     * @param mContext
     * @param layout   布局
     *                 透明样式
     */
    public static void bottomDialog(Context mContext, int layout, BottomDialogListener listener) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetEdit);//解决dialog包含edittext时 keyboads遮挡焦点以外的dialog
        View contentView = View.inflate(mContext, layout, null);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.0f);
        /** 解决bottomsheetdialog 弹出不完全*/
        bottomSheetDialog.setContentView(contentView);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        behavior.setPeekHeight(contentView.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
        /**  结束----------------------------*/
        //给布局设置透明背景色
        bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet)
                .setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        listener.showDialog(bottomSheetDialog);
        bottomSheetDialog.show();
    }


    public interface BottomDialogListener {
        void showDialog(BottomSheetDialog bottomSheetDialog);
    }

    public interface MenuDialogListener {
        void showDialog(View view, Dialog dialog);
    }


    public interface PopuWindowListener {
        void showDialog(View view, PopupWindow window);
    }

    public interface ToastDialogListener {
        void timesup();
    }
}
