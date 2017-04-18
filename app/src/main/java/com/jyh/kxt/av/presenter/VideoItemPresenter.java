package com.jyh.kxt.av.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.av.ui.fragment.VideoItemFragment;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.DateUtils;
import com.library.util.EncryptionUtils;
import com.library.widget.window.ToastView;
import com.tencent.smtt.sdk.VideoActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class VideoItemPresenter extends BasePresenter {

    @BindObject VideoItemFragment videoItemFragment;
    private RequestQueue queue;
    private String id;
    private VolleyRequest request;

    public VideoItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(Bundle arguments) {
        videoItemFragment.plRootView.loadWait();
        id = arguments.getString(IntentConstant.CODE);
        queue = videoItemFragment.getQueue();
        request = new VolleyRequest(mContext, queue);
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(final List<VideoListJson> videoListJsons) {
                if (videoListJsons != null) {
                    videoItemFragment.plvContent.setAdapter(new BaseListAdapter<VideoListJson>(videoListJsons) {
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

                            final VideoListJson video = videoListJsons.get(position);
                            Glide.with(mContext).load(HttpConstant.IMG_URL + video.getPicture()).error(R.mipmap.ico_def_load).placeholder
                                    (R.mipmap.ico_def_load).into
                                    (holder.iv);
                            holder.tvTitle.setText(video.getTitle());
                            try {
                                holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(video.getCreate_time())*1000));
                            } catch (Exception e) {
                                e.printStackTrace();
                                holder.tvTime.setText("00:00");
                            }
                            holder.tvCommentCount.setText(video.getNum_comment());
                            holder.tvPlayCount.setText(video.getNum_play());

                            holder.ivMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UmengShareTool.initUmengLayout((BaseActivity) mContext, video.getTitle(), "http://www.baidu.com",
                                            "http://www.baidu" +
                                                    ".com",
                                            null, null, holder.ivMore, null);
                                }
                            });
                            holder.iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                                    mContext.startActivity(intent);
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
                    videoItemFragment.plRootView.loadOver();
                } else
                    onErrorResponse(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    videoItemFragment.plRootView.loadError();
                    ToastView.makeText3(mContext, mContext.getString(R.string.toast_error_load));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getUrl() {
        String url = HttpConstant.VIDEO_LIST;
        try {
            JSONObject object = new JSONObject();
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION, com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION_VALUE);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM, com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM_VALUE);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_ID, id);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_LASTID, 1);
            url = url + com.jyh.kxt.base.constant.VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            url = "";
        }
        return url;
    }
}
