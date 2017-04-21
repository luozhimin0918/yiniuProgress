package com.jyh.kxt.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.VarConstant;
import com.jyh.kxt.base.utils.PingYinUtil;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List flashJsons;
    private Context context;

    public FastInfoAdapter(List<FlashJson> flashJsons, Context context) {
        this.flashJsons = flashJsons;
        inspiritDateInfo(this.flashJsons);
        this.context = context;
    }

    private Map<String, Integer> timeMap = new HashMap<>();

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
        }

        switch (type) {
            case TYPE_TIME:
                timeHolder.tvTime.setText(flashJsons.get(position).toString());
                break;
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
                setOnclick(kxHolder.tvMore, kxHolder.ivMore, kxHolder.ivShare, kxHolder.ivCollect, position, kxHolder.tvContent, null);
                break;
            case TYPE_RL:
                FlashJson flash_rl = (FlashJson) flashJsons.get(position);
                Flash_RL rl = JSON.parseObject(flash_rl.getContent().toString(), Flash_RL.class);

                Glide.with(context).load(String.format(HttpConstant.FLAG_URL, PingYinUtil.getFirstSpell(rl.getState()))).error(R.mipmap
                        .ico_def_load).placeholder(R.mipmap.ico_def_load).into(leftHolder
                        .ivFlash);

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
                setOnclick(rlHolder.tvMore, rlHolder.ivMore, rlHolder.ivShare, rlHolder.ivCollect, position, null, null);
                break;
            case TYPE_LEFT:
                FlashJson flash_left = (FlashJson) flashJsons.get(position);
                Flash_NEWS left = JSON.parseObject(flash_left.getContent().toString(), Flash_NEWS.class);

                Glide.with(context).load(left.getImage()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(leftHolder
                        .ivFlash);

                String time3 = "00:00";
                try {
                    time3 = getTime(left.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                leftHolder.tvTime.setText(time3);
                leftHolder.tvContent.setText(getString(left.getTitle()));

                setOnclick(leftHolder.tvMore, leftHolder.ivMore, leftHolder.ivShare, leftHolder.ivCollect, position, leftHolder
                        .tvContent, VarConstant.SOCKET_FLASH_LEFT);
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

                setOnclick(rightHolder.tvMore, rightHolder.ivMore, rightHolder.ivShare, rightHolder.ivCollect, position, rightHolder
                        .tvContent, VarConstant.SOCKET_FLASH_RIGHT);
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

                setOnclick(topHolder.tvMore, topHolder.ivMore, topHolder.ivShare, topHolder.ivCollect, position, topHolder.tvContent,
                        VarConstant.SOCKET_FLASH_TOP);
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

                setOnclick(bottomHolder.tvMore, bottomHolder.ivMore, bottomHolder.ivShare, bottomHolder.ivCollect, position, bottomHolder
                        .tvContent, VarConstant.SOCKET_FLASH_BOTTOM);
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
     */
    private void setOnclick(TextView tvMore, ImageView ivMore, ImageView ivShare, ImageView ivCollect, int position, TextView content,
                            String weizhi) {
        final FlashJson flash = (FlashJson) flashJsons.get(position);
        switch (flash.getCode()) {
            case VarConstant.SOCKET_FLASH_CJRL:
                break;
            case VarConstant.SOCKET_FLASH_KUAIXUN:
                break;
            case VarConstant.SOCKET_FLASH_KXTNEWS:
                if (weizhi == null) break;
                switch (weizhi) {
                    case VarConstant.SOCKET_FLASH_LEFT:
                        //左
                        break;
                    case VarConstant.SOCKET_FLASH_RIGHT:
                        //右
                        break;
                    case VarConstant.SOCKET_FLASH_TOP:
                        //顶部
                        break;
                    case VarConstant.SOCKET_FLASH_BOTTOM:
                        //底部
                        break;
                }
                break;
        }

        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flash.isColloct()){
//                    flash.setColloct();
                }
            }
        });
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
                switch (content.getImage_pos()) {
                    case VarConstant.SOCKET_FLASH_LEFT:
                        //左
                        return TYPE_LEFT;
                    case VarConstant.SOCKET_FLASH_RIGHT:
                        //右
                        return TYPE_RIGHT;
                    case VarConstant.SOCKET_FLASH_TOP:
                        //顶部
                        return TYPE_TOP;
                    case VarConstant.SOCKET_FLASH_BOTTOM:
                        //底部
                        return TYPE_BOTTOM;
                }
            default:
                return TYPE_KX;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    public void inspiritDateInfo(List flashJsons) {

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
                String[] splitTime = time.split(" ");
                String month = splitTime[0].split("-")[1];
                String day = splitTime[0].split("-")[2];

                String MD = month + "月" + day; //显示用

                if (!timeMap.containsKey(MD)) {
                    int position = i + timeMap.size();
                    timeMap.put(MD, position);
                    flashJsons.add(position, MD);
                }
            }

        }

    }


    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
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

    /**
     * 时间
     */
    class TimeViewHolder {
        @BindView(R.id.tv_time_day) TextView tvTime;

        TimeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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
        @BindView(R.id.ll_publish) RelativeLayout llContent;
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
