package com.jyh.kxt.base.util.emoje.view.I;


import com.jyh.kxt.base.util.emoje.bean.EmoticonBean;

public interface IView {
    void onItemClick(EmoticonBean bean);
    void onItemDisplay(EmoticonBean bean);
    void onPageChangeTo(int position);
}
