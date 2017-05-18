package com.jyh.kxt.datum.presenter;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.datum.bean.DataGroup;
import com.jyh.kxt.datum.bean.DataHot;
import com.jyh.kxt.datum.bean.DataList;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class DataPresenter extends BasePresenter {

    public interface TopTabViewClick {
        void topTabSelected(int position);
    }

    @BindObject DataFragment dataFragment;

    private TopTabViewClick topTabViewClick;
    private List<DataGroup> mDataLeftGroupList;
    private List<DataList> mDataRightList = new ArrayList<>();
    private HashMap<String, List<DataList>> mDataRightMap = new HashMap<>();

    private int mLeftSelectedItem = -1;
    private RadioButton mOldLeftSelectedView;

    public DataPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setTopTabViewClick(TopTabViewClick topTabViewClick) {
        this.topTabViewClick = topTabViewClick;
    }

    /**
     * 顶部热门数据
     */
    public void requestTopNavigationData() {

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject jsonParam = volleyRequest.getJsonParam();
        volleyRequest.doGet(HttpConstant.DATA_HOT, jsonParam, new HttpListener<List<DataHot>>() {
            @Override
            protected void onResponse(List<DataHot> mDataHotList) {
                int tvTitleHeight = SystemUtil.dp2px(mContext, 30);
                int topMargins = SystemUtil.dp2px(mContext, 8);
                int leftMargins = SystemUtil.dp2px(mContext, 15);
                int paddingLeftRight = SystemUtil.dp2px(mContext, 5);


                for (DataHot dataHot : mDataHotList) {

                    TextView tvTitle = new TextView(mContext);
                    tvTitle.setText(dataHot.getName());

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                            .WRAP_CONTENT, tvTitleHeight);

                    lp.setMargins(leftMargins, topMargins, 0, topMargins);

                    tvTitle.setPadding(paddingLeftRight, 0, paddingLeftRight, 0);

                    tvTitle.setGravity(Gravity.CENTER);
                    tvTitle.setBackgroundResource(R.drawable.shape_data_nav_bg);
                    dataFragment.llDataNav.addView(tvTitle, lp);

                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (topTabViewClick != null) {
                                int position = dataFragment.llDataNav.indexOfChild(v);
                                topTabViewClick.topTabSelected(position);
                            }
                        }
                    });
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
            }
        });


    }

    public void requestLeftNavigationData() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject jsonParam = volleyRequest.getJsonParam();
        volleyRequest.doGet(HttpConstant.DATA_GROUP, jsonParam, new HttpListener<List<DataGroup>>() {
            @Override
            protected void onResponse(List<DataGroup> mDataGroupList) {
                mDataLeftGroupList = mDataGroupList;
                initLeftLayout();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
            }
        });

    }


    private void initLeftLayout() {
        BaseListAdapter<DataGroup> adapter = new BaseListAdapter<DataGroup>(mDataLeftGroupList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolder viewHolder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_data_left, null);
                    viewHolder = new ViewHolder();

                    convertView.setTag(viewHolder);
                    viewHolder.rbName = (RadioButton) convertView.findViewById(R.id.tv_name);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                DataGroup dataGroup = mDataLeftGroupList.get(position);
                viewHolder.rbName.setText(dataGroup.getName());

                if (mLeftSelectedItem == position) {
                    viewHolder.rbName.setChecked(true);
                } else {
                    viewHolder.rbName.setChecked(false);
                }

                viewHolder.rbName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioButton rbName = (RadioButton) view;
                        rbName.setChecked(true);

                        if (mOldLeftSelectedView != null && mLeftSelectedItem != position) {
                            mOldLeftSelectedView.setChecked(false);
                        }

                        mLeftSelectedItem = position;
                        mOldLeftSelectedView = rbName;

                        //触发右侧刷新
                        DataGroup dataGroup = mDataLeftGroupList.get(position);
                        initRightLayout(dataGroup.getId());
                    }
                });


                return convertView;
            }

            class ViewHolder {
                RadioButton rbName;
            }
        };
        dataFragment.ivLeftContent.setAdapter(adapter);
        dataFragment.ivLeftContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        try {
            dataFragment.ivLeftContent.post(new Runnable() {
                @Override
                public void run() {
                    View childAt = dataFragment.ivLeftContent.getChildAt(0);
                    RadioButton rbName = (RadioButton) childAt.findViewById(R.id.tv_name);
                    rbName.performClick();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BaseListAdapter<DataList> rightContentAdapter;

    private void initRightLayout(final String groupId) {

        if (dataFragment.ivRightContent.getAdapter() == null) {
            rightContentAdapter = new BaseListAdapter<DataList>(mDataRightList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataList dataList = mDataRightList.get(position);

                    ViewHolder mViewHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_data_right, null);
                        mViewHolder = new ViewHolder();
                        convertView.setTag(mViewHolder);

                        mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    } else {
                        mViewHolder = (ViewHolder) convertView.getTag();
                    }

                    if ("hot".equals(dataList.getStyle_class())) {
                        String name = dataList.getName() + ".";
                        SpannableString msp = new SpannableString(name);

                        int gapWidth = SystemUtil.dp2px(mContext, 6);


                        CircleDrawable mCircleDrawable = new CircleDrawable(Color.RED);
                        mCircleDrawable.setBounds(0, 0, gapWidth, gapWidth);

                        ImageSpan what = new MyImageSpan(mCircleDrawable);

                        msp.setSpan(what, name.length() - 1, name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                        mViewHolder.tvTitle.setText(msp);
                    } else {
                        mViewHolder.tvTitle.setText(dataList.getName());
                    }


                    return convertView;

                }

                class ViewHolder {
                    TextView tvTitle;
                }
            };
            dataFragment.ivRightContent.setAdapter(rightContentAdapter);
        }


        if (mDataRightMap.get(groupId) != null) {

            mDataRightList.clear();
            mDataRightList.addAll(mDataRightMap.get(groupId));
            rightContentAdapter.notifyDataSetChanged();
            return;
        }

        dataFragment.ivRightContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mContext.startActivity(new Intent(mContext, DatumHistoryActivity.class));
            }
        });


        dataFragment.pllRightContent.loadWait(PageLoadLayout.BgColor.TRANSPARENT, null);
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("id", groupId);
        volleyRequest.doGet(HttpConstant.DATA_LIST, jsonParam, new HttpListener<List<DataList>>() {
            @Override
            protected void onResponse(List<DataList> mDataList) {
                mDataRightMap.put(groupId, mDataList);

                mDataRightList.clear();
                mDataRightList.addAll(mDataList);
                rightContentAdapter.notifyDataSetChanged();
                dataFragment.pllRightContent.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                dataFragment.pllRightContent.loadError();
            }
        });
    }

    private class CircleDrawable extends GradientDrawable {
        public CircleDrawable(int mColor) {

            int dottedSize = SystemUtil.dp2px(mContext, 3);
            setOrientation(Orientation.LEFT_RIGHT);
            setColor(mColor);
            setCornerRadius(dottedSize);
        }
    }

    public class MyImageSpan extends ImageSpan {

        public MyImageSpan(Drawable d) {
            super(d);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas,
                         CharSequence text,
                         int start, int end,
                         float x,
                         int top,
                         int y,
                         int bottom,
                         Paint paint) {
            Drawable b = getDrawable();
            canvas.save();
            canvas.translate(x, top);
            b.draw(canvas);
            canvas.restore();
        }
    }
}
