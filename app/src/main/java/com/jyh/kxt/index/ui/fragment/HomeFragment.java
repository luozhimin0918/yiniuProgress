package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.user.ui.SettingActivity;
import com.library.base.LibActivity;

import butterknife.OnClick;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home, LibActivity.StatusBarColor.THEME1);

    }
    @OnClick(R.id.tv_show_dialog)
    public void showDialog() {
        startActivity(new Intent(getContext(), SettingActivity.class));
//        showWaitDialog("请稍等");
    }

}
