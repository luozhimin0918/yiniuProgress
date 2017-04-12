package com.jyh.kxt.main.presenter;

import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemPresenter extends BasePresenter {

    @BindObject
    NewsItemFragment newsItemFragment;

    public NewsItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setAdapter() {
        List list = new ArrayList();
        newsItemFragment.plvContent.setAdapter(new BaseListAdapter<Object>(list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
    }
}
