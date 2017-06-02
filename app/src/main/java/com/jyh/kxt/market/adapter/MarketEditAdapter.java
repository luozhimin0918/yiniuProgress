package com.jyh.kxt.market.adapter;
/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.widget.helper.ItemTouchHelperAdapter;
import com.jyh.kxt.base.widget.helper.ItemTouchHelperViewHolder;
import com.jyh.kxt.base.widget.helper.OnStartDragListener;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketEditActivity;
import com.library.widget.PageLoadLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class MarketEditAdapter extends RecyclerView.Adapter<MarketEditAdapter.ItemViewHolder> implements
        ItemTouchHelperAdapter {
    private MarketEditActivity marketEditActivity;
    public List<MarketItemBean> listContent;
    private PageLoadLayout plEditPage;
    private OnStartDragListener mDragStartListener;
    private HashMap<MarketItemBean, Boolean> marketSelectedStatus = new HashMap<>();

    public MarketEditAdapter(MarketEditActivity marketEditActivity, OnStartDragListener dragStartListener,
                             List<MarketItemBean> listContent,
                             PageLoadLayout plEditPage) {

        this.marketEditActivity = marketEditActivity;
        this.listContent = listContent;
        this.plEditPage = plEditPage;
        this.mDragStartListener = dragStartListener;
    }

    public void completeChecked(boolean isChecked) {
        for (MarketItemBean marketItemBean : listContent) {
            marketSelectedStatus.put(marketItemBean, isChecked);
        }
        updateDeleteCount();
        notifyDataSetChanged();
    }

    public List<MarketItemBean> getCheckedList() {
        List<MarketItemBean> positionList = new ArrayList<>();

        for (int i = 0; i < listContent.size(); i++) {
            MarketItemBean marketItemBean = listContent.get(i);
            Boolean aBoolean = marketSelectedStatus.get(marketItemBean);
            if (aBoolean != null && aBoolean) {
                positionList.add(marketItemBean);
            }
        }
        return positionList;
    }

    private void updateDeleteCount() {

        int selectedCount = 0;
        Set<MarketItemBean> marketItemSet = marketSelectedStatus.keySet();
        Iterator mIterator = marketItemSet.iterator();
        while (mIterator.hasNext()) {
            MarketItemBean key = (MarketItemBean) mIterator.next();
            Boolean aBoolean = marketSelectedStatus.get(key);
            if (aBoolean) {
                selectedCount++;
            }
        }
        if (selectedCount == 0 && marketEditActivity.cbCompleteChecked.isChecked()) {
            marketEditActivity.cbCompleteChecked.setChecked(false);
        } else if (selectedCount == listContent.size() && !marketEditActivity.cbCompleteChecked.isChecked()) {
            marketEditActivity.cbCompleteChecked.setChecked(true);
        } else if (selectedCount != 0 && !marketEditActivity.cbCompleteChecked.isChecked()) {
            marketEditActivity.cbCompleteChecked.setChecked(true);
        }

        marketEditActivity.tvSelectedCount.setText("删除(" + selectedCount + ")");
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_opt_edit, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final MarketItemBean marketItemBean = listContent.get(position);

        holder.nameView.setText(marketItemBean.getName());

        Boolean isChecked = marketSelectedStatus.get(marketItemBean);
        if (isChecked == null || !isChecked) {
            holder.nameView.setChecked(false);
        } else {
            holder.nameView.setChecked(true);
        }

        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox myCheckBox = (CheckBox) v;
                marketSelectedStatus.put(marketItemBean, myCheckBox.isChecked());
                updateDeleteCount();
            }
        });

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });


        holder.topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOf = listContent.indexOf(marketItemBean);
                if (indexOf != -1) {
                    onItemMove(indexOf, 0);
                }
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        marketSelectedStatus.remove(listContent.get(position));
        updateDeleteCount();

        listContent.remove(position);

        notifyItemRemoved(position);

        if (listContent.size() == 0) {
            freePageNoData();
        }
    }

    public void freePageNoData() {
        plEditPage.loadEmptyData();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        MarketItemBean marketItemBean = listContent.get(fromPosition);
        listContent.remove(marketItemBean);
        listContent.add(toPosition, marketItemBean);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return listContent.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public final CheckBox nameView;//名称
        public final ImageView handleView; //拖动
        public final ImageView topView; //置顶

        public ItemViewHolder(View itemView) {
            super(itemView);

            nameView = (CheckBox) itemView.findViewById(R.id.opt_edit_chebox);

            handleView = (ImageView) itemView.findViewById(R.id.opt_edit_drag);
            topView = (ImageView) itemView.findViewById(R.id.opt_edit_top);
        }

        @Override
        public void onItemSelected() {
            int color = ContextCompat.getColor(marketEditActivity, R.color.line_background);
            itemView.setBackgroundColor(color);
        }

        @Override
        public void onItemClear() {
            int color = ContextCompat.getColor(marketEditActivity, R.color.theme1);
            itemView.setBackgroundColor(color);
        }
    }

}
