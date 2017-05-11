package com.jyh.kxt.user.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.CollectUtils;
import com.library.base.http.VarConstant;
import com.jyh.kxt.base.custom.RadianDrawable;
import com.jyh.kxt.base.utils.PingYinUtil;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.index.json.ConfigJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class CollectFlashAdapter extends BaseAdapter implements FastInfoPinnedListView.PinnedSectionListAdapter {

    private static final int TYPE_KX = 0;
    private static final int TYPE_RL = 1;
    private static final int TYPE_LEFT = 2;
    private static final int TYPE_RIGHT = 3;
    private static final int TYPE_TOP = 4;
    private static final int TYPE_BOTTOM = 5;

    private List flashJsons;
    private Context context;

    public CollectFlashAdapter(List<FlashJson> flashJsons, Context context) {
        this.flashJsons = flashJsons;
        initCollectStatus(1);
        this.context = context;
    }

    public void setData(List<FlashJson> flashJsons) {
        this.flashJsons.clear();
        this.flashJsons.addAll(flashJsons);
        initCollectStatus(1);
        notifyDataSetChanged();
    }


    public void addData(FlashJson flashJson) {
        this.flashJsons.add(1, flashJson);
        initCollectStatus(2);
        notifyDataSetChanged();
    }

    public void addData(List<FlashJson> flashJsons) {
        this.flashJsons.addAll(flashJsons);
        initCollectStatus(1);
        notifyDataSetChanged();
    }

    /**
     * 初始化收藏状态
     *
     * @param type
     */
    private void initCollectStatus(int type) {
    }

    public List<FlashJson> getData() {
        List data = new ArrayList(flashJsons);
        for (Object o : data) {
            if (o instanceof String) {
                data.remove(o);
            }
        }
        return new ArrayList<FlashJson>(data);
    }

    @Override
    public int getCount() {
        return flashJsons == null ? 0 : flashJsons.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        KXViewHolder kxHolder = null;
        NEWViewHolder topHolder = null;
        NEWViewHolder bottomHolder = null;
        NEWViewHolder leftHolder = null;
        NEWViewHolder rightHolder = null;
        RLViewHolder rlHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch (type) {
                case TYPE_KX:
                    convertView = inflater.inflate(R.layout.item_flash_news, parent, false);
                    kxHolder = new KXViewHolder(convertView);
                    convertView.setTag(kxHolder);
                    break;
                case TYPE_RL:
                    convertView = inflater.inflate(R.layout.item_flash_rl, parent, false);
                    rlHolder = new RLViewHolder(convertView);
                    convertView.setTag(rlHolder);
                    break;
                case TYPE_LEFT:
                    convertView = inflater.inflate(R.layout.item_flash_news_left, parent, false);
                    leftHolder = new NEWViewHolder(convertView);
                    convertView.setTag(leftHolder);
                    break;
                case TYPE_RIGHT:
                    convertView = inflater.inflate(R.layout.item_flash_news_right, parent, false);
                    rightHolder = new NEWViewHolder(convertView);
                    convertView.setTag(rightHolder);
                    break;
                case TYPE_TOP:
                    convertView = inflater.inflate(R.layout.item_flash_news_top, parent, false);
                    topHolder = new NEWViewHolder(convertView);
                    convertView.setTag(topHolder);
                    break;
                case TYPE_BOTTOM:
                    convertView = inflater.inflate(R.layout.item_flash_news_bottom, parent, false);
                    bottomHolder = new NEWViewHolder(convertView);
                    convertView.setTag(bottomHolder);
                    break;
            }
        } else {
            try {
                switch (type) {
                    case TYPE_KX:
                        kxHolder = (KXViewHolder) convertView.getTag();
                        break;
                    case TYPE_RL:
                        rlHolder = (RLViewHolder) convertView.getTag();
                        break;
                    case TYPE_LEFT:
                        leftHolder = (NEWViewHolder) convertView.getTag();
                        break;
                    case TYPE_RIGHT:
                        rightHolder = (NEWViewHolder) convertView.getTag();
                        break;
                    case TYPE_TOP:
                        topHolder = (NEWViewHolder) convertView.getTag();
                        break;
                    case TYPE_BOTTOM:
                        bottomHolder = (NEWViewHolder) convertView.getTag();
                        break;
                }
            } catch (Exception e) {
                LayoutInflater inflater = LayoutInflater.from(context);
                switch (type) {
                    case TYPE_KX:
                        convertView = inflater.inflate(R.layout.item_flash_news, parent, false);
                        kxHolder = new KXViewHolder(convertView);
                        convertView.setTag(kxHolder);
                        break;
                    case TYPE_RL:
                        convertView = inflater.inflate(R.layout.item_flash_rl, parent, false);
                        rlHolder = new RLViewHolder(convertView);
                        convertView.setTag(rlHolder);
                        break;
                    case TYPE_LEFT:
                        convertView = inflater.inflate(R.layout.item_flash_news_left, parent, false);
                        leftHolder = new NEWViewHolder(convertView);
                        convertView.setTag(leftHolder);
                        break;
                    case TYPE_RIGHT:
                        convertView = inflater.inflate(R.layout.item_flash_news_right, parent, false);
                        rightHolder = new NEWViewHolder(convertView);
                        convertView.setTag(rightHolder);
                        break;
                    case TYPE_TOP:
                        convertView = inflater.inflate(R.layout.item_flash_news_top, parent, false);
                        topHolder = new NEWViewHolder(convertView);
                        convertView.setTag(topHolder);
                        break;
                    case TYPE_BOTTOM:
                        convertView = inflater.inflate(R.layout.item_flash_news_bottom, parent, false);
                        bottomHolder = new NEWViewHolder(convertView);
                        convertView.setTag(bottomHolder);
                        break;
                }
            }
        }

        switch (type) {
            case TYPE_KX:
                FlashJson flash = (FlashJson) flashJsons.get(position);
                Flash_KX kx = JSON.parseObject(flash.getContent().toString(), Flash_KX.class);
                String time = "00:00";
                try {
                    time = getTime(kx.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                kxHolder.tvTime.setText(time);
                kxHolder.tvContent.setText(getString(kx.getTitle()));
                kxHolder.tvMore.setVisibility(View.VISIBLE);
                kxHolder.ivMore.setVisibility(View.VISIBLE);
                setOnclick(kxHolder.tvMore, kxHolder.ivMore, kxHolder.ivShare, kxHolder.ivCollect, position, kxHolder.tvContent, null,
                        null, TYPE_KX);

                if (VarConstant.IMPORTANCE_HIGH.equals(kx.getImportance())) {
                    kxHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                } else {
                    kxHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                }

                kxHolder.ivCollect.setSelected(flash.isColloct());

                break;
            case TYPE_RL:
                FlashJson flash_rl = (FlashJson) flashJsons.get(position);
                Flash_RL rl = JSON.parseObject(flash_rl.getContent().toString(), Flash_RL.class);

                boolean onlyShowHigh = SPUtils.getBoolean(context, SpConstant.FLASH_FILTRATE_HIGH);

                Glide.with(context).load(String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(rl.getState()))).into(rlHolder
                        .ivFlag);

                String time2 = "00:00";
                try {
                    time2 = getTime(rl.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rlHolder.tvTime.setText(time2);

                rlHolder.tvTitle.setText(getString(rl.getTitle()));
                rlHolder.tvContent.setText(context.getResources().getString(R.string.date_describe, rl.getBefore(), rl.getForecast(), rl
                        .getReality()));
                rlHolder.tvMore.setVisibility(View.GONE);
                rlHolder.ivMore.setVisibility(View.GONE);

                /**
                 * 前值 后值 等
                 */
                String describe = context.getResources().getString(R.string.date_describe,
                        rl.getBefore(),
                        rl.getForecast(),
                        rl.getReality());

                String reality = rl.getReality();
                setDescribeForegroundColor(rlHolder.tvContent, describe, reality);

                /**
                 *  重要程度
                 */
                rlHolder.star.setImportance(rl.getImportance());

                /**
                 * 公布状态, 已公布,未公布 利多 ,金银 , 石油   影响较小等
                 */
                setAlarmState(reality,
                        Integer.parseInt(rl.getEffecttype()),//0 利多美元  1 利多金银石油 2 影响较小
                        rlHolder.llExponent);


                setOnclick(rlHolder.tvMore, rlHolder.ivMore, rlHolder.ivShare, rlHolder.ivCollect, position, rlHolder.tvContent, null,
                        null, TYPE_RL);
                /**
                 * 重要性判断
                 */
                if (onlyShowHigh) {
                    if (VarConstant.IMPORTANCE_HIGH.equals(rl.getImportance())) {
                        rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                    } else {
                        rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                        return null;
                    }
                } else {
                    if (VarConstant.IMPORTANCE_HIGH.equals(rl.getImportance())) {
                        rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                    } else {
                        rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                    }
                }

                rlHolder.ivCollect.setSelected(flash_rl.isColloct());

                break;
            case TYPE_LEFT:
                FlashJson flash_left = (FlashJson) flashJsons.get(position);
                Flash_NEWS left = JSON.parseObject(flash_left.getContent().toString(), Flash_NEWS.class);

                Glide.with(context).load(left.getImage()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(leftHolder
                        .ivFlash);

                Set<String> set = SPUtils.getStringSet(context, SpConstant.FLASH_FILTRATE);

                String time3 = "00:00";
                try {
                    time3 = getTime(left.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                leftHolder.tvTime.setText(time3);
                leftHolder.tvContent.setText(getString(left.getTitle()));
                leftHolder.tvMore.setVisibility(View.GONE);
                leftHolder.ivMore.setVisibility(View.GONE);

                if (VarConstant.IMPORTANCE_HIGH.equals(left.getImportance())) {
                    leftHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                } else {
                    leftHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                }

                setOnclick(leftHolder.tvMore, leftHolder.ivMore, leftHolder.ivShare, leftHolder.ivCollect, position, leftHolder
                        .tvContent, VarConstant.SOCKET_FLASH_LEFT, null, TYPE_LEFT);

                leftHolder.ivCollect.setSelected(flash_left.isColloct());
                break;
            case TYPE_RIGHT:
                FlashJson flash_right = (FlashJson) flashJsons.get(position);
                Flash_NEWS right = JSON.parseObject(flash_right.getContent().toString(), Flash_NEWS.class);

                Glide.with(context).load(right.getImage()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(rightHolder
                        .ivFlash);

                String time4 = "00:00";
                try {
                    time4 = getTime(right.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rightHolder.tvTime.setText(time4);
                rightHolder.tvContent.setText(getString(right.getTitle()));
                rightHolder.tvMore.setVisibility(View.GONE);
                rightHolder.ivMore.setVisibility(View.GONE);

                if (VarConstant.IMPORTANCE_HIGH.equals(right.getImportance())) {
                    rightHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                } else {
                    rightHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                }

                setOnclick(rightHolder.tvMore, rightHolder.ivMore, rightHolder.ivShare, rightHolder.ivCollect, position, rightHolder
                        .tvContent, VarConstant.SOCKET_FLASH_RIGHT, null, TYPE_RIGHT);

                rightHolder.ivCollect.setSelected(flash_right.isColloct());

                break;
            case TYPE_TOP:
                FlashJson flash_top = (FlashJson) flashJsons.get(position);
                Flash_NEWS top = JSON.parseObject(flash_top.getContent().toString(), Flash_NEWS.class);

                Glide.with(context).load(top.getImage()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(topHolder
                        .ivFlash);

                String time5 = "00:00";
                try {
                    time5 = getTime(top.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                topHolder.tvTime.setText(time5);
                topHolder.tvContent.setText(getString(top.getTitle()));
                topHolder.tvContent.setVisibility(View.GONE);
                topHolder.tvMore.setVisibility(View.VISIBLE);
                topHolder.ivMore.setVisibility(View.VISIBLE);

                if (VarConstant.IMPORTANCE_HIGH.equals(top.getImportance())) {
                    topHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                } else {
                    topHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                }

                setOnclick(topHolder.tvMore, topHolder.ivMore, topHolder.ivShare, topHolder.ivCollect, position, topHolder.tvContent,
                        VarConstant.SOCKET_FLASH_TOP, topHolder.ivFlash, TYPE_TOP);

                topHolder.ivCollect.setSelected(flash_top.isColloct());

                break;
            case TYPE_BOTTOM:
                FlashJson flash_bottom = (FlashJson) flashJsons.get(position);
                Flash_NEWS bottom = JSON.parseObject(flash_bottom.getContent().toString(), Flash_NEWS.class);

                Glide.with(context).load(bottom.getImage()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into
                        (bottomHolder
                                .ivFlash);

                String time6 = "00:00";
                try {
                    time6 = getTime(bottom.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bottomHolder.tvTime.setText(time6);
                bottomHolder.tvContent.setText(getString(bottom.getTitle()));

                bottomHolder.ivFlash.setVisibility(View.GONE);
                bottomHolder.tvMore.setVisibility(View.VISIBLE);
                bottomHolder.ivMore.setVisibility(View.VISIBLE);

                if (VarConstant.IMPORTANCE_HIGH.equals(bottom.getImportance())) {
                    bottomHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                } else {
                    bottomHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
                }

                setOnclick(bottomHolder.tvMore, bottomHolder.ivMore, bottomHolder.ivShare, bottomHolder.ivCollect, position, bottomHolder
                        .tvContent, VarConstant.SOCKET_FLASH_BOTTOM, bottomHolder.ivFlash, TYPE_BOTTOM);

                bottomHolder.ivCollect.setSelected(flash_bottom.isColloct());

                break;
        }

        return convertView;
    }

    /**
     * 绑定点击事件
     *
     * @param tvMore
     * @param ivMore
     * @param ivShare
     * @param ivCollect
     * @param ivFlash
     * @param type
     */
    private void setOnclick(final TextView tvMore, final ImageView ivMore, final ImageView ivShare, final ImageView ivCollect, int position,
                            final TextView content,
                            String weizhi, final ImageView ivFlash, final int type) {
        final FlashJson flash = (FlashJson) flashJsons.get(position);

        ivCollect.setVisibility(View.GONE);

        showMore(tvMore, ivMore, flash.isShowMore(), content, ivFlash, type);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flash.isShowMore()) {
                    flash.setShowMore(false);
                    showMore(tvMore, ivMore, false, content, ivFlash, type);
                } else {
                    flash.setShowMore(true);
                    showMore(tvMore, ivMore, true, content, ivFlash, type);
                }
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String configStr = SPUtils.getString(context, SpConstant.CONFIG);
                    ConfigJson config = JSON.parseObject(configStr, ConfigJson.class);
                    String url_kx_share = config.getUrl_kx_share();

                    String shareUrl = url_kx_share.replace("{id}", flash.getSocre());
                    String code = flash.getCode();
                    String title = "";
                    String discription = "";
                    String image = null;
                    String id = flash.getSocre();
                    switch (code) {
                        case VarConstant.SOCKET_FLASH_KUAIXUN:
                            Flash_KX kx = JSON.parseObject(flash.getContent().toString(), Flash_KX.class);
                            title = kx.getTitle();
                            break;
                        case VarConstant.SOCKET_FLASH_CJRL:
                            Flash_RL rl = JSON.parseObject(flash.getContent().toString(), Flash_RL.class);
                            title = rl.getTitle();
                            discription = context.getResources().getString(R.string.date_describe,
                                    rl.getBefore(),
                                    rl.getForecast(),
                                    rl.getReality());
                            break;
                        case VarConstant.SOCKET_FLASH_KXTNEWS:
                            Flash_NEWS news = JSON.parseObject(flash.getContent().toString(), Flash_NEWS.class);
                            title = news.getTitle();
                            discription = news.getDescription();
                            image = news.getImage();
                            break;
                    }

                    UmengShareTool.initUmengLayout((BaseActivity) context, new ShareJson(title, shareUrl, discription, image, null,
                            UmengShareTool.TYPE_DEFAULT, null, null, null, false, false), flash, ivShare, null);

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastView.makeText3(context, "分享失败");
                }
            }
        });
    }

    /**
     * 展视或隐藏更多
     *
     * @param tvMore
     * @param ivMore
     * @param isShowMore
     * @param content    内容
     * @param ivFlash    图片
     * @param type       位置
     */
    private void showMore(TextView tvMore, ImageView ivMore, boolean isShowMore, TextView content, ImageView ivFlash, int type) {
        if (isShowMore) {
            ivMore.setSelected(true);
            tvMore.setText("收起");
            if (content != null)
                content.setMaxLines(Integer.MAX_VALUE);
            switch (type) {
                case TYPE_TOP:
                    content.setVisibility(View.VISIBLE);
                    break;
                case TYPE_BOTTOM:
                    ivFlash.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            ivMore.setSelected(false);
            tvMore.setText("展开");
            if (content != null)
                content.setMaxLines(3);
            switch (type) {
                case TYPE_TOP:
                    content.setVisibility(View.GONE);
                    break;
                case TYPE_BOTTOM:
                    ivFlash.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 设置前值、预期值等
     *
     * @param tvDescribe
     * @param describe
     * @param reality
     */
    private void setDescribeForegroundColor(TextView tvDescribe, String describe, String reality) {
        try {
            float realityFloat = Float.parseFloat(reality.replace("%", ""));
            int index = describe.lastIndexOf(":") + 1;
            SpannableString spannableString = new SpannableString(describe);
            if (realityFloat > 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.rise_color)),
                        index,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            } else if (realityFloat < 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.decline_color)),
                        index,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                tvDescribe.setTextColor(ContextCompat.getColor(context, R.color.unaltered_color));
            }
        } catch (NumberFormatException e) {
        } finally {
            tvDescribe.setText(describe);
        }
    }

    private void setAlarmState(String reality, int effectType, LinearLayout llExponent) {

        llExponent.removeAllViews();


        String effect = "||";
        switch (effectType) {
            case 0:
                effect = "美元|金银 石油|";
                break;
            case 1:
                effect = "金银 石油|美元|";
                break;
            case 2:
                effect = "||";
                break;
        }


        RadianDrawable radianDrawable = new RadianDrawable(context);

        try {
            Float.parseFloat(reality.replace("%", ""));

            radianDrawable.setStroke(R.color.line_color);

            if (effectType != 2) {
                String[] effectSplit = effect.split("\\|");
                for (int i = 0; i < effectSplit.length; i++) {
                    drawingShapeColor(i, effectType, effectSplit[i], llExponent);
                }
            } else {
                drawingShapeColor(2, effectType, "影响较小", llExponent);
            }

        } catch (Exception e) {

            RadianDrawable effectDrawable = new RadianDrawable(context);

            TextView textView = generateTextView();
            textView.setText("未公布");

            effectDrawable.setStroke(R.color.line_color);
            textView.setTextColor(ContextCompat.getColor(context, R.color.line_color));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);
        }
    }

    private void drawingShapeColor(int type, int effectType, String effect, LinearLayout llExponent) {
        int shapeColor = 0;
        Drawable leftDrawable = null;

        switch (type) {
            case 0:
                shapeColor = R.color.calendar_line0;
                leftDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_top_red);
                break;
            case 1:
                shapeColor = R.color.calendar_line1;
                leftDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_decline_green);
                break;
            case 2:
                shapeColor = R.color.calendar_line2;
                leftDrawable = null;
                break;
        }

        if (effectType != 2) {
            if (effect != null && !"".equals(effect.trim())) {
                String[] effect0Split = effect.split(" ");

                for (int i = 0; i < effect0Split.length; i++) {
                    RadianDrawable effectDrawable = new RadianDrawable(context);

                    TextView textView = generateTextView();
                    textView.setText(effect0Split[i]);

                    effectDrawable.setStroke(shapeColor);
                    textView.setTextColor(ContextCompat.getColor(context, shapeColor));

                    textView.setBackground(effectDrawable);

                    textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                    llExponent.addView(textView);
                }
            }
        } else {
            RadianDrawable effectDrawable = new RadianDrawable(context);

            TextView textView = generateTextView();
            textView.setText(effect);

            effectDrawable.setStroke(shapeColor);
            textView.setTextColor(ContextCompat.getColor(context, shapeColor));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);
        }
    }

    private TextView generateTextView() {
        int padding = SystemUtil.dp2px(context, 5);
        int MarginsRight = SystemUtil.dp2px(context, 10);
        int minWidth = SystemUtil.dp2px(context, 55);

        TextView itemView = new TextView(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(layoutParams);

        layoutParams.setMargins(0, 0, MarginsRight, 0);
        itemView.setPadding(padding, padding, padding, padding);
        itemView.setMinWidth(minWidth);

        itemView.setTextSize(11);
        itemView.setGravity(Gravity.CENTER);
        return itemView;
    }

    @Override
    public int getItemViewType(int position) {
        FlashJson flashJson = (FlashJson) flashJsons.get(position);
        String code = flashJson.getCode();
        switch (code) {
            case VarConstant.SOCKET_FLASH_KUAIXUN:
                return TYPE_KX;
            case VarConstant.SOCKET_FLASH_CJRL:
                return TYPE_RL;
            case VarConstant.SOCKET_FLASH_KXTNEWS:
                Flash_NEWS content = JSON.parseObject(flashJson.getContent().toString(), Flash_NEWS.class);
                int type;
                switch (content.getImage_pos()) {
                    case VarConstant.SOCKET_FLASH_LEFT:
                        //左
                        type = TYPE_LEFT;
                        break;
                    case VarConstant.SOCKET_FLASH_RIGHT:
                        //右
                        type = TYPE_RIGHT;
                        break;
                    case VarConstant.SOCKET_FLASH_TOP:
                        //顶部
                        type = TYPE_TOP;
                        break;
                    case VarConstant.SOCKET_FLASH_BOTTOM:
                        //底部
                        type = TYPE_BOTTOM;
                        break;
                    default:
                        type = TYPE_LEFT;
                        break;
                }
                return type;
            default:
                return TYPE_KX;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    private String getString(String str) {
        if (str == null) return null;
        return str.replace("<br />", "\n").replace("<br/>", "");
    }

    private String getTime(String time) throws Exception {
        String[] splitTime = time.split(" ");
        String[] splitTime2 = splitTime[1].split(":");
        return splitTime2[0] + ":" + splitTime2[1];
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }

    /**
     * 普通快讯
     */
    class KXViewHolder extends BaseViewHolder {
        public KXViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 图文
     */
    class NEWViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_flash)
        ImageView ivFlash;

        public NEWViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 日历
     */
    class RLViewHolder {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_describe) TextView tvContent;
        @BindView(R.id.tv_more) TextView tvMore;
        @BindView(R.id.iv_more) ImageView ivMore;
        @BindView(R.id.iv_share) ImageView ivShare;
        @BindView(R.id.iv_collect) ImageView ivCollect;
        @BindView(R.id.v_line) View vLine;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.ll_exponent) LinearLayout llExponent;
        @BindView(R.id.ll_star) StarView star;
        @BindView(R.id.iv_guoqi) ImageView ivFlag;

        public RLViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class BaseViewHolder {
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.tv_content) TextView tvContent;
        @BindView(R.id.tv_more) TextView tvMore;
        @BindView(R.id.iv_more) ImageView ivMore;
        @BindView(R.id.iv_share) ImageView ivShare;
        @BindView(R.id.iv_collect) ImageView ivCollect;
        @BindView(R.id.v_line) View vLine;
    }


}
