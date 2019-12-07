package com.ysxsoft.deliverylocker_big.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**TabLayout和Viewpager和Fragment联用的适配器
 * Created by Administrator on 2017/2/23.
 */

public class FgVpAdapter<T extends Fragment> extends FragmentPagerAdapter {
    private List<FgTableBean<T>> mList;

    public FgVpAdapter(FragmentManager fm, List<FgTableBean<T>> mList) {
        super(fm);
        this.mList = mList;
    }

    @Override
    public T getItem(int position) {
        return mList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getTitle();
    }


}
