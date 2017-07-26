package com.jyh.kxt.datum.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.BinderThread;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RadianDrawable;
import com.jyh.kxt.base.impl.OnRequestPermissions;
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;
import com.jyh.kxt.base.utils.ColorFormatUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.PingYinUtil;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.datum.bean.CalendarHolidayBean;
import com.jyh.kxt.datum.bean.CalendarImportantBean;
import com.jyh.kxt.datum.bean.CalendarNotBean;
import com.jyh.kxt.datum.bean.CalendarTitleBean;
import com.jyh.kxt.datum.bean.CalendarType;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.CalendarItemFragment;
import com.jyh.kxt.index.json.AlarmJson;
import com.jyh.kxt.index.presenter.AlarmPresenter;
import com.jyh.kxt.index.ui.MainActivity;
import com.library.util.ObserverCall;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarItemAdapter extends BaseListAdapter<CalendarType> {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private CalendarItemFragment parentFragment;

    private String adIconDay, adIconNight;
    private String ad1TvColorDay = "#1384ED", ad1TvColorNight = "#1384ED", ad2TvColorDay = "#1384ED", ad2TvColorNight
            = "#1384ED";

    public CalendarItemAdapter(Context mContext, List<CalendarType> dataList) {
        super(dataList);
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setParentFragment(CalendarItemFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CalendarType mCalendarType = getItem(position);

        ViewHolder0 viewHolder0 = null;
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        ViewHolder3 viewHolder3 = null;
        ViewHolder4 viewHolder4 = null;

        int itemType = getItemViewType(position);
        if (view == null) {
            switch (itemType) {
                case 0:
                    view = layoutInflater.inflate(R.layout.item_calendar_title, null);
                    viewHolder0 = new ViewHolder0(view);
                    view.setTag(viewHolder0);
                    break;
                case 1:
                    view = layoutInflater.inflate(R.layout.item_calendar_content1, null);
                    viewHolder1 = new ViewHolder1(view);
                    view.setTag(viewHolder1);
                    break;
                case 2:
                    view = layoutInflater.inflate(R.layout.item_calendar_content2, null);
                    viewHolder2 = new ViewHolder2(view);
                    view.setTag(viewHolder2);
                    break;
                case 3:
                    view = layoutInflater.inflate(R.layout.item_calendar_content3, null);
                    viewHolder3 = new ViewHolder3(view);
                    view.setTag(viewHolder3);
                    break;
                case 4:
                    view = layoutInflater.inflate(R.layout.item_calendar_no_data, null);
                    viewHolder4 = new ViewHolder4(view);
                    view.setTag(viewHolder4);
                    break;
                default:
                    break;
            }
        } else {
            switch (itemType) {
                case 0:
                    viewHolder0 = (ViewHolder0) view.getTag();
                    break;
                case 1:
                    viewHolder1 = (ViewHolder1) view.getTag();
                    break;
                case 2:
                    viewHolder2 = (ViewHolder2) view.getTag();
                    break;
                case 3:
                    viewHolder3 = (ViewHolder3) view.getTag();
                    break;
                case 4:
                    viewHolder4 = (ViewHolder4) view.getTag();
                    break;
            }
        }


        switch (itemType) {
            case 0:
                CalendarTitleBean mCalendarTitleBean = (CalendarTitleBean) mCalendarType;
                viewHolder0.tvTitle.setText(mCalendarTitleBean.getName());
                viewHolder0.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color60));

                View paddingTagView = viewHolder0.llContent.findViewWithTag("paddingView");
                if (paddingTagView != null) {
                    viewHolder0.llContent.removeView(paddingTagView);
                }

                if (mCalendarTitleBean.getSpaceType() == 1) {
                    View mPaddingView = new View(mContext);
                    mPaddingView.setTag("paddingView");
                    mPaddingView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_color2));

                    int paddingHeight = SystemUtil.dp2px(mContext, 10);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            paddingHeight);
                    mPaddingView.setLayoutParams(lp);
                    viewHolder0.llContent.addView(mPaddingView, 0);
                }

                if (mCalendarTitleBean.isShowAd()) {
                    Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
                    int adTvMaxWidth = SystemUtil.getScreenDisplay(mContext).widthPixels / 3;
                    viewHolder0.tvAd1.setMaxWidth(adTvMaxWidth);

                    List<AdTitleItemBean> ads = mCalendarTitleBean.getAds();
                    if (ads == null || ads.size() == 0) {
                        viewHolder0.tvAd1.setVisibility(View.GONE);
                        viewHolder0.tvAd2.setVisibility(View.GONE);
                        viewHolder0.ivAd.setVisibility(View.GONE);
                    } else if (ads.size() == 1) {
                        viewHolder0.tvAd1.setVisibility(View.VISIBLE);
                        viewHolder0.ivAd.setVisibility(View.VISIBLE);
                        final AdTitleItemBean adItemJson = ads.get(0);
                        AdTitleIconBean icon = mCalendarTitleBean.getIcon();

                        ad1TvColorDay = adItemJson.getDay_color();
                        ad1TvColorNight = adItemJson.getNight_color();
                        if (icon != null) {
                            adIconDay = icon.getDay_icon();
                            adIconNight = icon.getNight_icon();
                        }

                        if (isNight) {
                            viewHolder0.tvAd1.setTextColor(ColorFormatUtils.formatColor(ad1TvColorNight));
                            if (adIconNight != null) {
                                Glide.with(mContext).load(adIconNight).into(viewHolder0.ivAd);
                            }
                        } else {
                            viewHolder0.tvAd1.setTextColor(ColorFormatUtils.formatColor(ad1TvColorDay));
                            if (adIconDay != null) {
                                Glide.with(mContext).load(adIconDay).into(viewHolder0.ivAd);
                            }
                        }


                        viewHolder0.tvAd1.setText(adItemJson.getTitle());
                        viewHolder0.tvAd1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JumpUtils.jump((BaseActivity) mContext, adItemJson.getO_class(), adItemJson
                                                .getO_action(), adItemJson
                                                .getO_id(),
                                        adItemJson.getHref());
                            }
                        });
                        viewHolder0.tvAd2.setVisibility(View.GONE);
                    } else {
                        viewHolder0.tvAd1.setVisibility(View.VISIBLE);
                        viewHolder0.tvAd2.setVisibility(View.VISIBLE);
                        viewHolder0.ivAd.setVisibility(View.VISIBLE);
                        final AdTitleItemBean adItemJson = ads.get(0);
                        final AdTitleItemBean adItemJson2 = ads.get(1);
                        AdTitleIconBean icon = mCalendarTitleBean.getIcon();
                        if (icon != null) {
                            adIconDay = icon.getDay_icon();
                            adIconNight = icon.getNight_icon();
                        }

                        if (isNight) {
                            ad1TvColorNight = adItemJson.getNight_color();
                            viewHolder0.tvAd1.setTextColor(ColorFormatUtils.formatColor(ad1TvColorNight));
                            viewHolder0.tvAd2.setTextColor(ColorFormatUtils.formatColor(ad2TvColorNight));
                            if (adIconNight != null) {
                                Glide.with(mContext).load(adIconNight).into(viewHolder0.ivAd);
                            }
                        } else {
                            ad1TvColorDay = adItemJson.getNight_color();
                            viewHolder0.tvAd1.setTextColor(ColorFormatUtils.formatColor(ad1TvColorDay));
                            viewHolder0.tvAd2.setTextColor(ColorFormatUtils.formatColor(ad2TvColorDay));
                            if (adIconDay != null) {
                                Glide.with(mContext).load(adIconDay).into(viewHolder0.ivAd);
                            }
                        }


                        viewHolder0.tvAd1.setText(adItemJson.getTitle());
                        viewHolder0.tvAd1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JumpUtils.jump((BaseActivity) mContext, adItemJson.getO_class(), adItemJson
                                                .getO_action(), adItemJson
                                                .getO_id(),
                                        adItemJson.getHref());
                            }
                        });
                        viewHolder0.tvAd2.setText(adItemJson2.getTitle());
                        viewHolder0.tvAd2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JumpUtils.jump((BaseActivity) mContext, adItemJson2.getO_class(), adItemJson2
                                                .getO_action(),
                                        adItemJson2.getO_id(),
                                        adItemJson2.getHref());
                            }
                        });
                    }
                } else {
                    viewHolder0.tvAd1.setVisibility(View.GONE);
                    viewHolder0.tvAd2.setVisibility(View.GONE);
                    viewHolder0.ivAd.setVisibility(View.GONE);
                }

                break;
            case 1:
                CalendarFinanceBean mCalendarFinanceBean = (CalendarFinanceBean) mCalendarType;

                viewHolder1.tvTitle.setText(mCalendarFinanceBean.getState() + mCalendarFinanceBean.getTitle());
                viewHolder1.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background));
                viewHolder1.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
                viewHolder1.tvAlarm.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
                viewHolder1.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
                viewHolder1.tvDescribe.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
                /**
                 * 时间
                 */
                try {
                    String time = mCalendarFinanceBean.getTime().split(" ")[1];
                    viewHolder1.tvTime.setText(time);
                } catch (Exception e) {
                    viewHolder1.tvTime.setText("");
                }

                /**
                 * 前值 后值 等
                 */
                String describe = mContext.getResources().getString(R.string.date_describe,
                        mCalendarFinanceBean.getBefore(),
                        mCalendarFinanceBean.getForecast(),
                        mCalendarFinanceBean.getReality());

                String reality = mCalendarFinanceBean.getReality();
                setDescribeForegroundColor(viewHolder1.tvDescribe, describe, reality);

                /**
                 *  国旗
                 */
                String firstSpell = PingYinUtil.getFirstSpell(mCalendarFinanceBean.getState());
                String stateUrl = String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(firstSpell));
                Glide
                        .with(mContext)
                        .load(stateUrl)
                        .centerCrop()
                        .into(viewHolder1.ivGuoqi);
                /**
                 *  重要程度
                 */
                viewHolder1.llStar.setImportance(mCalendarFinanceBean.getImportance());

                /**
                 * 公布状态, 已公布,未公布 利多 ,金银 , 石油   影响较小等
                 */
                setAlarmState(mCalendarFinanceBean,
                        reality,
                        mCalendarFinanceBean.getEffecttype(),
                        viewHolder1.llExponent,
                        viewHolder1.tvAlarm);

                viewHolder1.vLine.setVisibility(mCalendarFinanceBean.isShowLine() ? View.VISIBLE : View.INVISIBLE);
                break;
            case 2:
                CalendarImportantBean mCalendarImportantBean = (CalendarImportantBean) mCalendarType;
                viewHolder2.tvTitle.setText(mCalendarImportantBean.getState() + mCalendarImportantBean.getTitle());
                viewHolder2.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background));
                viewHolder2.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
                viewHolder2.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
                /**
                 * 时间
                 */
                try {
                    String time = mCalendarImportantBean.getTime();
                    viewHolder2.tvTime.setText(time);
                } catch (Exception e) {
                    viewHolder2.tvTime.setText("");
                }
                /**
                 *  国旗
                 */
                String firstSpell2 = PingYinUtil.getFirstSpell(mCalendarImportantBean.getState());
                String stateUrl2 = String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(firstSpell2));
                Glide
                        .with(mContext)
                        .load(stateUrl2)
                        .centerCrop()
                        .into(viewHolder2.ivGuoqi);

                /**
                 *  重要程度
                 */
                viewHolder2.llStar.setImportance(mCalendarImportantBean.getImportance());

                viewHolder2.vLine.setVisibility(mCalendarImportantBean.isShowLine() ? View.VISIBLE : View.INVISIBLE);
                break;
            case 3:
                CalendarHolidayBean mCalendarHolidayBean = (CalendarHolidayBean) mCalendarType;
                viewHolder3.tvTitle.setText(mCalendarHolidayBean.getState() + mCalendarHolidayBean.getTitle());
                viewHolder3.vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background));
                viewHolder3.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.font_color6));
                viewHolder3.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
                /**
                 * 时间
                 */
                try {
                    String time = mCalendarHolidayBean.getTime().split(" ")[1];
                    viewHolder3.tvTime.setText(time);
                } catch (Exception e) {
                    viewHolder3.tvTime.setText("");
                }
                /**
                 *  国旗
                 */
                String firstSpell3 = PingYinUtil.getFirstSpell(mCalendarHolidayBean.getState());
                String stateUrl3 = String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(firstSpell3));
                Glide
                        .with(mContext)
                        .load(stateUrl3)
                        .centerCrop()
                        .into(viewHolder3.ivGuoqi);

                viewHolder3.vLine.setVisibility(mCalendarHolidayBean.isShowLine() ? View.VISIBLE : View.INVISIBLE);
                break;
            case 4:
                CalendarNotBean mCalendarNotBean = (CalendarNotBean) mCalendarType;
                viewHolder4.tvDescribe.setText(mCalendarNotBean.getDescribe());
                viewHolder4.tvDescribe.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
                break;
        }
        return view;
    }


    class ViewBaseHolder {
        @BindView(R.id.iv_guoqi) ImageView ivGuoqi;
        @BindView(R.id.v_line) View vLine;
    }

    class ViewHolder0 {
        @BindView(R.id.ll_title_content) LinearLayout llContent;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_advert1) TextView tvAd1;
        @BindView(R.id.tv_advert2) TextView tvAd2;
        @BindView(R.id.iv_ad) ImageView ivAd;

        public ViewHolder0(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder1 extends ViewBaseHolder {
        @BindView(R.id.ll_star) StarView llStar;
        @BindView(R.id.ll_left_gq) LinearLayout llLeftGq;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_describe) TextView tvDescribe;
        @BindView(R.id.tv_time) TextView tvTime;

        @BindView(R.id.ll_exponent) LinearLayout llExponent;//公布值,
        @BindView(R.id.tv_alarm) TextView tvAlarm;//点击闹钟

        public ViewHolder1(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder2 extends ViewBaseHolder {
        @BindView(R.id.ll_star) StarView llStar;
        @BindView(R.id.ll_left_gq) LinearLayout llLeftGq;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.rl_content) RelativeLayout rlContent;

        public ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder3 extends ViewBaseHolder {
        @BindView(R.id.ll_left_gq) LinearLayout llLeftGq;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.rl_content) RelativeLayout rlContent;

        public ViewHolder3(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder4 {
        @BindView(R.id.iv_error) ImageView ivError;
        @BindView(R.id.tv_describe) TextView tvDescribe;

        public ViewHolder4(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private void setAlarmState(final CalendarType mCalendarType,
                               String reality,
                               int effectType,
                               LinearLayout llExponent,
                               final TextView tvAlarm) {

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


        RadianDrawable radianDrawable = new RadianDrawable(mContext);

        try {
            Float.parseFloat(reality.replace("%", ""));

            radianDrawable.setStroke(R.color.line_color);

            int alarmColor = ContextCompat.getColor(mContext, R.color.font_color6);
            tvAlarm.setTextColor(alarmColor);
            tvAlarm.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tvAlarm.setText("已公布");

            if (effectType != 2) {
                String[] effectSplit = effect.split("\\|");
                for (int i = 0; i < effectSplit.length; i++) {
                    drawingShapeColor(i, effectType, effectSplit[i], llExponent);
                }
            } else {
                drawingShapeColor(2, effectType, "影响较小", llExponent);
            }

        } catch (NumberFormatException e) {
            radianDrawable.setStroke(R.color.font_color8);

            Drawable alarmDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_alarm);
            tvAlarm.setCompoundDrawablesWithIntrinsicBounds(alarmDrawable, null, null, null);
            tvAlarm.setText("定时");

            if (mCalendarType instanceof CalendarFinanceBean) {
                CalendarFinanceBean calendarFinanceBean = (CalendarFinanceBean) mCalendarType;
                AlarmJson alarmItem = AlarmPresenter.getInstance().getAlarmItem(calendarFinanceBean.getCode());
                if (alarmItem != null) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    Date baseDate = new Date(Long.parseLong(alarmItem.getTime()));
                    String formatDate = simpleDateFormat.format(baseDate);

                    tvAlarm.setText(formatDate);
                }
            }

            RadianDrawable effectDrawable = new RadianDrawable(mContext);

            TextView textView = generateTextView();
            textView.setText("未公布");

            effectDrawable.setStroke(R.color.font_color3);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color3));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);

            tvAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//设置时间
                    parentFragment.showTimingWindow(mCalendarType, new ObserverCall<Integer>() {
                        @Override
                        public void onComplete(Integer time) {
                            try {
                                long baseTime = 0L;


                                CalendarFinanceBean calendarFinanceBean = null;

                                if (mCalendarType instanceof CalendarFinanceBean) {
                                    calendarFinanceBean = (CalendarFinanceBean) mCalendarType;
                                    String financeTime = calendarFinanceBean.getTime();

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date parseDate = simpleDateFormat.parse(financeTime);
                                    baseTime = parseDate.getTime();
                                }

                                int oneMinute = 60 * 1000;
                                switch (time) {
                                    case 0:
                                        baseTime -= 5 * oneMinute;
                                        break;
                                    case 1:
                                        baseTime -= 10 * oneMinute;
                                        break;
                                    case 2:
                                        baseTime -= 15 * oneMinute;
                                        break;
                                    case 3:
                                        baseTime -= 30 * oneMinute;
                                        break;
                                    case 4:
                                        baseTime -= 60 * oneMinute;
                                        break;
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                Date baseDate = new Date(baseTime);
                                final String formatDate = simpleDateFormat.format(baseDate);

                                MainActivity mainActivity = (MainActivity) mContext;
                                mainActivity.checkAlarmPermissions(baseTime,
                                        calendarFinanceBean,
                                        new OnRequestPermissions() {
                                            /**
                                             * 调用成功
                                             */
                                            @Override
                                            public void doSomething() {
                                                int alarmColor = ContextCompat.getColor(mContext, R.color.font_color8);
                                                tvAlarm.setText(formatDate);
                                                tvAlarm.setTextColor(alarmColor);
                                            }

                                            /**
                                             * 权限失败
                                             */
                                            @Override
                                            public void doFailSomething() {

                                            }
                                        });

                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
        tvAlarm.setBackground(radianDrawable);
    }

    private void drawingShapeColor(int type, int effectType, String effect, LinearLayout llExponent) {

        CalendarFragment parentFragment = (CalendarFragment) this.parentFragment.getParentFragment();


        int shapeColor = 0;
        Drawable leftDrawable = null;

        switch (type) {
            case 0:
                shapeColor = R.color.calendar_line0;
                leftDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_top_red);
                break;
            case 1:
                shapeColor = R.color.calendar_line1;
                leftDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_decline_green);
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
                    String splitTrim = effect0Split[i].trim();
                    if (parentFragment.judgeSet.size() == 0 ||
                            parentFragment.judgeSet.contains("全部") ||
                            parentFragment.judgeSet.contains(splitTrim)) {

                        RadianDrawable effectDrawable = new RadianDrawable(mContext);

                        TextView textView = generateTextView();
                        textView.setText(splitTrim);

                        effectDrawable.setStroke(shapeColor);
                        textView.setTextColor(ContextCompat.getColor(mContext, shapeColor));

                        textView.setBackground(effectDrawable);

                        textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                        llExponent.addView(textView);
                    }
                }
            }
        } else {
            RadianDrawable effectDrawable = new RadianDrawable(mContext);

            TextView textView = generateTextView();
            textView.setText(effect);

            effectDrawable.setStroke(shapeColor);
            textView.setTextColor(ContextCompat.getColor(mContext, shapeColor));
            textView.setBackground(effectDrawable);

            llExponent.addView(textView);
        }
    }


    private TextView generateTextView() {
        int padding = SystemUtil.dp2px(mContext, 5);
        int MarginsRight = SystemUtil.dp2px(mContext, 10);
        int minWidth = SystemUtil.dp2px(mContext, 55);

        TextView itemView = new TextView(mContext);

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

    private void setDescribeForegroundColor(TextView tvDescribe, String describe, String reality) {
        try {
            float realityFloat = Float.parseFloat(reality.replace("%", ""));
            int index = describe.lastIndexOf(":") + 1;
            SpannableString spannableString = new SpannableString(describe);
            if (realityFloat > 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.rise_color)),
                        index - 3,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            } else if (realityFloat < 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.rise_color/*decline_color*/)),
                        index - 3,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                tvDescribe.setTextColor(ContextCompat.getColor(mContext, R.color.unaltered_color));
            }
            tvDescribe.setText(spannableString);
        } catch (NumberFormatException e) {
            tvDescribe.setText(describe);
        } finally {
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (dataList.get(position).getAdapterType()) {
            case TITLE:
                return 0;
            case CONTENT1:
                return 1;
            case CONTENT2:
                return 2;
            case CONTENT3:
                return 3;
            case NO_DATA:
                return 4;
            default:
                return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

}
