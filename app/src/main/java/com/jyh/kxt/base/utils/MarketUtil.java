package com.jyh.kxt.base.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RadianDrawable2;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.util.SPUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/11.
 * 行情的Util
 */

public class MarketUtil {

    public static int isHighsOrLows(String range) {
        if (range.contains("-")) {
            return -1;
        } else {
            return 0;
        }
    }

    @BindingAdapter("bindingRange")
    public static void bindingRange(TextView tvLabel, String range) {
        int fontColor;

        if (range.contains("-")) {
            fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.decline_color);
        } else {
            fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);
        }

        tvLabel.setTextColor(fontColor);
    }

    /**
     * 0 默认情况
     * 1 上涨 调红_闪烁
     * 2 下跌  调绿_闪烁
     * <p>
     * 11 上涨动画完毕,恢复文字变红,背景set Null
     * 12 下跌动画完毕,恢复文字变绿,背景set Null
     *
     * @param tvLabel
     * @param bgGlint
     */
    @BindingAdapter("bindingColorLump")
    public static void bindingColorLump(final TextView tvLabel, int bgGlint) {
        int colorBg = 0;
        switch (bgGlint) {
            case 0: //表示不执行任何操作
                break;
            case 1: //上涨  调红_闪烁
            case 11:
                colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);
                break;
            case 2: //下跌  调绿_闪烁
            case 12:
                colorBg = ContextCompat.getColor(tvLabel.getContext(), R.color.decline_color);
                break;
            default:
                break;
        }

        if (bgGlint == 1 || bgGlint == 2) {
            RadianDrawable2 radianDrawable = new RadianDrawable2(tvLabel.getContext(), colorBg, 3);

            tvLabel.setBackground(radianDrawable);
            tvLabel.setTextColor(ContextCompat.getColor(tvLabel.getContext(), R.color.white));
        }
        if (bgGlint == 11 || bgGlint == 12) {
            tvLabel.setBackground(null);
            tvLabel.setTextColor(colorBg);
        }
    }

    @BindingAdapter(value = {"bindingMarketItem", "bindingFromSource"})
    public static void bindingGlint(final TextView tvLabel, final MarketItemBean marketItemBean, int fromSource) {
        if (fromSource > 1 && marketItemBean != null) {
            if (tvLabel.getScaleX() == 1.1f || tvLabel.getScaleY() == 1.1f || tvLabel.getPaint().isFakeBoldText()) {
                tvLabel.setScaleX(1.0f);
                tvLabel.setScaleY(1.0f);
                tvLabel.getPaint().setFakeBoldText(false);
                return;
            }

            double aborPrice = Double.valueOf(marketItemBean.getAborPrice());
            double lastPrice = Double.valueOf(marketItemBean.getPrice());

            //大于等于上一次 涨
            if (lastPrice >= aborPrice) {
                int fontColor = ContextCompat.getColor(tvLabel.getContext(), R.color.rise_color);
                tvLabel.setTextColor(fontColor);
            }

            tvLabel.setScaleX(1.1f);
            tvLabel.setScaleY(1.1f);
            tvLabel.getPaint().setFakeBoldText(true);
            tvLabel.postInvalidate();

            tvLabel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvLabel.setScaleX(1.0f);
                    tvLabel.setScaleY(1.0f);
                    tvLabel.getPaint().setFakeBoldText(false);
                    tvLabel.postInvalidate();

                    bindingRange(tvLabel, marketItemBean.getRange());
                }
            }, 500);
        }
    }


    /**
     * 行情 相关处理
     */
    public static void mapToMarketBean(View view,
                                       int switchItemType,
                                       HashMap<String, MarketItemBean> marketMap,
                                       String text) {

        JSONObject jsonObject = JSONObject.parseObject(text);
        String code = jsonObject.getString("c");
        String price = jsonObject.getString("p");   //最新价  3.5951
        String change = jsonObject.getString("d");  //涨跌额  0.0238
        String range = jsonObject.getString("df");  //涨跌幅 "0.6664239912636867%",

        final MarketItemBean marketItemBean = marketMap.get(code);
        if (marketItemBean != null) {
            marketItemBean.setPrice(price);
            marketItemBean.setChange(reserveDecimals(change, 4));

            //设置涨跌幅
            final String rangeReserve = reserveDecimals(range, 2);
            marketItemBean.setRange(rangeReserve);
            if (switchItemType == 0) {
                marketItemBean.setSwitchTarget(marketItemBean.getRange());
            } else {
                marketItemBean.setSwitchTarget(marketItemBean.getChange());
            }
            int increaseSource = marketItemBean.getMarketFromSource() + 1;
            marketItemBean.setMarketFromSource(increaseSource);
        }
    }

    private static String reserveDecimals(String defStr, int places) {
        //先替换负数和百分比
        boolean isIncludeNegativeNumber = defStr.contains("-");
        boolean isIncludePercentage = defStr.contains("%");

        String clearSpecialChars = defStr.replace("-", "").replace("%", "");

        BigDecimal bigDecimal = new BigDecimal(clearSpecialChars);
        String after = bigDecimal.setScale(places, BigDecimal.ROUND_HALF_UP).toString();

        if (isIncludeNegativeNumber) {
            after = "-" + after;
        } else {
            boolean isIncludePositiveNumber = defStr.contains("+");
            if (!isIncludePositiveNumber) {
                after = "+" + after;
            }
        }
        if (isIncludePercentage) {
            after = after + "%";
        }
        return after;
    }

    public static String replacePositive(String defStr) {
        boolean isIncludeNegativeNumber = defStr.contains("-");
        if (!isIncludeNegativeNumber) {
            boolean isIncludePositiveNumber = defStr.contains("+");
            if (!isIncludePositiveNumber) {
                defStr = "+" + defStr;
            }
        }
        return defStr;
    }


    /**
     * @param mContext
     * @param marketList
     * @param status     状态为0   则第一次安装使用  2 不比对直接保存
     */
    public static void saveMarketEditOption(Context mContext, List<MarketItemBean> marketList, int status) {

        Boolean isInit = SPUtils.getBoolean(mContext, SpConstant.INIT_MARKET_MY_OPTION);
        if (status == 0 && isInit) {
            return;
        }
        if (status == 0 && !isInit) {
            SPUtils.save(mContext, SpConstant.INIT_MARKET_MY_OPTION, true);
            SPUtils.save(mContext, SpConstant.MARKET_MY_OPTION, JSON.toJSONString(marketList));
        }


        List<MarketItemBean> mergeLocalMarket;
        if (status == 2) {
            mergeLocalMarket = marketList;
        } else {
            mergeLocalMarket = getMergeLocalMarket(mContext, marketList);
        }
        SPUtils.save(mContext, SpConstant.MARKET_MY_OPTION, JSON.toJSONString(mergeLocalMarket));
    }

    public static List<MarketItemBean> getMarketEditOption(Context mContext) {
        String option = SPUtils.getString(mContext, SpConstant.MARKET_MY_OPTION);
        if (option == null || "".equals(option)) {
            return new ArrayList<>();
        }
        List<MarketItemBean> list = JSONArray.parseArray(option, MarketItemBean.class);
        return list;
    }

    public static List<MarketItemBean> getMergeLocalMarket(Context mContext, List<MarketItemBean> marketItemBeen) {
        List<MarketItemBean> localMarketList = getMarketEditOption(mContext);

        HashMap<String, MarketItemBean> mergeCodeMap = new HashMap<>();
        for (MarketItemBean marketItemBean : localMarketList) {
            mergeCodeMap.put(marketItemBean.getCode(), marketItemBean);
        }

        for (MarketItemBean marketItemBean : marketItemBeen) {

            MarketItemBean codeItemBean = mergeCodeMap.get(marketItemBean.getCode());
            if (codeItemBean == null) {
                localMarketList.add(marketItemBean);
            }
        }

        return localMarketList;
    }
}
