package com.ysxsoft.deliverylocker_big.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class KeybordUtils {

    /**
     * 判断软键盘是否开启
     * @return
     */
    public static boolean isSoftShowing(AppCompatActivity activity) {
        //获取当前屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    /**
     * 隐藏软键盘
     *
     * @param v
     */
    public static boolean hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 开启软键盘
     *
     * @param v 注：当软盘关闭时自动弹出
     */
    public static void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 软键盘显示隐藏监听
     *
     * @param view
     * @param listener
     */
    public static void setOnkeyboardListener(View view, KeyBoardListener listener) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            view.getWindowVisibleDisplayFrame(r);
            int screenHeight = view.getRootView()
                    .getHeight();
            int heightDifference = screenHeight - (r.bottom);
            if (heightDifference > 200) {
                //软键盘显示
                listener.show(true);
            } else {
                //软键盘隐藏
                listener.show(false);
            }
        });
    }

    /**
     * 判断当前点击事件是否是软键盘
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                //保留点击edittext的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前点击事件是否是软键盘或者（软键盘显示时需要保留的点击事件）
     * 这样会导致软键盘覆盖布局的样式弹出，如果设置android:windowSoftInputMode="adjustPan"，这个判断就需要加上软键盘高度。。。（没做）
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(Activity activity, View v, View v1, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {
            Rect r = new Rect();
            //获取当前界面可视部分
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = screenHeight - r.bottom;

            int[] v1Top = {0, 0};
            v1.getLocationInWindow(v1Top);
            int left1 = v1Top[0], top1 = v1Top[1] + heightDifference, bottom1 = top1 + v1.getHeight(), right1 = left1 + v1.getWidth();
            if (event.getX() > left1 && event.getX() < right1 && event.getY() > top1 && event.getY() < bottom1) {
                //保留点击v1的事件
                return false;
            }
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1] + heightDifference, bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                //保留点击edittext的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 软键盘显示隐藏的监听
     */
    public interface KeyBoardListener {
        void show(boolean show);
    }
}
