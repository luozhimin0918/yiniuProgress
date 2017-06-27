package com.jyh.kxt.base.annotation;

/**
 * 项目名:Kxt
 * 类描述: 删除item监听
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/17.
 */

public interface DelNumListener {
    /**
     * 删除选中数量发生改变
     * @param num
     */
    void delItem(Integer num);

    /**
     * 删除失败
     */
    void delError();

    /**
     * 删除成功
     */
    void delSuccessed();

    /**
     * 是否选中全部item
     * @param isAll
     */
    void delAll(boolean isAll);

    /**
     * 退出删除模式
     */
    void quitEdit();
}
