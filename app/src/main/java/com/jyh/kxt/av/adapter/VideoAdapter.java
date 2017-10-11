package com.jyh.kxt.av.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.ShareItemJson;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.OnPopupFunListener;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.library.base.http.VarConstant;
import com.library.util.DateUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:视听Adapter
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/19.
 */

public class VideoAdapter extends BaseListAdapter<VideoListJson> {

    private String url_video_share;
    private Context mContext;
    private List<VideoListJson> list;
    private LayoutInflater mInflater;

    public VideoAdapter(Context context, List<VideoListJson> list) {
        super(list);
        this.mContext = context;
        this.list = list;
        mInflater = LayoutInflater.from(mContext);

        try {
            String config = SPUtils.getString(context, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson mainInitJson = JSON.parseObject(config, MainInitJson.class);
            url_video_share = mainInitJson.getUrl_video_share();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        VideoListJson videoListJson = list.get(position);
        if (!"ad".equals(videoListJson.getType())) { //不是广告
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderAd holderAd = null;

        int itemViewType = getItemViewType(position);
        final VideoListJson video = list.get(position);

        if (convertView == null) {
            switch (itemViewType) {
                case 0:
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_video, null);
                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_img);
                    holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    holder.tvCommentCount = (TextView) convertView.findViewById(R.id.tv_commentCount);
                    holder.tvPlayCount = (TextView) convertView.findViewById(R.id.tv_playCount);
                    holder.tvZanCount = (TextView) convertView.findViewById(R.id.tv_zanCount);
                    holder.ivPlay = (ImageView) convertView.findViewById(R.id.iv_playBtn);
                    holder.vLine = convertView.findViewById(R.id.v_line);
                    convertView.setTag(holder);
                    break;
                case 1:
                    holderAd = new ViewHolderAd();
                    convertView = mInflater.inflate(R.layout.item_video_ad_img, parent, false);
                    holderAd.ivAdvertView = (ImageView) convertView.findViewById(R.id.view_image);
                    convertView.setTag(holderAd);
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case 1:
                    holderAd = (ViewHolderAd) convertView.getTag();
                    break;
            }
        }


        if (itemViewType == 0) {
            setTheme(holder);

            boolean collect = CollectUtils.isCollect(mContext, VarConstant.COLLECT_TYPE_VIDEO, video);
            video.setCollect(collect);

            boolean good = NativeStore.isThumbSucceed(mContext, VarConstant.GOOD_TYPE_VIDEO, video.getId());
            video.setGood(good);

            Glide.with(mContext)
                    .load(HttpConstant.IMG_URL + video.getPicture())
                    .error(R.mipmap.icon_def_video)
                    .override(400, 300)
                    .placeholder(R.mipmap.icon_def_video)
                    .into(holder.iv);
            holder.tvTitle.setText(video.getTitle());
            holder.tvZanCount.setText(video.getNum_good());

            try {
                holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(video.getCreate_time()) * 1000, DateUtils
                        .TYPE_YMD));
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvTime.setText("2017-1-1");
            }
            holder.tvCommentCount.setText(video.getNum_comment());

            int numPlay = 0;
            try {
                numPlay = Integer.parseInt(video.getNum_play());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (numPlay < 10000) {
                holder.tvPlayCount.setText(String.valueOf(numPlay));
            } else {
                float wanPlay = (float) numPlay / 10000;
                BigDecimal b = new BigDecimal(wanPlay);
                float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                holder.tvPlayCount.setText(f1 + "万");
            }


            holder.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (url_video_share == null) {
                        TSnackbar.make(v,
                                "分享失败了喔,可能因为网络状况不好",
                                TSnackbar.LENGTH_LONG,
                                TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                                .setPromptThemBackground(Prompt.WARNING)
                                .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                        return;
                    }
                    UmengShareBean umengShareBean = new UmengShareBean();
                    umengShareBean.setFromSource(UmengShareUtil.SHARE_ARTICLE);
                    umengShareBean.setTitle(video.getTitle());
                    umengShareBean.setWebUrl(url_video_share.replace("{id}", video.getId()));
                    umengShareBean.setImageUrl(video.getShare_image());
                    umengShareBean.setDetail(video.getIntroduce());
                    umengShareBean.setSinaTitle(video.getShare_sina_title());//替换微博的

                    List<ShareItemJson> functionList = new ArrayList<>();
                    functionList.add(new ShareItemJson(UmengShareUtil.FUN_COPY_URL, R.mipmap.icon_share_link, "复制链接"));

                    //收藏
                    ShareItemJson collectShare = new ShareItemJson(R.drawable.sel_share_collect, "收藏");
                    functionList.add(collectShare);
                    collectShare.isSelectedView = video.isCollect();

                    //赞
                    ShareItemJson favourShare = new ShareItemJson(R.drawable.sel_share_ding, "赞");
                    functionList.add(favourShare);
                    favourShare.isSelectedView = video.isGood();

                    functionList.add(new ShareItemJson(UmengShareUtil.FUN_CLOSE_POP, R.mipmap.icon_share_close, "取消"));


                    UmengShareUI umengShareUI = new UmengShareUI((BaseActivity) mContext);
                    umengShareUI.showSharePopup(umengShareBean, functionList);

                    umengShareUI.setOnPopupFunListener(new OnPopupFunListener() {
                        @Override
                        public void onClickItem(final View view, ShareItemJson mShareItemJson, RecyclerView.Adapter recyclerAdapter) {

                            switch (mShareItemJson.icon) {
                                case R.drawable.sel_share_collect:
                                    clickCollect(video, mShareItemJson, recyclerAdapter);
                                    break;
                                case R.drawable.sel_share_ding:
                                    clickFavour(video, mShareItemJson, recyclerAdapter);
                                    break;
                            }
                        }
                    });
                }
            });
            holder.tvCommentCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, video.getId());
                    mContext.startActivity(intent);
                }
            });

            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, video.getId());
                    mContext.startActivity(intent);
                }
            });
        } else if (itemViewType == 1) {
            Glide.with(mContext).load(video.getPicture()).into(holderAd.ivAdvertView);
            holderAd.ivAdvertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpUtils.jump((BaseActivity) mContext,
                            video.getO_class(),
                            video.getO_action(),
                            video.getO_id(),
                            video.getHref());
                }
            });
        }
        return convertView;
    }

    /**
     * 点赞
     */

    private void clickFavour(final VideoListJson video, final ShareItemJson mShareItemJson, final RecyclerView.Adapter recyclerAdapter) {
        if (mShareItemJson.isSelectedView) {
            ToastView.makeText3(VideoAdapter.this.mContext, "已点赞");
        } else {
            NativeStore.addThumbID(VideoAdapter.this.mContext, VarConstant.GOOD_TYPE_VIDEO, video.getId(), null,
                    new ObserverData<Boolean>() {
                        @Override
                        public void callback(Boolean aBoolean) {
                            mShareItemJson.isSelectedView = true;
                            video.setIsGood(true);
                            recyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastView.makeText3(mContext, "点赞失败");
                        }
                    });
        }
    }

    /**
     * 点击收藏
     *
     * @param video
     * @param mShareItemJson
     * @param recyclerAdapter
     */
    private void clickCollect(final VideoListJson video, final ShareItemJson mShareItemJson, final RecyclerView.Adapter recyclerAdapter) {
        final BaseActivity mActivity = (BaseActivity) VideoAdapter.this.mContext;

        boolean isCollectVideo = CollectUtils.isCollect(VideoAdapter.this.mContext,
                VarConstant.COLLECT_TYPE_VIDEO,
                video);

        if (isCollectVideo) {
            CollectUtils.unCollect(VideoAdapter.this.mContext, VarConstant.COLLECT_TYPE_VIDEO, video, null,
                    new ObserverData<Boolean>() {
                        @Override
                        public void callback(Boolean o) {
                            mShareItemJson.isSelectedView = false;
                            video.setIsCollect(false);
                            recyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastView.makeText3(mActivity, "收藏失败");
                        }
                    });

        } else {
            CollectUtils.collect(VideoAdapter.this.mContext,
                    VarConstant.COLLECT_TYPE_VIDEO,
                    video,
                    null,
                    new ObserverData<Boolean>() {
                        @Override
                        public void callback(Boolean o) {
                            //改变umeng面板收藏按钮状态
                            mShareItemJson.isSelectedView = true;
                            video.setIsCollect(true);
                            recyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastView.makeText3(mActivity, "收藏失败");
                        }
                    });
        }
    }

    private void setTheme(ViewHolder holder) {
        holder.ivPlay.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_video_play_big));
        holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color10));
        holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
        int paddingVal = SystemUtil.dp2px(mContext, 6);
        holder.tvTime.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvTime, R.mipmap.icon_video_time, 0, 0,
                0);

        holder.tvCommentCount.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
        paddingVal = SystemUtil.dp2px(mContext, 6);
        holder.tvCommentCount.setPadding(0, paddingVal, 0, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvCommentCount, R.mipmap
                .icon_video_comment, 0, 0, 0);

        holder.tvZanCount.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
        paddingVal = SystemUtil.dp2px(mContext, 6);
        holder.tvZanCount.setPadding(0, paddingVal, 0, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvZanCount, R.mipmap
                .icon_video_list_zan, 0, 0, 0);


        holder.tvPlayCount.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
        paddingVal = SystemUtil.dp2px(mContext, 6);
        holder.tvPlayCount.setPadding(0, paddingVal, 0, paddingVal);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(holder.tvPlayCount, R.mipmap
                .icon_video_play_small, 0, 0, 0);

        holder.ivMore.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_video_more));

        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));

    }

    public void setData(List<VideoListJson> videoListJsons) {
        list.clear();
        list.addAll(videoListJsons);
        list = videoListJsons;
        notifyDataSetChanged();
    }

    public void addData(List<VideoListJson> videoListJsons) {
        list.addAll(videoListJsons);
        notifyDataSetChanged();
    }

    public List<VideoListJson> getData() {
        return dataList;
    }


    class ViewHolderAd {
        public ImageView ivAdvertView;
    }

    class ViewHolder {
        public ImageView iv, ivPlay;
        public TextView tvTitle, tvTime, tvPlayCount, tvCommentCount, tvZanCount;
        public ImageView ivMore;
        public View vLine;
    }
}
