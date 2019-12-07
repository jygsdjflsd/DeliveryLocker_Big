package com.ysxsoft.deliverylocker_big.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MyConstraintLayout extends ConstraintLayout {


    private DisPatchTouchListener listener;

    public MyConstraintLayout(Context context) {
        super(context);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(DisPatchTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if (listener != null){
                listener.onTouch();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface DisPatchTouchListener{
        void onTouch();
    }
}
