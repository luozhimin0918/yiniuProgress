package com.jyh.kxt.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RadianDrawable;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.PingYinUtil;
import com.jyh.kxt.base.utils.SaveImage;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.base.http.VarConstant;
import com.library.util.DateUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class FastInfoAdapter extends BaseAdapter implements FastInfoPinnedListView.PinnedSectionListAdapter {

    private static final int TYPE_TIME = 0;
    private static final int TYPE_KX = 1;
    private static final int TYPE_RL = 2;
    private static final int TYPE_LEFT = 3;
    private static final int TYPE_RIGHT = 4;
    private static final int TYPE_TOP = 5;
    private static final int TYPE_BOTTOM = 6;
    private PopupUtil.Config config;
    private ImageView ivPop;
    private ImageView ivDownView;
    private PopupUtil popupUtil;
    private int imgMaxWidth;
    private int imgMaxHeight;

    private List flashJsons;
    private Context context;

    private List<Object> baseFlashArrayList = new ArrayList<>();

    public FastInfoAdapter(List<FlashJson> flashJsons, final Context context) {

        if (flashJsons != null) {
            baseFlashArrayList.addAll(flashJsons);
        }

        filterNullData();

        this.flashJsons = flashJsons;
        this.context = context;
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(context);
        imgMaxHeight = screenDisplay.heightPixels / 3;


        popupUtil = new PopupUtil((Activity) context);
        View inflate = popupUtil.createPopupView(R.layout.pop_img);
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupUtil != null && popupUtil.isShowing()) {
                    popupUtil.dismiss();
                }
            }
        });
        ivPop = (ImageView) inflate.findViewById(R.id.iv_pop);
        ivDownView = (ImageView) inflate.findViewById(R.id.iv_download);
        config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style1;

        justLookImportant();
    }

    /**
     * 过滤非空数据和添加收藏状态
     */
    private void filterNullData() {
        if (flashJsons != null) {
            Iterator<Object> iterator = flashJsons.iterator();
            while (iterator.hasNext()) {
                try {
                    Object next = iterator.next();
                    if (next instanceof FlashJson) {
                        FlashJson mConvertFlash = (FlashJson) next;
                        if (next == null || mConvertFlash.getContent() == null) {
                            iterator.remove();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Object flash : flashJsons) {
                if (flash instanceof FlashJson) {
                    FlashJson mConvertFlash = (FlashJson) flash;
                    if (CollectUtils.isCollect(context, VarConstant.COLLECT_TYPE_FLASH, flash)) {
                        mConvertFlash.setColloct(true);
                    } else {
                        mConvertFlash.setColloct(false);
                    }
                }
            }
        }
    }

    /**
     * 过滤重要数据
     */
    public void justLookImportant() {
        try {
            this.flashJsons.clear();

            boolean onlyShowHigh = SPUtils.getBoolean(context, SpConstant.FLASH_FILTRATE_HIGH);

            for (int i = 0; i < baseFlashArrayList.size(); i++) {

                Object mFlashObj = baseFlashArrayList.get(i);
                if (mFlashObj != null) {
                    if (mFlashObj instanceof FlashJson) {
                        FlashJson flashJson = (FlashJson) mFlashObj;
                        boolean mIsCalendarData = VarConstant.SOCKET_FLASH_CJRL.equals(flashJson.getCode());
                        if (mIsCalendarData) {//类型是财经日历
                            if (onlyShowHigh) { //开启只看重要
                                String content = flashJson.getContent();
                                Flash_RL mFlashRl = JSON.parseObject(content, Flash_RL.class);

                                if (!VarConstant.IMPORTANCE_HIGH.equals(mFlashRl.getImportance())) {
                                    Log.e("符合", "justLookImportant: ");
                                    continue;
                                }
                            }
                        }
                    }
                }
                this.flashJsons.add(mFlashObj);
            }
            inspiritDateInfo(this.flashJsons);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤重要数据
     */
    public List justLookImportant(List<FlashJson> objList) {
        List mNewLookList = new ArrayList();
        try {
            boolean onlyShowHigh = SPUtils.getBoolean(context, SpConstant.FLASH_FILTRATE_HIGH);

            for (int i = 0; i < objList.size(); i++) {
                Object mFlashObj = objList.get(i);
                if (mFlashObj != null) {
                    if (mFlashObj instanceof FlashJson) {
                        FlashJson flashJson = (FlashJson) mFlashObj;
                        boolean mIsCalendarData = VarConstant.SOCKET_FLASH_CJRL.equals(flashJson.getCode());
                        if (mIsCalendarData) {//类型是财经日历
                            if (onlyShowHigh) { //开启只看重要
                                String content = flashJson.getContent();
                                Flash_RL mFlashRl = JSON.parseObject(content, Flash_RL.class);

                                if (!VarConstant.IMPORTANCE_HIGH.equals(mFlashRl.getImportance())) {
                                    continue;
                                }
                            }
                        }
                    }
                }
                mNewLookList.add(mFlashObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objList.clear();
        objList.addAll(mNewLookList);
        return objList;
    }

    private Map<String, Integer> timeMap = new HashMap<>();

    public void setData(List<FlashJson> flashJsons) {

        if (flashJsons == null) return;
        Iterator<FlashJson> iterator = flashJsons.iterator();
        while (iterator.hasNext()) {
            FlashJson next = iterator.next();
            if (next == null || next.getContent() == null) {
                iterator.remove();
            }
        }

        for (FlashJson flash : flashJsons) {
            if (CollectUtils.isCollect(context, VarConstant.COLLECT_TYPE_FLASH, flash)) {
                flash.setColloct(true);
            } else {
                flash.setColloct(false);
            }
        }

        this.flashJsons.clear();
        this.flashJsons.addAll(flashJsons);

        baseFlashArrayList.clear();
        baseFlashArrayList.addAll(this.flashJsons);

        justLookImportant();

        notifyDataSetChanged();
    }


    public void addData(FlashJson flashJson) {

        if (flashJson == null || flashJson.getContent() == null) {
            return;
        }

        if (CollectUtils.isCollect(context, VarConstant.COLLECT_TYPE_FLASH, flashJson)) {
            flashJson.setColloct(true);
        } else {
            flashJson.setColloct(false);
        }

        this.flashJsons.add(1, flashJson);
        this.baseFlashArrayList.add(1, flashJson);
        notifyDataSetChanged();
    }

    public void addData(final List<FlashJson> flashJsons) {

        if (flashJsons == null) return;

        Iterator<FlashJson> iterator = flashJsons.iterator();
        while (iterator.hasNext()) {
            FlashJson next = iterator.next();
            if (next == null || next.getContent() == null) {
                iterator.remove();
            }
        }


        for (FlashJson flash : flashJsons) {
            if (CollectUtils.isCollect(context, VarConstant.COLLECT_TYPE_FLASH, flash)) {
                flash.setColloct(true);
            } else {
                flash.setColloct(false);
            }
        }
        inspiritDateInfo2(flashJsons);
        FastInfoAdapter.this.flashJsons.addAll(flashJsons);

        justLookImportant(FastInfoAdapter.this.flashJsons);

        FastInfoAdapter.this.baseFlashArrayList.addAll(flashJsons);

//        inspiritDateInfo2(FastInfoAdapter.this.flashJsons);

        notifyDataSetChanged();

       /* Observable<SingleThreadJson> observable = Observable.create(new Observable
                .OnSubscribe<SingleThreadJson>() {
            @Override
            public void call(Subscriber<? super SingleThreadJson> subscriber) {


                subscriber.onCompleted();
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SingleThreadJson>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SingleThreadJson jsonStr) {
                    }
                });*/
    }

    /**
     * 获取非时间数据
     *
     * @return
     */
    public List<FlashJson> getData() {
        List data = new ArrayList(flashJsons);
        for (Object o : flashJsons) {
            if (o instanceof String) {
                data.remove(o);
            }
        }
        return new ArrayList<>(data);
    }

    /**
     * 获取未处理的数据
     *
     * @return
     */
    public List getSource() {
        return flashJsons;
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
    public View getView(int position, View convertView, final ViewGroup parent) {

        TimeViewHolder timeHolder = null;
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
                case TYPE_TIME:
                    convertView = inflater.inflate(R.layout.layout_flash_time_bar, parent, false);
                    timeHolder = new TimeViewHolder(convertView);
                    convertView.setTag(timeHolder);
                    break;
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
                    case TYPE_TIME:
                        timeHolder = (TimeViewHolder) convertView.getTag();
                        break;
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
                    case TYPE_TIME:
                        convertView = inflater.inflate(R.layout.layout_flash_time_bar, parent, false);
                        timeHolder = new TimeViewHolder(convertView);
                        convertView.setTag(timeHolder);
                        break;
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

        try {
            switch (type) {
                case TYPE_TIME:
                    timeHolder.tvTime.setBackgroundColor(ContextCompat.getColor(context, R.color.timeBarColor));
                    timeHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
                    timeHolder.tvTime.setText(flashJsons.get(position).toString());
                    break;
                case TYPE_KX:
                    FlashJson flash = (FlashJson) flashJsons.get(position);
                    final Flash_KX kx = JSON.parseObject(flash.getContent().toString(), Flash_KX.class);
                    String time = "00:00";
                    try {
                        time = getTime(kx.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    kxHolder.tvTime.setText(time);
                    kxHolder.tvContent.setText(getString(kx.getTitle()));

                    imgMaxWidth = kxHolder.llContent.getWidth();

                    if (RegexValidateUtil.isEmpty(kx.getImage())) {
                        kxHolder.imageView.setVisibility(View.GONE);
                    } else {
                        kxHolder.imageView.setVisibility(View.VISIBLE);
                        //点击查看大图
                        kxHolder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Glide.with(context)
                                        .load(kx.getImage())
                                        .asBitmap()
                                        .error(R.mipmap.icon_def_video)
                                        .placeholder(R.mipmap.icon_def_video)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super
                                                    Bitmap> glideAnimation) {
                                                if (popupUtil.isShowing()) {
                                                    popupUtil.dismiss();
                                                }

//                                                //图片尺寸
//                                                int width = resource.getWidth();
//                                                int height = resource.getHeight();
//                                                //屏幕尺寸
//                                                DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(context);
//                                                int widthPixels = screenDisplay.widthPixels;
//                                                int heightPixels = screenDisplay.heightPixels;
//                                                //放大1.5倍后的图片尺寸
//                                                double largeWidth = width * 1.5;
//                                                double largeHeight = height * 1.5;
//                                                //放大图片(最大1.5倍),是其宽或高全屏
//                                                if (largeWidth <= widthPixels && largeHeight <= heightPixels) {
//                                                    width *= 1.5;
//                                                    height *= 1.5;
//                                                } else if (largeWidth > widthPixels && largeHeight > heightPixels) {
//                                                    double outWidth = largeWidth - widthPixels;
//                                                    double outHeight = largeHeight - heightPixels;
//                                                    if (outHeight > outWidth) {
//                                                        float size = widthPixels / (float) width;
//                                                        width = widthPixels;
//                                                        height *= size;
//                                                    } else {
//                                                        float size = heightPixels / (float) height;
//                                                        height = heightPixels;
//                                                        width *= size;
//                                                    }
//                                                } else if (largeWidth > widthPixels) {
//                                                    float size = widthPixels / (float) width;
//                                                    width = widthPixels;
//                                                    height *= size;
//                                                } else {
//                                                    float size = heightPixels / (float) height;
//                                                    height = heightPixels;
//                                                    width *= size;
//                                                }

                                                config.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                config.height = WindowManager.LayoutParams.MATCH_PARENT;

                                                popupUtil.setConfig(config);

                                                ViewGroup.LayoutParams layoutParams = ivPop.getLayoutParams();
                                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                ivPop.setLayoutParams(layoutParams);

                                                ivPop.setImageBitmap(resource);
                                                popupUtil.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                            }
                                        });
                            }
                        });
                        ivDownView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new SaveImage(context).execute(kx.getImage());
                            }
                        });

                        final KXViewHolder finalKxHolder = kxHolder;
                        Glide.with(context)
                                .load(kx.getImage())
                                .asBitmap()
                                .error(R.mipmap.icon_def_news)
                                .placeholder(R.mipmap.icon_def_news)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                            glideAnimation) {
//                                        int width = resource.getWidth();//px
//                                        int height = resource.getHeight();
//                                        ViewGroup.LayoutParams layoutParams = finalKxHolder.imageView
// .getLayoutParams();
//                                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                                        //等比例缩放
//                                        finalKxHolder.imageView.setAdjustViewBounds(true);
//                                        finalKxHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                                        if (width > imgMaxWidth) {
//                                            layoutParams.width = imgMaxWidth;
//                                            finalKxHolder.imageView.setLayoutParams(layoutParams);
//                                        }
//                                        if (height > imgMaxHeight) {
//                                            layoutParams.height = imgMaxHeight;
//                                            finalKxHolder.imageView.setLayoutParams(layoutParams);
//                                        }
                                        finalKxHolder.imageView.setImageBitmap(resource);
                                    }
                                });
                    }

                    setOnclick(kxHolder.llMore, kxHolder.tvMore, kxHolder.ivMore, kxHolder.ivShare, kxHolder
                                    .ivCollect, position, kxHolder
                                    .tvContent, null,
                            null, TYPE_KX);

                    setKxTheme(kxHolder, kx);

                    setShowMoreBtn(kxHolder);

                    kxHolder.ivCollect.setSelected(flash.isColloct());

                    break;
                case TYPE_RL:
                    FlashJson flash_rl = (FlashJson) flashJsons.get(position);
                    Flash_RL rl = JSON.parseObject(flash_rl.getContent().toString(), Flash_RL.class);

                    boolean onlyShowHigh = SPUtils.getBoolean(context, SpConstant.FLASH_FILTRATE_HIGH);

                    Glide.with(context).load(String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(rl
                            .getState()))).into(rlHolder
                            .ivFlag);

                    String time2 = "00:00";
                    try {
                        time2 = getTime(rl.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rlHolder.tvTime.setText(time2);

                    rlHolder.tvTitle.setText(rl.getState() + getString(rl.getTitle()));
                    rlHolder.tvContentBefore.setText(context.getResources().getString(R.string.date_describe_Before,
                            rl.getBefore()));
                    rlHolder.tvContentForecast.setText(context.getResources().getString(R.string
                            .date_describe_Forecast, rl.getForecast()));
                    rlHolder.tvContentReality.setText(context.getResources().getString(R.string
                            .date_describe_Reality1, rl.getReality()));
                    rlHolder.tvMore.setVisibility(View.GONE);
                    rlHolder.ivMore.setVisibility(View.GONE);

                    setRlTheme(rlHolder);

                    /**
                     * 前值 后值 等
                     */
                    String describe = context.getResources().getString(R.string.date_describe_Reality1, rl.getReality());

                    final String reality = rl.getReality();
                    setDescribeForegroundColor(rlHolder.tvContentReality, describe, reality);

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


                    setOnclick(rlHolder.llMore, rlHolder.tvMore, rlHolder.ivMore, rlHolder.ivShare, rlHolder
                                    .ivCollect, position, rlHolder
                                    .tvContentBefore, null,
                            null, TYPE_RL);
                    /**
                     * 重要性判断
                     */
                    if (onlyShowHigh) {
                        if (VarConstant.IMPORTANCE_HIGH.equals(rl.getImportance())) {
                            rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
                        } else {
                            rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
//                            return null;
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

                    Glide.with(context).load(left.getImage()).error(R.mipmap.icon_def_news).placeholder(R.mipmap
                            .icon_def_news).into
                            (leftHolder
                                    .ivFlash);


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

                    setNewsTheme(leftHolder, left);

                    setOnclick(leftHolder.llMore, leftHolder.tvMore, leftHolder.ivMore, leftHolder.ivShare,
                            leftHolder.ivCollect, position,
                            leftHolder
                                    .tvContent, VarConstant.SOCKET_FLASH_LEFT, null, TYPE_LEFT);

                    leftHolder.ivCollect.setSelected(flash_left.isColloct());

                    setShowMoreBtn(leftHolder);
                    break;
                case TYPE_RIGHT:
                    FlashJson flash_right = (FlashJson) flashJsons.get(position);
                    Flash_NEWS right = JSON.parseObject(flash_right.getContent().toString(), Flash_NEWS.class);

                    Glide.with(context).load(right.getImage()).error(R.mipmap.icon_def_news).placeholder(R.mipmap
                            .icon_def_news).into
                            (rightHolder
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

                    setNewsTheme(rightHolder, right);

                    setOnclick(rightHolder.llMore, rightHolder.tvMore, rightHolder.ivMore, rightHolder.ivShare,
                            rightHolder.ivCollect,
                            position, rightHolder
                                    .tvContent, VarConstant.SOCKET_FLASH_RIGHT, null, TYPE_RIGHT);

                    rightHolder.ivCollect.setSelected(flash_right.isColloct());
                    setShowMoreBtn(rightHolder);
                    break;
                case TYPE_TOP:
                    FlashJson flash_top = (FlashJson) flashJsons.get(position);
                    Flash_NEWS top = JSON.parseObject(flash_top.getContent().toString(), Flash_NEWS.class);

                    final NEWViewHolder finalTopHolder = topHolder;
                    Glide.with(context).load(top.getImage()).asBitmap().error(R.mipmap.icon_def_news).placeholder(R
                            .mipmap
                            .icon_def_news).into
                            (new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                        glideAnimation) {
                                    int width = resource.getWidth();
                                    int height = resource.getHeight();
                                    int viewWidth = finalTopHolder.ivFlash.getWidth();
                                    float viewHeight = (int) (((float) viewWidth) / width * height);
                                    ViewGroup.LayoutParams layoutParams = finalTopHolder.ivFlash.getLayoutParams();
                                    layoutParams.height = (int) viewHeight;
                                    finalTopHolder.ivFlash.setLayoutParams(layoutParams);
                                    finalTopHolder.ivFlash.setImageBitmap(resource);
                                }
                            });

                    String time5 = "00:00";
                    try {
                        time5 = getTime(top.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    topHolder.tvTime.setText(time5);
                    topHolder.tvContent.setText(getString(top.getTitle()));
                    topHolder.tvContent.setVisibility(View.GONE);

                    setNewsTheme(topHolder, top);

                    setOnclick(topHolder.llMore, topHolder.tvMore, topHolder.ivMore, topHolder.ivShare, topHolder
                                    .ivCollect, position,
                            topHolder.tvContent,
                            VarConstant.SOCKET_FLASH_TOP, topHolder.ivFlash, TYPE_TOP);

                    topHolder.ivCollect.setSelected(flash_top.isColloct());

                    setShowMoreBtn(topHolder);

                    break;
                case TYPE_BOTTOM:
                    FlashJson flash_bottom = (FlashJson) flashJsons.get(position);
                    Flash_NEWS bottom = JSON.parseObject(flash_bottom.getContent().toString(), Flash_NEWS.class);

                    final NEWViewHolder finalBottomHolder = bottomHolder;
                    Glide.with(context).load(bottom.getImage()).asBitmap().error(R.mipmap.icon_def_news).placeholder
                            (R.mipmap
                                    .icon_def_news).into
                            (new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                        glideAnimation) {
                                    int width = resource.getWidth();
                                    int height = resource.getHeight();
                                    int viewWidth = finalBottomHolder.ivFlash.getWidth();
                                    float viewHeight = (int) (((float) viewWidth) / width * height);
                                    ViewGroup.LayoutParams layoutParams = finalBottomHolder.ivFlash.getLayoutParams();
                                    layoutParams.height = (int) viewHeight;
                                    finalBottomHolder.ivFlash.setLayoutParams(layoutParams);
                                    finalBottomHolder.ivFlash.setImageBitmap(resource);
                                }
                            });

                    String time6 = "00:00";
                    try {
                        time6 = getTime(bottom.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bottomHolder.tvTime.setText(time6);
                    bottomHolder.tvContent.setText(getString(bottom.getTitle()));

                    bottomHolder.ivFlash.setVisibility(View.GONE);

                    setNewsTheme(bottomHolder, bottom);

                    setOnclick(bottomHolder.llMore, bottomHolder.tvMore, bottomHolder.ivMore, bottomHolder.ivShare,
                            bottomHolder.ivCollect,
                            position, bottomHolder
                                    .tvContent, VarConstant.SOCKET_FLASH_BOTTOM, bottomHolder.ivFlash, TYPE_BOTTOM);

                    bottomHolder.ivCollect.setSelected(flash_bottom.isColloct());
                    setShowMoreBtn(bottomHolder);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * 显示或隐藏显示更多按钮
     *
     * @param kxHolder
     */
    private void setShowMoreBtn(BaseViewHolder kxHolder) {
        final BaseViewHolder finalKxHolder = kxHolder;
        kxHolder.tvContent.post(new Runnable() {
            @Override
            public void run() {
                //获取textView的行数
                int txtPart = finalKxHolder.tvContent.getLineCount();
                if (txtPart <= 3) {
                    finalKxHolder.ivMore.setVisibility(View.GONE);
                    finalKxHolder.tvMore.setVisibility(View.GONE);
                } else {
                    finalKxHolder.ivMore.setVisibility(View.VISIBLE);
                    finalKxHolder.tvMore.setVisibility(View.VISIBLE);
//                    finalKxHolder.extendContent.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                }
            }
        });
    }

    /**
     * 绑定点击事件
     *
     * @param llMore
     * @param tvMore
     * @param ivMore
     * @param ivShare
     * @param ivCollect
     * @param ivFlash
     * @param type
     */
    private void setOnclick(LinearLayout llMore, final TextView tvMore, final ImageView ivMore, final ImageView
            ivShare, final ImageView
                                    ivCollect, int position,
                            final TextView content,
                            String weizhi, final ImageView ivFlash, final int type) {
        final FlashJson flash = (FlashJson) flashJsons.get(position);

        if (CollectUtils.isCollect(context, VarConstant.COLLECT_TYPE_FLASH, flash)) {
            ivCollect.setSelected(true);
        } else {
            ivCollect.setSelected(false);
        }
        showMore(tvMore, ivMore, flash.isShowMore(), content, ivFlash, type);

        llMore.setOnClickListener(new View.OnClickListener() {
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

        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flash.isColloct()) {
                    CollectUtils.unCollect(context, VarConstant.COLLECT_TYPE_FLASH, flash, new ObserverData() {
                        @Override
                        public void callback(Object o) {
                            flash.setColloct(false);
                            ivCollect.setSelected(false);
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastView.makeText3(context, "取消收藏失败");
                        }
                    }, null);
                } else {
                    CollectUtils.collect(context, VarConstant.COLLECT_TYPE_FLASH, flash, new ObserverData() {
                        @Override
                        public void callback(Object o) {
                            flash.setColloct(true);
                            ivCollect.setSelected(true);
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastView.makeText3(context, "收藏失败");
                        }
                    }, null);
                }
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String appConfig = SPUtils.getString(context, SpConstant.INIT_LOAD_APP_CONFIG);
                    MainInitJson config = JSON.parseObject(appConfig, MainInitJson.class);
                    String url_kx_share = config.getUrl_kx_share();

                    String shareUrl = url_kx_share.replace("{id}", flash.getUid());
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
                            discription = context.getResources().getString(R.string.date_describe1,
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

                    UmengShareBean umengShareBean = new UmengShareBean();
                    umengShareBean.setTitle(title);
                    umengShareBean.setDetail(discription);
                    umengShareBean.setImageUrl(image);
                    umengShareBean.setWebUrl(shareUrl);

                    umengShareBean.setFromSource(UmengShareUtil.SHARE_KX);

                    UmengShareUI umengShareUI = new UmengShareUI((BaseActivity) context);
                    umengShareUI.showSharePopup(umengShareBean);
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
    private void showMore(TextView tvMore, ImageView ivMore, boolean isShowMore, TextView content, ImageView ivFlash,
                          int type) {
        if (isShowMore) {
            ivMore.setSelected(true);
            tvMore.setText("收起");
            if (content != null) {
                content.setMaxLines(Integer.MAX_VALUE);
            }
//            switch (type) {
//                case TYPE_TOP:
//                    content.setVisibility(View.VISIBLE);
//                    break;
//                case TYPE_BOTTOM:
//                    ivFlash.setVisibility(View.VISIBLE);
//                    break;
//            }
        } else {
            ivMore.setSelected(false);
            tvMore.setText("展开");
            if (content != null) {
                content.setMaxLines(3);
            }
//            switch (type) {
//                case TYPE_TOP:
//                    content.setVisibility(View.GONE);
//                    break;
//                case TYPE_BOTTOM:
//                    ivFlash.setVisibility(View.GONE);
//                    break;
//            }
        }

        if (ivFlash != null) {
            ivFlash.setVisibility(View.VISIBLE);
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
                tvDescribe.setTextColor(ContextCompat.getColor(context, R.color.rise_color));

            } else if (realityFloat < 0) {
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.decline_color)),
                        index,
                        describe.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvDescribe.setTextColor(ContextCompat.getColor(context, R.color.decline_color));
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

        } catch (NumberFormatException e) {

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

//        if (effectType != 2) {
//            if (effect != null && !"".equals(effect.trim())) {
//                String[] effect0Split = effect.split(" ");
//
//                for (int i = 0; i < effect0Split.length; i++) {
//                    RadianDrawable effectDrawable = new RadianDrawable(context);
//
//                    TextView textView = generateTextView();
//                    textView.setText(effect0Split[i]);
//
//                    effectDrawable.setStroke(shapeColor);
//                    textView.setTextColor(ContextCompat.getColor(context, shapeColor));
//
//                    textView.setBackground(effectDrawable);
//
//                    textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
//                    llExponent.addView(textView);
//                }
//            }
//        } else {
//            RadianDrawable effectDrawable = new RadianDrawable(context);
//
//            TextView textView = generateTextView();
//            textView.setText(effect);
//
//            effectDrawable.setStroke(shapeColor);
//            textView.setTextColor(ContextCompat.getColor(context, shapeColor));
//            textView.setBackground(effectDrawable);
//
//            llExponent.addView(textView);
//        }
        if (effectType != 2) {
            if (effect != null && !"".equals(effect.trim())) {
                String[] effect0Split = effect.split(" ");
                Set<String> set = SPUtils.getStringSet(context, SpConstant.FLASH_FILTRATE);
                for (int i = 0; i < effect0Split.length; i++) {
                    String splitTrim = effect0Split[i].trim();
                    if ("外汇".equals(splitTrim)) {
                        splitTrim = "美元";
                    }
                    if (set.size() == 0 ||
                            set.contains("全部") ||
                            set.contains(splitTrim)) {

                        RadianDrawable effectDrawable = new RadianDrawable(context);

                        TextView textView = generateTextView();
                        textView.setText(splitTrim);

                        effectDrawable.setStroke(shapeColor);
                        textView.setTextColor(ContextCompat.getColor(context, shapeColor));

                        textView.setBackground(effectDrawable);

                        textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                        llExponent.addView(textView);
                    }
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
        Object obj = flashJsons.get(position);
        if (obj instanceof String) return TYPE_TIME;
        FlashJson flashJson = (FlashJson) obj;
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
        return 7;
    }

    public void inspiritDateInfo(List flashJsons) {
        timeMap.clear();
        if (flashJsons == null) return;

        Iterator it = flashJsons.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if (obj instanceof String) {
                it.remove();
            }
        }

        int size = flashJsons.size();

        for (int i = 0; i < size; i++) {
            FlashJson flashJson = (FlashJson) flashJsons.get(i);
            Object content = flashJson.getContent();
            String code = flashJson.getCode();
            if (content == null) return;
            String time = "";
            switch (code) {
                case VarConstant.SOCKET_FLASH_CJRL:
                    //日历
                    time = JSON.parseObject(content.toString(), Flash_RL.class).getTime();
                    break;
                case VarConstant.SOCKET_FLASH_KUAIXUN:
                    //快讯
                    time = JSON.parseObject(content.toString(), Flash_KX.class).getTime();
                    break;
                case VarConstant.SOCKET_FLASH_KXTNEWS:
                    //图文
                    time = JSON.parseObject(content.toString(), Flash_NEWS.class).getTime();
                    break;
            }

            if (!TextUtils.isEmpty(time)) {
                String ymdWeek = DateUtils.getYMDWeek(time);

                if (!timeMap.containsKey(ymdWeek)) {
                    timeMap.put(ymdWeek, i);
                    if (i < size) {
                        flashJsons.add(i, ymdWeek);
                    } else {
                        flashJsons.add(ymdWeek);
                    }
                }
            }
        }

    }

    public void inspiritDateInfo2(List flashJsons) {

        if (flashJsons == null) return;

        int size = flashJsons.size();
        for (int i = 0; i < size; i++) {
            Object mConvertFlash = flashJsons.get(i);
            if (mConvertFlash instanceof FlashJson) {
                FlashJson flashJson = (FlashJson) mConvertFlash;
                Object content = flashJson.getContent();
                String code = flashJson.getCode();
                if (content == null) return;
                String time = "";
                switch (code) {
                    case VarConstant.SOCKET_FLASH_CJRL:
                        //日历
                        time = JSON.parseObject(content.toString(), Flash_RL.class).getTime();
                        break;
                    case VarConstant.SOCKET_FLASH_KUAIXUN:
                        //快讯
                        time = JSON.parseObject(content.toString(), Flash_KX.class).getTime();
                        break;
                    case VarConstant.SOCKET_FLASH_KXTNEWS:
                        //图文
                        time = JSON.parseObject(content.toString(), Flash_NEWS.class).getTime();
                        break;
                }

                if (!TextUtils.isEmpty(time)) {

                    String MD = DateUtils.getYMDWeek(time);
                    if (!timeMap.containsKey(MD)) {
                        timeMap.put(MD, i);
                        if (i < size) {
                            flashJsons.add(i, MD);
                        } else {
                            flashJsons.add(MD);
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }

    private void setNewsTheme(NEWViewHolder holder, Flash_NEWS news) {
        holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        holder.tvMore.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        holder.ivMore.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_flash_show_hide));
        holder.ivShare.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_flash_share));
        holder.vLine.setBackground(ContextCompat.getDrawable(context, R.color.line_color3));
        holder.ivCollect.setBackground(ContextCompat.getDrawable(context, R.drawable.sel_flash_item_collect));

        if (VarConstant.IMPORTANCE_HIGH.equals(news.getImportance())) {
            holder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
        } else {
            holder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
        }
    }

    private void setRlTheme(RLViewHolder rlHolder) {
        rlHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        rlHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_color5));
        rlHolder.tvContentBefore.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        rlHolder.tvContentForecast.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        rlHolder.tvContentReality.setTextColor(ContextCompat.getColor(context, R.color.font_color3));
        rlHolder.tvMore.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        rlHolder.ivMore.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_flash_show_hide));
        rlHolder.ivShare.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_flash_share));
        rlHolder.vLine.setBackground(ContextCompat.getDrawable(context, R.color.line_color3));
        rlHolder.ivCollect.setBackground(ContextCompat.getDrawable(context, R.drawable.sel_flash_item_collect));
    }

    private void setKxTheme(KXViewHolder kxHolder, Flash_KX kx) {
        kxHolder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        kxHolder.tvMore.setTextColor(ContextCompat.getColor(context, R.color.font_color6));
        kxHolder.ivMore.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_flash_show_hide));
        kxHolder.ivShare.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_flash_share));
        kxHolder.vLine.setBackground(ContextCompat.getDrawable(context, R.color.line_color3));
        if (VarConstant.IMPORTANCE_HIGH.equals(kx.getImportance())) {
            kxHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color11));
        } else {
            kxHolder.tvContent.setTextColor(ContextCompat.getColor(context, R.color.font_color1));
        }
        kxHolder.ivCollect.setBackground(ContextCompat.getDrawable(context, R.drawable.sel_flash_item_collect));
    }


    private String getString(String str) {
        if (str == null) return null;
        return str.replace("<br />", "\n").replace("<br/>", "\n");
    }

    private String getTime(String time) throws Exception {
        String[] splitTime = time.split(" ");
        String[] splitTime2 = splitTime[1].split(":");
        return splitTime2[0] + ":" + splitTime2[1];
    }

    public boolean isAdapterNullData() {
        return flashJsons == null || flashJsons.size() == 0;
    }

    /**
     * 筛选
     *
     * @param isFilterHeight
     */
    public void filtrate(boolean isFilterHeight) {
        if (isFilterHeight) {
            justLookImportant();
            filterNullData();
        }
        notifyDataSetChanged();
    }

    /**
     * 时间
     */
    class TimeViewHolder {
        @BindView(R.id.tv_time_day)
        TextView tvTime;

        TimeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 普通快讯
     */
    class KXViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_flash)
        ImageView imageView;
        @BindView(R.id.ll_content)
        LinearLayout llContent;

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
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_describe_Before)
        TextView tvContentBefore;
        @BindView(R.id.tv_describe_Forecast)
        TextView tvContentForecast;
        @BindView(R.id.tv_describe_Reality)
        TextView tvContentReality;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.iv_share)
        ImageView ivShare;
        @BindView(R.id.iv_collect)
        ImageView ivCollect;
        @BindView(R.id.v_line)
        View vLine;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.ll_exponent)
        LinearLayout llExponent;
        @BindView(R.id.ll_star)
        StarView star;
        @BindView(R.id.iv_guoqi)
        ImageView ivFlag;
        @BindView(R.id.ll_more)
        LinearLayout llMore;

        public RLViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class BaseViewHolder {
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.iv_share)
        ImageView ivShare;
        @BindView(R.id.iv_collect)
        ImageView ivCollect;
        @BindView(R.id.v_line)
        View vLine;

        @BindView(R.id.ll_more)
        LinearLayout llMore;
    }
}
