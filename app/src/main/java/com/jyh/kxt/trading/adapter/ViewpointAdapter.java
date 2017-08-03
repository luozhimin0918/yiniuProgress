package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.library.widget.tablayout.NavigationTabLayout;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.library.widget.listview.PinnedSectionListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointAdapter extends BaseListAdapter<ViewPointTradeBean> implements
        PinnedSectionListView.PinnedSectionListAdapter, NavigationTabLayout.OnTabSelectListener {

    private Context mContext;
    private LayoutInflater mInflater;

    private int navigationTabClickPosition = 0;

    private NavigationTabLayout navigationTabLayout;
    private int viewTypeCount = 2;

    public ViewpointAdapter(Context mContext, List<ViewPointTradeBean> dataList) {
        super(dataList);

        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder0 viewHolder0 = null;
        ViewHolder1 viewHolder1 = null;

        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            switch (itemViewType) {
                case 0:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_function_nav, parent, false);
                    viewHolder0 = new ViewHolder0(convertView);
                    convertView.setTag(viewHolder0);
                    navigationTabLayout = (NavigationTabLayout) convertView;
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.view_viewpoint_item1, parent, false);
                    viewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(viewHolder1);
                    break;
            }
        } else {
            switch (itemViewType) {
                case 0:
                    viewHolder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
            }
        }


        switch (itemViewType) {
            case 0:
                viewHolder0.navigationTabLayout.setData(R.array.nav_trading_menu);
                viewHolder0.navigationTabLayout.setOnTabSelectListener(this);
                viewHolder0.navigationTabLayout.setCurrentTab(navigationTabClickPosition);
                break;
            case 1:

                break;
        }


        return convertView;
    }


    @Override
    public void onTabSelect(int position, int clickId) {
        navigationTabClickPosition = position;
        if(clickId == 0){
            notifyDataSetChanged();
        }
    }

    class ViewHolder0 {
        @BindView(R.id.ntl_title_view) NavigationTabLayout navigationTabLayout;

        private View contentView;

        public ViewHolder0(View contentView) {
            this.contentView = contentView;
            ButterKnife.bind(this, contentView);
        }
    }

    class ViewHolder1 {
        private View contentView;

        public ViewHolder1(View contentView) {
            this.contentView = contentView;
        }
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        ViewPointTradeBean viewPointTradeBean = dataList.get(position);
        return viewPointTradeBean.getItemViewType();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 0;
    }
}
