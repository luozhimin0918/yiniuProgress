package com.jyh.kxt.av.presenter;

import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.av.ui.fragment.VideoItemFragment;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;

import java.util.ArrayList;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class VideoItemPresenter extends BasePresenter {

    @BindObject VideoItemFragment videoItemFragment;

    public VideoItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setAdapter() {
        videoItemFragment.plvContent.setAdapter(new BaseListAdapter<String>(new ArrayList<String>()) {
            ViewHolder viewHolder;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                return convertView;
            }

            class ViewHolder {

            }
        });
    }
}
