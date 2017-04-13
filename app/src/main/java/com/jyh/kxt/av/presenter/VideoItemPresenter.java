package com.jyh.kxt.av.presenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.fragment.VideoItemFragment;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class VideoItemPresenter extends BasePresenter {

    @BindObject VideoItemFragment videoItemFragment;

    public VideoItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setAdapter() {
        List list = getVideoList();
        videoItemFragment.plvContent.setAdapter(new BaseListAdapter<String>(list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, null);
                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_img);
                    holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    holder.tvCommentCount = (TextView) convertView.findViewById(R.id.tv_commentCount);
                    holder.tvPlayCount = (TextView) convertView.findViewById(R.id.tv_playCount);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.iv.setImageResource(R.mipmap.ic_launcher);
                holder.tvTitle.setText("好滴撒回复的bfUI奥比岛的并非");
                holder.tvTime.setText("2011-1-1");
                holder.tvCommentCount.setText("251");
                holder.tvPlayCount.setText("26549");

                holder.ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastView.makeText3(mContext, "分享");
                        UmengShareTool.initUmengLayout((Activity) mContext,"标题标题","http://www.baidu.com","内容内容",null,null,holder.ivMore,null);
                    }
                });
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastView.makeText3(mContext, "播放");
                    }
                });


                return convertView;
            }

            class ViewHolder {
                public ImageView iv;
                public TextView tvTitle, tvTime, tvPlayCount, tvCommentCount;
                public ImageView ivMore;
            }
        });
    }

    private List getVideoList() {

//        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
//        volleyRequest.doPost("www.baidu.com", new HttpListener<Object>() {
//            @Override
//            protected void onResponse(Object o) {
//
//            }
//        });

        List list = new ArrayList();
        for (int i = 0; i < 10; i++) {
            list.add("1111");
        }
        return list;
    }
}
