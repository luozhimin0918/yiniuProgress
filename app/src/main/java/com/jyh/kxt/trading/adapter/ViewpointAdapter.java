package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.widget.ThumbView2;
import com.jyh.kxt.trading.json.ViewpointJson;
import com.library.util.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointAdapter extends BaseListAdapter<ViewpointJson> {

    private List<ViewpointJson> list;
    private Context context;

    private int ImgPadding;//图片间隔
    private int imgWidth;//图片宽度

    public ViewpointAdapter(Context context, List<ViewpointJson> dataList) {
        super(dataList);
        this.list = dataList;
        this.context = context;
        ImgPadding = SystemUtil.dp2px(context, 10);

        //屏幕宽度减去左右间隔，再减去图片间隔，然后再均分3分
        imgWidth = (SystemUtil.getScreenDisplay(context).widthPixels - SystemUtil.dp2px(context, 15 * 2 + 10 * 2)) / 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_viewpoint, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ViewpointJson bean = list.get(position);

        setTheme(viewHolder);
        setData(viewHolder, bean);
        onClickListener(viewHolder, bean);

        return convertView;
    }

    /**
     * @param viewHolder
     * @param bean
     */
    private void onClickListener(ViewHolder viewHolder, ViewpointJson bean) {
        viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.llPinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * @param viewHolder
     */
    private void setTheme(ViewHolder viewHolder) {

    }

    /**
     * 设置数据
     *
     * @param viewHolder
     * @param bean
     */
    private void setData(ViewHolder viewHolder, ViewpointJson bean) {
        if (bean == null) return;
        RequestManager requestManager = Glide.with(context);
        viewHolder.tvName.setText(bean.getAuthor());
        viewHolder.tvContent.setText(bean.getContent());
        viewHolder.tvPinglun.setText(bean.getNum_commend());
        viewHolder.tvTime.setText(bean.getTime());
        requestManager.load(bean.getAuthor_picture()).error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap.icon_user_def_photo).into
                (viewHolder.rivAvatar);
        String type = "";
        boolean isGood = NativeStore.isThumbSucceed(context, type, bean.getAuthor_id());
        viewHolder.tvThumb.setThumbCount(Integer.parseInt(bean.getNum_like()), bean.getAuthor_id(), type, isGood);
        List<String> imgs = bean.getImgs();
        setImgs(viewHolder, requestManager, imgs);
    }

    /**
     * 设置图片
     *
     * @param viewHolder
     * @param requestManager
     * @param imgs
     */
    private void setImgs(ViewHolder viewHolder, RequestManager requestManager, List<String> imgs) {
        if (imgs == null || imgs.size() == 0)
            return;
        int size = imgs.size();
        viewHolder.ivRootView.removeAllViews();
        if (size >= 9) {
            for (int i = 0; i < 9; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgWidth, ViewGroup
                        .LayoutParams.MATCH_PARENT);

                if (i != 3)
                    params.rightMargin = ImgPadding;
                if (i / 3 >= 1)
                    params.topMargin = ImgPadding;

                viewHolder.ivRootView.addView(imageView, params);
                requestManager.load(imgs.get(i)).error(R.mipmap.icon_video_defualt_photo).placeholder(R.mipmap.icon_video_defualt_photo)
                        .into
                                (imageView);
            }
        } else {
            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgWidth, ViewGroup
                        .LayoutParams.MATCH_PARENT);

                if (i != 3)
                    params.rightMargin = ImgPadding;
                if (i / 3 >= 1)
                    params.topMargin = ImgPadding;

                viewHolder.ivRootView.addView(imageView, params);
                requestManager.load(imgs.get(i)).error(R.mipmap.icon_video_defualt_photo).placeholder(R.mipmap.icon_video_defualt_photo)
                        .into
                                (imageView);
            }
        }
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_pinglun) TextView tvPinglun;
        @BindView(R.id.iv_more) ImageView ivMore;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.iv_rootView) GridLayout ivRootView;

        @BindView(R.id.tv_thumb) ThumbView2 tvThumb;
        @BindView(R.id.ll_dianzan) LinearLayout llDianzan;
        @BindView(R.id.ll_pinglun) LinearLayout llPinglun;
        @BindView(R.id.ll_share) LinearLayout llShare;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

}
