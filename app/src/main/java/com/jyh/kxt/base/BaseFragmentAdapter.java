package com.jyh.kxt.base;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/10.
 */

public class BaseFragmentAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private List<Fragment> listFragment;

    public BaseFragmentAdapter(FragmentManager fm, List<Fragment> listFragment) {
        super(fm);
        this.fm = fm;
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
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        fm.beginTransaction().remove(fragment);
    }
}
