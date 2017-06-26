package com.jyh.kxt.base.annotation;

/**
 * 项目名:Kxt
 * 类描述: 删除item监听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/17.
 */

public interface DelNumListener {
    void delItem(Integer num);

    void delError();

    void delAll(boolean isAll);
}
