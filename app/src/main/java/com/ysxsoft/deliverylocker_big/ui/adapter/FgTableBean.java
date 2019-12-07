package com.ysxsoft.deliverylocker_big.ui.adapter;


import androidx.fragment.app.Fragment;

/**
 * Created by jyg on 2017/12/29/029.
 * function :
 */

public class FgTableBean<T extends Fragment> {

    private T fragment;

    private String title;
    private int id;

    public FgTableBean(T fragment, String title, int id) {
        this.fragment = fragment;
        this.title = title;
        this.id = id;
    }
    public FgTableBean() {
    }

    public T getFragment() {
        return fragment;
    }

    public void setFragment(T fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
