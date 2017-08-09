package com.jyh.kxt.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.library.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:行情
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class QuoteAdapter extends BaseListAdapter<QuoteItemJson> {

    private Context mContext;
    private String searchKey;

    private int defaultDayColor_name = Color.parseColor("#FF2E3239");
    private int defaultNightColor_name = Color.parseColor("#FF909090");
    private int defaultDayColor_code = Color.parseColor("#FF999999");
    private int defaultNightColor_code = Color.parseColor("#FF444444");
    private int keyDayColor = Color.parseColor("#FF1C9CF2");
    private int keyNightColor = Color.parseColor("#FF136AA4");

    public QuoteAdapter(Context context, List<QuoteItemJson> dataList) {
        super(dataList);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_quote, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuoteItemJson quote = dataList.get(position);

        setTheme(holder, quote);

        return convertView;
    }

    private void setTheme(ViewHolder holder, QuoteItemJson quote) {
        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        String name = quote.getName();
        String code = quote.getCode();
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
                if (code != null && code.contains(searchKey)) {
                    String before = code.substring(0, code.indexOf(searchKey));
                    String end = code.substring(code.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultNightColor_code + "'>" + before + "</font><font color='" + keyNightColor +
                            "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultNightColor_code +
                            "'>" + end + "</font>";
                    holder.tvCode.setText(Html.fromHtml(content));
                } else {
                    holder.tvCode.setText(code);
                    holder.tvCode.setTextColor(defaultNightColor_code);
                }
            } else {
                holder.tvName.setTextColor(defaultNightColor_name);
                holder.tvCode.setTextColor(defaultNightColor_code);
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
                if (code != null && code.contains(searchKey)) {
                    String before = code.substring(0, code.indexOf(searchKey));
                    String end = code.substring(code.indexOf(searchKey) + searchKey.length());
                    String content = "<font color='" + defaultDayColor_code + "'>" + before + "</font><font color='" + keyDayColor +
                            "'>"
                            + searchKey +
                            "</font><font " +
                            "color='" + defaultDayColor_code +
                            "'>" + end + "</font>";
                    holder.tvCode.setText(Html.fromHtml(content));
                } else {
                    holder.tvCode.setText(code);
                    holder.tvCode.setTextColor(defaultDayColor_code);
                }
            } else {
                holder.tvName.setTextColor(defaultDayColor_name);
                holder.tvCode.setTextColor(defaultDayColor_code);
            }
        }
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setData(List data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.v_line) View vLine;
        @BindView(R.id.tv_code) TextView tvCode;
        @BindView(R.id.iv_zx) ImageView ivZx;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
