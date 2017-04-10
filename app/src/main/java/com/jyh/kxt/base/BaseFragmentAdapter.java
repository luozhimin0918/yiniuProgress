package com.jyh.kxt.base;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/10.
 */

public class BaseFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> listFragment;

    public BaseFragmentAdapter(FragmentManager fm, List<Fragment> listFragment) {
        super(fm);
        this.listFragment = listFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
