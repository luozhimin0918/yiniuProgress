package com.jyh.kxt.main.presenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemPresenter extends BasePresenter {

    @BindObject
    NewsItemFragment newsItemFragment;

    private boolean isMain;//是否为首页

    private List<SlideJson> slides;//幻灯片
    private List<SlideJson> ads;//广告
    private List<SlideJson> shortcuts;//按钮
    private List<QuotesJson> quotes;//行情
    private List<NewsJson> news;

    public NewsItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setAdapter() {
        List list = getList();
        newsItemFragment.plvContent.setAdapter(new BaseListAdapter<Object>(list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, null);
                    holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.ivPhoto.setImageResource(R.mipmap.icon_test);
                holder.tvTitle.setText("fdafdadfdh");
                holder.tvAuthor.setText("文/快讯通");
                holder.tvTime.setText("18:18");

                return convertView;
            }

            class ViewHolder {
                public TextView tvTitle, tvAuthor, tvTime;
                public ImageView ivPhoto;
            }
        });
    }

    private List getList() {
        List list = new ArrayList();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        return list;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public void init(Bundle arguments) {
        if (isMain) {
            slides = arguments.getParcelableArrayList(IntentConstant.NEWS_SLIDE);
            shortcuts = arguments.getParcelableArrayList(IntentConstant.NEWS_SHORTCUTS);
            quotes = arguments.getParcelableArrayList(IntentConstant.NEWS_QUOTES);
            ads = arguments.getParcelableArrayList(IntentConstant.NEWS_ADS);
            news = arguments.getParcelableArrayList(IntentConstant.NEWS_NEWS);
        } else {

        }
    }
}
