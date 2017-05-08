package com.jyh.kxt.base.annotation;

import android.view.View;

/**
 * 项目名:Kxt
 * 类描述:RecyclerView item点击事件监听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public interface OnItemClickListener {
    void onItemClick(int position, View view);
}
