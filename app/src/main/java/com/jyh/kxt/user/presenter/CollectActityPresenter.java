package com.jyh.kxt.user.presenter;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.user.ui.CollectActivity;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.listener.OnTabSelectListener;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:luozhimin
 * 创建日期:2017/7/11.
 */


public class CollectActityPresenter extends BasePresenter implements DelNumListener,  ViewPager.OnPageChangeListener, OnTabSelectListener {
    @BindObject CollectActivity collectActivity;

    private boolean isVideoEdit, isNewsEdit, isFlashEdit, isAuthorEdit;
    private boolean isVideoAllSel, isNewsAllSel, isFlashAllSel, isAuthorAllSel;
    private TSnackbar snackBar;
    private int delNum;
    public CollectActityPresenter(IBaseView iBaseView) {super(iBaseView);}

    public void onChangeTheme(){
        if (collectActivity.flashFragment != null)
            collectActivity.flashFragment.onChangeTheme();
        if (collectActivity.newsFragment != null)
            collectActivity.newsFragment.onChangeTheme();
        if (collectActivity.videoFragment != null) {
            collectActivity.videoFragment.onChangeTheme();
        }
        if (collectActivity.authorFragment != null)
            collectActivity.authorFragment.onChangeTheme();
        if (collectActivity.stlNavigationBar != null)
            collectActivity.stlNavigationBar.notifyDataSetChanged();
    }

    public void onBackPressed(){
        if (collectActivity.llDel.getVisibility() == View.VISIBLE) {
            switch (collectActivity.vpContent.getCurrentItem()) {
                case 0:
                    collectActivity.videoFragment.quitEdit(this);
                    break;
                case 1:
                    collectActivity.newsFragment.quitEdit(this);
                    break;
                case 2:
                    collectActivity.flashFragment.quitEdit(this);
                    break;
                case 3:
                    collectActivity.authorFragment.quitEdit(this);
                    break;
            }
        } else
            collectActivity.superBackPressed();
    }

    /**
     * 判断控制是否可以滑动和点击viewPager
     * @param isEdit
     */
    private void editMode(boolean isEdit) {
        collectActivity.stlNavigationBar.setClickable(!isEdit);
        collectActivity.vpContent.setScrollable(!isEdit);
    }

    /**
     * 编辑
     */
    public void editOpe(){
        //编辑
        if (snackBar != null && snackBar.isShownOrQueued()) {

            editMode(false);
        } else {

            switch (collectActivity.vpContent.getCurrentItem()) {
                case 0:
                    if (collectActivity.videoFragment.adapter != null && collectActivity.videoFragment.adapter.getData().size() > 0) {
                        isVideoEdit = !isVideoEdit;
                    } else {
                        isVideoEdit = false;
                    }
                    collectActivity.videoFragment.edit(isVideoEdit, this);
                    if (isVideoEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(isVideoEdit);
                    break;
                case 1:
                    if (collectActivity.newsFragment.adapter != null && collectActivity.newsFragment.adapter.getData().size() > 0) {
                        isNewsEdit = !isNewsEdit;
                    } else {
                        isNewsEdit = false;
                    }
                    collectActivity.newsFragment.edit(isNewsEdit, this);
                    if (isNewsEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(isNewsEdit);
                    break;
                case 2:
                    if (collectActivity.flashFragment.adapter != null && collectActivity.flashFragment.adapter.getData().size() > 0) {
                        isFlashEdit = !isFlashEdit;
                    } else {
                        isFlashEdit = false;
                    }
                    collectActivity.flashFragment.edit(isFlashEdit, this);
                    if (isFlashEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(isFlashEdit);
                    break;
                case 3:
                    if (collectActivity.authorFragment.adapter != null && collectActivity.authorFragment.adapter.getData().size() > 0) {
                        isAuthorEdit = !isAuthorEdit;
                    } else {
                        isAuthorEdit = false;
                    }
                    collectActivity.authorFragment.edit(isAuthorEdit, this);
                    if (isAuthorEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(isAuthorEdit);
                    break;
            }
        }
    }

    /**
     * 全选
     */
    public void selectAll(){
        //全选
        boolean selected = collectActivity.ivSelAll.isSelected();
        collectActivity.ivSelAll.setSelected(!selected);
        switch (collectActivity.vpContent.getCurrentItem()) {
            case 0:
                isVideoAllSel = !selected;
                collectActivity.videoFragment.selAll(isVideoAllSel, this);
                break;
            case 1:
                isNewsAllSel = !selected;
                collectActivity.newsFragment.selAll(isNewsAllSel, this);
                break;
            case 2:
                isFlashAllSel = !selected;
                collectActivity.flashFragment.selAll(isFlashAllSel, this);
                break;
            case 3:
                isAuthorAllSel = !selected;
                collectActivity.authorFragment.selAll(isAuthorAllSel, this);
                break;
        }
    }

    /**
     * 删除操作
     */
    public void deleteOpe(){
        //删除
        if (delNum != 0) {
            snackBar = TSnackbar.make(collectActivity.tvDel, "删除收藏中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
            snackBar.setPromptThemBackground(Prompt.SUCCESS);
            snackBar.addIconProgressLoading(0, true, false);
            snackBar.show();
            switch (collectActivity.vpContent.getCurrentItem()) {
                case 0:
                    collectActivity.videoFragment.del(this);
                    break;
                case 1:
                    collectActivity.newsFragment.del(this);
                    break;
                case 2:
                    collectActivity.flashFragment.del(this);
                    break;
                case 3:
                    collectActivity.authorFragment.del(this);
                    break;
            }
        } else {
            ToastView.makeText3(collectActivity, "请选中至少一项");
        }
    }

    @Override
    public void delItem(Integer num) {
        try {
            collectActivity.tvDel.setText("删除(" + num + ")");
            delNum = num;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delError() {
        snackBar.setPromptThemBackground(Prompt.ERROR).setText("删除收藏失败").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(collectActivity.getContext()), collectActivity.getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delSuccessed() {
        snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("删除收藏成功").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(collectActivity.getContext()),collectActivity.getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delAll(boolean isAll) {
        switch (collectActivity.vpContent.getCurrentItem()) {
            case 0:
                isVideoAllSel = isAll;
                break;
            case 1:
                isNewsAllSel = isAll;
                break;
            case 2:
                isFlashAllSel = isAll;
                break;
            case 3:
                isAuthorAllSel = isAll;
                break;
        }
        collectActivity.ivSelAll.setSelected(isAll);
    }

    @Override
    public void quitEdit() {
        collectActivity.llDel.setVisibility(View.GONE);
        collectActivity.ivSelAll.setSelected(false);
        switch (collectActivity.vpContent.getCurrentItem()) {
            case 0:
                isVideoEdit = false;
                isVideoAllSel = false;
//                videoFragment.quitEdit(numListener);
                break;
            case 1:
                isNewsEdit = false;
                isNewsAllSel = false;
//                newsFragment.quitEdit(numListener);
                break;
            case 2:
                isFlashEdit = false;
                isFlashAllSel = false;
//                flashFragment.quitEdit(numListener);
                break;
            case 3:
                isAuthorEdit = false;
                isAuthorAllSel = false;
//                authorFragment.quitEdit(numListener);
                break;
        }
        collectActivity.ivBarFunction.setText("编辑");
        editMode(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (collectActivity.videoFragment != null && collectActivity.videoFragment.adapter != null) {
            collectActivity.videoFragment.adapter.notifyDefaul();
        }
        if (collectActivity.newsFragment != null && collectActivity.newsFragment.adapter != null) {
            collectActivity. newsFragment.adapter.notifyDefaul();
        }
        if (collectActivity.flashFragment != null && collectActivity.flashFragment.adapter != null) {
            collectActivity.flashFragment.adapter.notifyDefaul();
        }
        if (collectActivity.authorFragment != null && collectActivity.authorFragment.adapter != null) {
            collectActivity.authorFragment.adapter.notifyDefaul();
        }
        delNum = 0;
        collectActivity.tvDel.setText("删除(" + delNum + ")");
        isAuthorAllSel = false;
        isFlashAllSel = false;
        isNewsAllSel = false;
        isVideoAllSel = false;
        switch (position) {
            case 0:
                if (isVideoEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(isVideoAllSel);
                editMode(isVideoEdit);
                break;
            case 1:
                if (isNewsEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(isNewsAllSel);
                editMode(isNewsEdit);
                break;
            case 2:
                if (isFlashEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(isFlashAllSel);
                editMode(isFlashEdit);
                break;
            case 3:
                if (isAuthorEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(isAuthorAllSel);
                editMode(isAuthorEdit);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
