package com.jyh.kxt.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.library.util.SPUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/14.
 */

public class AutoCompleteAdapter extends BaseListAdapter<QuoteItemJson> implements Filterable {

    private int defaultDayColor_name = Color.parseColor("#FF2E3239");
    private int defaultNightColor_name = Color.parseColor("#FF909090");
    private int defaultDayColor_code = Color.parseColor("#FF999999");
    private int defaultNightColor_code = Color.parseColor("#FF444444");
    private int keyDayColor = Color.parseColor("#FF1C9CF2");
    private int keyNightColor = Color.parseColor("#FF136AA4");

    private Context mContext;
    private Filter filter;
    private List<QuoteItemJson> backup;
    private String key;

    public AutoCompleteAdapter(List<QuoteItemJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_autocomplete_quote, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        QuoteItemJson quote = dataList.get(position);
        setTheme(holder, quote);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (dataList == null) return null;
                    if (backup == null)
                        backup = new ArrayList<>(dataList);
                    FilterResults results = new FilterResults();
                    if (!TextUtils.isEmpty(constraint)) {
                        List entries = new ArrayList<>();
                        for (QuoteItemJson entry : backup) {
                            String pinyin = entry.getPinyin();
                            String letter = entry.getLetter();
                            String name = entry.getName();
                            String code = entry.getCode();
                            String cs = constraint.toString();
                            key = cs;
                            if (cs != null) {
                                if (name != null && pinyin != null && code != null && letter != null) {
                                    if (name.contains(cs) || pinyin.contains(cs) || code.contains(cs) || letter.contains(cs)) {
                                        entries.add(entry);
                                    }
                                } else if (cs.equals(name) || cs.equals(code) || cs.equals(letter) || cs.equals(pinyin)) {
                                    entries.add(entry);
                                }
                            }
                        }
                        results.count = entries.size();
                        results.values = entries;
                    } else {
                        results.count = 0;
                        results.values = new ArrayList<>();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results == null) return;
                    dataList.clear();
                    dataList.addAll((Collection<? extends QuoteItemJson>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }

    private void setTheme(ViewHolder holder, QuoteItemJson quote) {
        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        String name = quote.getName();
        String code = quote.getCode();
        holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.font_color3));
        holder.tvContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
        holder.tvTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
        if (isNight) {
            if (key != null && !"".equals(key)) {
                if (name != null && name.contains(key)) {
                    String before = name.substring(0, name.indexOf(key));
                    String end = name.substring(name.indexOf(key) + key.length());
                    String content = "<font color='" + defaultNightColor_name + "'>" + before + "</font><font color='" + keyNightColor +
                            "'>"
                            + key +
                            "</font><font " +
                            "color='" + defaultNightColor_name +
                            "'>" + end + "</font>";
                    holder.tvTitle.setText(Html.fromHtml(content));
                } else {
                    holder.tvTitle.setText(name);
                    holder.tvTitle.setTextColor(defaultNightColor_name);
                }
                if (code != null && code.contains(key)) {
                    String before = code.substring(0, code.indexOf(key));
                    String end = code.substring(code.indexOf(key) + key.length());
                    String content = "<font color='" + defaultNightColor_code + "'>" + before + "</font><font color='" + keyNightColor +
                            "'>"
                            + key +
                            "</font><font " +
                            "color='" + defaultNightColor_code +
                            "'>" + end + "</font>";
                    holder.tvContent.setText(Html.fromHtml(content));
                } else {
                    holder.tvContent.setText(code);
                    holder.tvContent.setTextColor(defaultNightColor_code);
                }
            } else {
                holder.tvTitle.setTextColor(defaultNightColor_name);
                holder.tvContent.setTextColor(defaultNightColor_code);
                holder.tvTitle.setText(name);
                holder.tvContent.setText(code);
            }
        } else {
            if (key != null && !"".equals(key)) {
                if (name != null && name.contains(key)) {
                    String before = name.substring(0, name.indexOf(key));
                    String end = name.substring(name.indexOf(key) + key.length());
                    String content = "<font color='" + defaultDayColor_name + "'>" + before + "</font><font color='" + keyDayColor + "'>"
                            + key +
                            "</font><font " +
                            "color='" + defaultDayColor_name +
                            "'>" + end + "</font>";
                    holder.tvTitle.setText(Html.fromHtml(content));
                } else {
                    holder.tvTitle.setText(name);
                    holder.tvTitle.setTextColor(defaultDayColor_name);
                }
                if (code != null && code.contains(key)) {
                    String before = code.substring(0, code.indexOf(key));
                    String end = code.substring(code.indexOf(key) + key.length());
                    String content = "<font color='" + defaultDayColor_code + "'>" + before + "</font><font color='" + keyDayColor +
                            "'>"
                            + key +
                            "</font><font " +
                            "color='" + defaultDayColor_code +
                            "'>" + end + "</font>";
                    holder.tvContent.setText(Html.fromHtml(content));
                } else {
                    holder.tvContent.setText(code);
                    holder.tvContent.setTextColor(defaultDayColor_code);
                }
            } else {
                holder.tvTitle.setTextColor(defaultDayColor_name);
                holder.tvContent.setTextColor(defaultDayColor_code);
                holder.tvTitle.setText(name);
                holder.tvContent.setText(code);
            }
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.ll_rootView) LinearLayout itemView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
