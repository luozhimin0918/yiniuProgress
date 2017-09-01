package com.jyh.kxt.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.chat.json.LetterSysJson;
import com.library.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/29.
 */

public class LetterSysAdapter extends BaseListAdapter<LetterSysJson> {

    private Context mContext;

    public LetterSysAdapter(List<LetterSysJson> dataList, Context mContext) {
        super(dataList);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_letter_sys_content, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
        holder.tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
        holder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
        holder.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color6));

        Glide.with(mContext).load(R.mipmap.icon_msg_sys).asBitmap().centerCrop().into(new ImageViewTarget<Bitmap>(holder.rivAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.rivAvatar.setImageBitmap(resource);
            }
        });


        LetterSysJson bean = dataList.get(position);
        try {
            holder.tvTime.setText(DateUtils.transformTime(Long.parseLong(bean.getDatetime()) * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvTime.setText("00:00");
        }
        setURLSpan(holder.tvContent, bean);
        holder.tvTitle.setText("系统消息");

        return convertView;
    }

    /**
     * 设置超链接颜色
     *
     * @param tvContent
     * @param bean
     */
    private void setURLSpan(TextView tvContent, LetterSysJson bean) {
        String info = bean.getContent();
        //创建一个 SpannableString对象 注意：不要在xml配置android:autoLink属性
        SpannableString sp = new SpannableString(info);
        //这句很重要，也可以添加自定义正则表达式
        Linkify.addLinks(sp, Linkify.ALL);
        //主要是获取span的位置
        URLSpan[] spans = sp.getSpans(0, info.length(), URLSpan.class);
        //这里可以用过循环处理就可以动态实现文本颜色的差别化了
        int urlColor = ContextCompat.getColor(mContext, R.color.font_color_url);
        if (spans != null && spans.length > 0) {
            for (URLSpan span : spans) {
                sp.setSpan(new ForegroundColorSpan(urlColor), sp.getSpanStart(span), sp.getSpanEnd(span), Spannable
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        //设置斜体
//        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27, 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //SpannableString对象设置给TextView
        tvContent.setText(sp);
        //设置TextView可点击
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setData(List<LetterSysJson> data) {
        this.dataList.clear();
        this.dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<LetterSysJson> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.riv_avatar) RoundImageView rivAvatar;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.v_line) View vLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
