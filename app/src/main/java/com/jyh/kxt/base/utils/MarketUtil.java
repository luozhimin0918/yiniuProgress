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

    @BindingAdapter(value = {"bindingArrowsPrice", "bindingArrowsRange"})
    public static void bindingArrows(TextView tvLabel, String price, String range) {

        int highsOrLows = MarketUtil.isHighsOrLows(range);
        switch (highsOrLows) {
            case -1:
                tvLabel.setText(price + " ↓");
                break;
            case 0:
                tvLabel.setText(price + " ↑");
                break;
        }
    }


    /**
     * 行情 相关处理
     */
    public static void mapToMarketBean(View view, int switchItemType, HashMap<String, MarketItemBean> marketMap,
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

            if (rangeReserve.contains("-")) {
                marketItemBean.setBgGlint(2);
            } else {
                marketItemBean.setBgGlint(1);
            }

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (rangeReserve.contains("-")) {
                        marketItemBean.setBgGlint(12);
                    } else {
                        marketItemBean.setBgGlint(11);
                    }
                }
            }, 500);
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
     * @param fromSource 0表示来自于网络  1表示来自于本地
     */
    public static void saveMarketEditOption(Context mContext, List<MarketItemBean> marketList, int fromSource) {
        if (fromSource == 0) {
            String marketEditOption = getMarketEditOption(mContext);

            List<MarketItemBean> localMarketList = JSONArray.parseArray(marketEditOption, MarketItemBean.class);
            HashMap<String, MarketItemBean> localCodeMap = new HashMap<>();

            //合并所有本地和网络的数据
            for (MarketItemBean marketItemBean : localMarketList) {
                marketItemBean.setFromSource(1);
                localCodeMap.put(marketItemBean.getCode(), marketItemBean);
            }

            for (MarketItemBean marketItemBean : marketList) {
                if (localCodeMap.get(marketItemBean.getCode()) == null) {//如果本地不存这条数据
                    marketItemBean.setFromSource(0);
                    marketList.add(marketItemBean);
                }
            }
        }
        String marketJson = JSON.toJSONString(marketList);
        SPUtils.save(mContext, SpConstant.MARKET_MY_OPTION, marketJson);
    }

    public static String getMarketEditOption(Context mContext) {
        return SPUtils.getString(mContext, SpConstant.MARKET_MY_OPTION);
    }
}
