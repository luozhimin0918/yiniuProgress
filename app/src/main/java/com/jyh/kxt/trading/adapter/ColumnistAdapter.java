package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.library.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/1.
 */

public class ColumnistAdapter extends BaseListAdapter<ColumnistListJson> {

    private Context mContext;
    private String searchKey;

    private int defaultDayColor_name = Color.parseColor("#FF2E3239");
    private int defaultNightColor_name = Color.parseColor("#FF909090");
    private int keyDayColor = Color.parseColor("#FF1C9CF2");
    private int keyNightColor = Color.parseColor("#FF136AA4");

    public ColumnistAdapter(List<ColumnistListJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_trading_columnist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        ColumnistListJson bean = dataList.get(position);

        Glide.with(mContext).load(bean.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap
                .icon_user_def_photo)
                .into(new ImageViewTarget<Bitmap>(holder.rivAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        holder.rivAvatar.setImageBitmap(resource);
                    }
                });

        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        String name = bean.getName();
        if (isNight) {
            if (searchKey != null && !"".equals(searchKey)) {
                if (name != null && name.contains(searchKey)) {
                    String before = name.substring(0, name.indexOf(searchKey));
                    String end = name.substring(name.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultNightColor_name + "'>" + before + "</font><font color='" + keyNightColor +
                            "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultNightColor_name +
                            "'>" + end + "</font>";
                    holder.tvName.setText(Html.fromHtml(content));
                } else {
                    holder.tvName.setText(name);
                    holder.tvName.setTextColor(defaultNightColor_name);
                }
            } else {
                holder.tvName.setText(name);
                holder.tvName.setTextColor(defaultNightColor_name);
            }
        } else {
            if (searchKey != null && !"".equals(searchKey)) {
                if (name != null && name.contains(searchKey)) {
                    String before = name.substring(0, name.indexOf(searchKey));
                    String end = name.substring(name.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultDayColor_name + "'>" + before + "</font><font color='" + keyDayColor + "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultDayColor_name +
                            "'>" + end + "</font>";
                    holder.tvName.setText(Html.fromHtml(content));
                } else {
                    holder.tvName.setText(name);
                    holder.tvName.setTextColor(defaultDayColor_name);
                }
            } else {
                holder.tvName.setText(name);
                holder.tvName.setTextColor(defaultDayColor_name);
            }
        }

        String point_num = bean.getPoint_num();
        String article_num = bean.getArticle_num();
        String num_fans = bean.getNum_fans();
        holder.tvViewpoint.setText("观点数: " + (point_num == null || point_num.equals("null") ? 0 : point_num));
        holder.tvArticle.setText("文章数: " + (article_num == null || article_num.equals("null") ? 0 : article_num));
        holder.tvFans.setText("粉丝: " + (num_fans == null || num_fans.equals("null") ? 0 : num_fans));

        setTheme(holder);

        return convertView;
    }

    private void setTheme(ViewHolder holder) {
        holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.tvFans.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.tvArticle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.tvViewpoint.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background3));
    }

    public void setData(List<ColumnistListJson> jsons) {
        dataList.clear();
        dataList.addAll(jsons);
        notifyDataSetChanged();
    }

    public void addData(List<ColumnistListJson> jsons) {
        dataList.addAll(jsons);
        notifyDataSetChanged();
    }

    public List<ColumnistListJson> getData() {
        return dataList;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_viewpoint) TextView tvViewpoint;
        @BindView(R.id.tv_article) TextView tvArticle;
        @BindView(R.id.tv_fans) TextView tvFans;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
