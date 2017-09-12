//package com.jyh.kxt.base.util;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.jyh.kxt.base.BaseActivity;
//import com.jyh.kxt.base.constant.SpConstant;
//import com.jyh.kxt.base.json.AdTitleIconBean;
//import com.jyh.kxt.base.json.AdTitleItemBean;
//import com.jyh.kxt.base.utils.ColorFormatUtils;
//import com.jyh.kxt.base.utils.JumpUtils;
//import com.library.util.SPUtils;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * 项目名:Kxt
// * 类描述:
// * 创建人:苟蒙蒙
// * 创建日期:2017/7/28.
// */
//
//public class AdUtils {
//    public static List<AdTitleItemBean> checkAdPosition(List<AdTitleItemBean> ads) {
//        if (ads == null) return null;
//        Iterator<AdTitleItemBean> iterator = ads.iterator();
//        while (iterator.hasNext()) {
//            AdTitleItemBean next = iterator.next();
//            String position = next.getPosition();
//            if (!"1".equals(position) && !"2".equals(position)) {
//                iterator.remove();
//            }
//        }
//        if (ads.size() == 0) return null;
//        int size = ads.size();
//        if (size == 1) {
//            return ads;
//        } else {
//            List<AdTitleItemBean> adTitles = new ArrayList<>();
//            AdTitleItemBean ad1 = ads.get(0);
//            AdTitleItemBean ad2 = ads.get(1);
//            String position1 = ad1.getPosition();
//            String position2 = ad2.getPosition();
//            if (position1 == null) {
//                if (position2 == null) return ads;
//                if (position2.equals("2")) return ads;
//                if (position2.equals("1")) {
//                    adTitles.add(ad2);
//                    adTitles.add(ad1);
//                    return adTitles;
//                }
//            } else if (position1.equals("1")) {
//                return ads;
//            } else if (position1.equals("2")) {
//                adTitles.add(ad2);
//                adTitles.add(ad1);
//                return adTitles;
//            }
//        }
//        return ads;
//    }
//
//    /**
//     * 设置广告数据
//     *
//     * @param mContext
//     * @param adTv1            右广告TextView
//     * @param adTv2            左广告TextView
//     * @param adIv             广告ImageView
//     * @param adTitleItemBeens
//     * @param icon
//     */
//    public static void setAd(Context mContext, TextView adTv1, TextView adTv2, ImageView adIv, List<AdTitleItemBean> adTitleItemBeens,
//                             AdTitleIconBean icon) {
//        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
//        List<AdTitleItemBean> ads = AdUtils.checkAdPosition(adTitleItemBeens);
//        String adIcon = null;
//        if (ads == null || ads.size() == 0) {
//            adTv1.setVisibility(View.GONE);
//            adTv2.setVisibility(View.GONE);
//            adIv.setVisibility(View.GONE);
//        } else if (ads.size() == 1) {
//            adTv1.setVisibility(View.GONE);
//            adTv2.setVisibility(View.GONE);
//            adIv.setVisibility(View.VISIBLE);
//            final AdTitleItemBean adItemJson = ads.get(0);
//
//            if (isNight) {
//                if (icon != null) {
//                    adIcon = icon.getNight_icon();
//                }
//                if (adIcon != null) {
//                    Glide.with(mContext).load(adIcon).into(adIv);
//                }
//            } else {
//                if (icon != null) {
//                    adIcon = icon.getDay_icon();
//                }
//                if (adIcon != null) {
//                    Glide.with(mContext).load(adIcon).into(adIv);
//                }
//            }
//
//            setAd(mContext, isNight, adTv1, adTv2, adItemJson, 0, false);
//        } else {
//            adIv.setVisibility(View.VISIBLE);
//            final AdTitleItemBean adItemJson = ads.get(0);
//            final AdTitleItemBean adItemJson2 = ads.get(1);
//
//            if (isNight) {
//                if (icon != null) {
//                    adIcon = icon.getNight_icon();
//                }
//                if (adIcon != null) {
//                    Glide.with(mContext).load(adIcon).into(adIv);
//                }
//            } else {
//                if (icon != null) {
//                    adIcon = icon.getDay_icon();
//                }
//                if (adIcon != null) {
//                    Glide.with(mContext).load(adIcon).into(adIv);
//                }
//            }
//
//
//            setAd(mContext, isNight, adTv1, adTv2, adItemJson, 0, true);
//            setAd(mContext, isNight, adTv1, adTv2, adItemJson2, 1, true);
//        }
//    }
//
//    /**
//     * 设置广告位置
//     *
//     * @param mContext
//     * @param isNight
//     * @param adTv1
//     * @param adTv2
//     * @param ad
//     * @param position
//     * @param isShowAll 是够全部显示
//     */
//    private static void setAd(final Context mContext, Boolean isNight, TextView adTv1, TextView adTv2, final AdTitleItemBean ad, int
//            position,
//                              boolean isShowAll) {
//        if (ad == null) return;
//        String title = ad.getTitle();
//        if (isShowAll) {
//            if (position == 0) {
//                //左
//                adTv2.setVisibility(View.VISIBLE);
//                adTv2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        JumpUtils.jump((BaseActivity) mContext, ad.getO_class(), ad
//                                        .getO_action(),
//                                ad.getO_id(),
//                                ad.getHref());
//                    }
//                });
//                adTv2.setText(title);
//                String tvColor;
//                if (isNight) {
//                    tvColor = ad.getNight_color();
//                } else {
//                    tvColor = ad.getDay_color();
//                }
//                adTv2.setTextColor(Color.parseColor(tvColor == null ? "#1384ED" : tvColor));
//            } else {
//                //右
//                adTv1.setVisibility(View.VISIBLE);
//                adTv1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        JumpUtils.jump((BaseActivity) mContext, ad.getO_class(), ad
//                                        .getO_action(),
//                                ad.getO_id(),
//                                ad.getHref());
//                    }
//                });
//                adTv1.setText(title);
//                String tvColor;
//                if (isNight) {
//                    tvColor = ad.getNight_color();
//                } else {
//                    tvColor = ad.getDay_color();
//                }
//                adTv1.setTextColor(Color.parseColor(tvColor == null ? "#1384ED" : tvColor));
//            }
//        } else {
//            String adPosition = ad.getPosition();
//            if (adPosition == null || adPosition.equals("1")) {
//                //左
//                adTv1.setVisibility(View.INVISIBLE);
//                adTv1.setText(title);
//                adTv2.setVisibility(View.VISIBLE);
//                adTv2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        JumpUtils.jump((BaseActivity) mContext, ad.getO_class(), ad
//                                        .getO_action(),
//                                ad.getO_id(),
//                                ad.getHref());
//                    }
//                });
//                adTv2.setText(title);
//                String tvColor;
//                if (isNight) {
//                    tvColor = ad.getNight_color();
//                } else {
//                    tvColor = ad.getDay_color();
//                }
//                adTv2.setTextColor(Color.parseColor(tvColor == null ? "#1384ED" : tvColor));
//            } else {
//                //右
//                adTv1.setVisibility(View.VISIBLE);
//                adTv2.setVisibility(View.GONE);
//                adTv1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        JumpUtils.jump((BaseActivity) mContext, ad.getO_class(), ad
//                                        .getO_action(),
//                                ad.getO_id(),
//                                ad.getHref());
//                    }
//                });
//                adTv1.setText(title);
//                String tvColor;
//                if (isNight) {
//                    tvColor = ad.getNight_color();
//                } else {
//                    tvColor = ad.getDay_color();
//                }
//                adTv1.setTextColor(Color.parseColor(tvColor == null ? "#1384ED" : tvColor));
//            }
//        }
//    }
//
//    public static void onChangeTheme(Context mContext, boolean isNight, TextView adTv1, TextView adTv2, ImageView adIv,
//                                     List<AdTitleItemBean>
//                                             adTitleItemBeens,
//                                     AdTitleIconBean icon) {
//        List<AdTitleItemBean> ads = AdUtils.checkAdPosition(adTitleItemBeens);
//        String adIcon = null;
//        String adTvColor;
//
//        if (ads == null || ads.size() == 0)
//            return;
//        if (icon != null) {
//            if (isNight) {
//                adIcon = icon.getNight_icon();
//            } else {
//                adIcon = icon.getDay_icon();
//            }
//        }
//        for (AdTitleItemBean ad : ads) {
//
//            String position = ad.getPosition();
//            if (position.equals("1")) {
//                //左
//                if (isNight) {
//                    adTvColor = ad.getNight_color();
//                    adTvColor = adTvColor == null ? "#1384ED" : adTvColor;
//                    if (adTv2 != null)
//                        adTv2.setTextColor(ColorFormatUtils.formatColor(adTvColor));
//                } else {
//                    adTvColor = ad.getDay_color();
//                    adTvColor = adTvColor == null ? "#1384ED" : adTvColor;
//                    if (adTv2 != null)
//                        adTv2.setTextColor(ColorFormatUtils.formatColor(adTvColor));
//                }
//            } else {
//                //右
//                if (isNight) {
//                    adTvColor = ad.getNight_color();
//                    adTvColor = adTvColor == null ? "#1384ED" : adTvColor;
//                    if (adTv1 != null)
//                        adTv1.setTextColor(ColorFormatUtils.formatColor(adTvColor));
//                } else {
//                    adTvColor = ad.getDay_color();
//                    adTvColor = adTvColor == null ? "#1384ED" : adTvColor;
//                    if (adTv1 != null)
//                        adTv1.setTextColor(ColorFormatUtils.formatColor(adTvColor));
//                }
//            }
//            if (adIv != null && adIcon != null) {
//                Glide.with(mContext).load(adIcon).into(adIv);
//            }
//        }
//
//    }
//}
