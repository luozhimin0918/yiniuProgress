package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.tablayout.NavigationTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointAdapter extends BaseListAdapter<ViewPointTradeBean> implements
        PinnedSectionListView.PinnedSectionListAdapter, NavigationTabLayout.OnTabSelectListener {

    private Context mContext;
    private LayoutInflater mInflater;

    private int navigationTabClickPosition = 0;

    private int viewTypeCount = 2;

    public ViewpointAdapter(Context mContext, List<ViewPointTradeBean> dataList) {
        super(dataList);

        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder0 viewHolder0 = null;
        ViewHolder1 viewHolder1 = null;

        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            switch (itemViewType) {
                case 0:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_function_nav, parent, false);
                    viewHolder0 = new ViewHolder0(convertView);
                    convertView.setTag(viewHolder0);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_item1, parent, false);
                    viewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(viewHolder1);

                    viewHolder1.initGridView();

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, ViewPointDetailActivity.class));
                        }
                    });
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    viewHolder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
            }
        }


        switch (itemViewType) {
            case 0:
                viewHolder0.navigationTabLayout.setData(R.array.nav_trading_menu);
                viewHolder0.navigationTabLayout.setOnTabSelectListener(this);
                viewHolder0.navigationTabLayout.setCurrentTab(navigationTabClickPosition);
                break;
            case 1:
                viewHolder1.tvContent.convertToGif(viewPointTradeBean.content);
                viewHolder1.tvNickName.setText(viewPointTradeBean.author_name);

                CharSequence formatCreateTime = DateFormat.format("MM-dd HH:mm", viewPointTradeBean.time * 1000);
                viewHolder1.tvTime.setText(formatCreateTime.toString());

                viewHolder1.setAuthorImage(viewPointTradeBean.author_img);

//                viewHolder1.gridViewNotifyDataSetChanged(strings);
                break;
        }


        return convertView;
    }


    @Override
    public void onTabSelect(int position, int clickId) {
        navigationTabClickPosition = position;
        if (clickId == 0) {
            notifyDataSetChanged();
        }
    }

    class ViewHolder0 {
        @BindView(R.id.ntl_title_view) NavigationTabLayout navigationTabLayout;

        private View contentView;

        public ViewHolder0(View contentView) {
            this.contentView = contentView;
            ButterKnife.bind(this, contentView);
        }
    }

    class ViewHolder1 {
        @BindView(R.id.riv_user_avatar) RoundImageView rivUserAvatar;
        @BindView(R.id.tv_nick_name) TextView tvNickName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.viewpoint_function_share) ImageView viewpointFunctionShare;
        @BindView(R.id.viewpoint_title) EmoticonSimpleTextView tvContent;
        @BindView(R.id.viewpoint_picture_layout) GridView gridPictureLayout;
        @BindView(R.id.viewpoint_function_zan) LinearLayout viewpointFunctionZan;
        @BindView(R.id.rl_content_item) RelativeLayout rlContentItem;

        ViewHolder1(View contentView) {
            ButterKnife.bind(this, contentView);
        }

        void setAuthorImage(String authorImageUrl) {
            Glide.with(mContext)
                    .load(authorImageUrl)
                    .asBitmap()
                    .override(80, 80).into(
                    new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                glideAnimation) {
                            rivUserAvatar.setImageBitmap(resource);
                        }
                    });
        }

        /**
         * 初始化GridView 控件
         */
        void initGridView() {
            gridPictureLayout.setMotionEventSplittingEnabled(false);
            gridPictureLayout.setNumColumns(3);
            gridPictureLayout.setBackgroundColor(Color.TRANSPARENT);
            gridPictureLayout.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridPictureLayout.setCacheColorHint(0);
            gridPictureLayout.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridPictureLayout.setGravity(Gravity.CENTER);
            gridPictureLayout.setVerticalScrollBarEnabled(false);
        }

        void gridViewNotifyDataSetChanged(List<String> gridList) {
            BaseListAdapter<String> pictureAdapter = new BaseListAdapter<String>(gridList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder mViewHolder;

                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.view_viewpoint_item_picture, parent, false);
                        mViewHolder = new ViewHolder();
                        convertView.setTag(mViewHolder);

                        mViewHolder.mPictureView = (ImageView) convertView.findViewById(R.id.item_picture);
                    } else {
                        mViewHolder = (ViewHolder) convertView.getTag();
                    }
                    mViewHolder.mPictureView.setBackgroundColor(Color.BLUE);
                    return convertView;
                }

                class ViewHolder {
                    ImageView mPictureView;
                }
            };
            gridPictureLayout.setAdapter(pictureAdapter);
        }


    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        return viewPointTradeBean.getItemViewType();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }
}
