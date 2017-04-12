package com.jyh.kxt.av.presenter;

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
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import org.w3c.dom.ls.LSInput;

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
            ViewHolder holder = null;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, videoItemFragment
                            .plvContent, false);
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
