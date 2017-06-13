package com.jyh.kxt.user.presenter;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.user.ui.fragment.CollectFlashFragment;
import com.library.base.http.VarConstant;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/12.
 */

public class CollectFlashPresenter extends BasePresenter {

    @BindObject CollectFlashFragment collectFlashFragment;

    private List<FlashJson> newsJsons;//当前填充的数据
    private List<FlashJson> newsAll;//所有数据
    private boolean isMore;//是否拥有更多数据
    private int pageCount = 1;
    private int currentPage = 1;//当前页码

    public CollectFlashPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void loadMore() {
        if (isMore) {
            List<FlashJson> newsMore;
            currentPage++;
            if (currentPage < pageCount) {
                newsMore = newsAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, currentPage * VarConstant.LIST_MAX_SIZE);
            } else {
                isMore = false;
                newsMore = newsAll.subList((currentPage - 1) * VarConstant.LIST_MAX_SIZE, newsAll.size());
            }
            collectFlashFragment.loadMore(newsMore);
        } else {
            collectFlashFragment.lvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collectFlashFragment.lvContent.getRefreshableView().goneFoot2();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 500);
        }
    }

    public void refresh() {
        CollectUtils.getCollectData(mContext, VarConstant.COLLECT_TYPE_FLASH,"",new ObserverData<List>() {
            @Override
            public void callback(List list) {
                if (list == null || list.size() == 0) {
                    collectFlashFragment.lvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            collectFlashFragment.lvContent.onRefreshComplete();
                        }
                    }, 500);
                } else {
                    int size = list.size();
                    pageCount = size / VarConstant.LIST_MAX_SIZE;
                    int i = size % VarConstant.LIST_MAX_SIZE;
                    if (i != 0)
                        pageCount++;

                    if (pageCount == 1) {
                        isMore = false;
                        currentPage = 1;
                        newsJsons = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        newsAll = list;
                        newsJsons = newsAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }
                    List<FlashJson> adapterSourceList = new ArrayList<>(newsJsons);
                    collectFlashFragment.refresh(adapterSourceList);
                }
            }

            @Override
            public void onError(Exception e) {
                collectFlashFragment.lvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        collectFlashFragment.lvContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }

    public void init() {
        collectFlashFragment.plRootView.loadWait();
        collectFlashFragment.plRootView.setNullImgId(0);
        CollectUtils.getCollectData(mContext, VarConstant.COLLECT_TYPE_FLASH,"", new ObserverData<List>() {
            @Override
            public void callback(List list) {
                if (list == null || list.size() == 0) {
                    collectFlashFragment.plRootView.setNullImgId(R.mipmap.icon_collect_null);
                    collectFlashFragment.plRootView.setNullText(mContext.getString(R.string.error_collect_null));
                    collectFlashFragment.plRootView.loadEmptyData();
                } else {
                    int size = list.size();
                    pageCount = size / VarConstant.LIST_MAX_SIZE;
                    int i = size % VarConstant.LIST_MAX_SIZE;
                    if (i != 0)
                        pageCount++;

                    if (pageCount == 1) {
                        isMore = false;
                        currentPage = 1;
                        newsJsons = list;
                    } else {
                        isMore = true;
                        currentPage = 1;
                        newsAll = list;
                        newsJsons = newsAll.subList(0, VarConstant.LIST_MAX_SIZE);
                    }

                    List<FlashJson> adapterSourceList = new ArrayList<>(newsJsons);
                    collectFlashFragment.init(adapterSourceList);
                    collectFlashFragment.plRootView.loadOver();
                }
            }

            @Override
            public void onError(Exception e) {
                collectFlashFragment.plRootView.loadError();
            }
        });
    }
}
