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
        if (collectActivity.snackBar != null && collectActivity.snackBar.isShownOrQueued()) {

            editMode(false);
        } else {

            switch (collectActivity.vpContent.getCurrentItem()) {
                case 0:
                    if (collectActivity.videoFragment.adapter != null && collectActivity.videoFragment.adapter.getData().size() > 0) {
                        collectActivity.isVideoEdit = !collectActivity.isVideoEdit;
                    } else {
                        collectActivity.isVideoEdit = false;
                    }
                    collectActivity.videoFragment.edit(collectActivity.isVideoEdit, this);
                    if (collectActivity.isVideoEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(collectActivity.isVideoEdit);
                    break;
                case 1:
                    if (collectActivity.newsFragment.adapter != null && collectActivity.newsFragment.adapter.getData().size() > 0) {
                        collectActivity.isNewsEdit = !collectActivity.isNewsEdit;
                    } else {
                        collectActivity.isNewsEdit = false;
                    }
                    collectActivity.newsFragment.edit(collectActivity.isNewsEdit, this);
                    if (collectActivity.isNewsEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(collectActivity.isNewsEdit);
                    break;
                case 2:
                    if (collectActivity.flashFragment.adapter != null && collectActivity.flashFragment.adapter.getData().size() > 0) {
                        collectActivity.isFlashEdit = !collectActivity.isFlashEdit;
                    } else {
                        collectActivity.isFlashEdit = false;
                    }
                    collectActivity.flashFragment.edit(collectActivity.isFlashEdit, this);
                    if (collectActivity.isFlashEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(collectActivity.isFlashEdit);
                    break;
                case 3:
                    if (collectActivity.authorFragment.adapter != null && collectActivity.authorFragment.adapter.getData().size() > 0) {
                        collectActivity.isAuthorEdit = !collectActivity.isAuthorEdit;
                    } else {
                        collectActivity.isAuthorEdit = false;
                    }
                    collectActivity.authorFragment.edit(collectActivity.isAuthorEdit, this);
                    if (collectActivity.isAuthorEdit) {
                        collectActivity.llDel.setVisibility(View.VISIBLE);
                        collectActivity.ivBarFunction.setText("取消");
                    } else {
                        collectActivity.llDel.setVisibility(View.GONE);
                        collectActivity.ivBarFunction.setText("编辑");
                    }
                    editMode(collectActivity.isAuthorEdit);
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
                collectActivity.isVideoAllSel = !selected;
                collectActivity.videoFragment.selAll(collectActivity.isVideoAllSel, this);
                break;
            case 1:
                collectActivity.isNewsAllSel = !selected;
                collectActivity.newsFragment.selAll(collectActivity.isNewsAllSel, this);
                break;
            case 2:
                collectActivity.isFlashAllSel = !selected;
                collectActivity.flashFragment.selAll(collectActivity.isFlashAllSel, this);
                break;
            case 3:
                collectActivity.isAuthorAllSel = !selected;
                collectActivity.authorFragment.selAll(collectActivity.isAuthorAllSel, this);
                break;
        }
    }

    /**
     * 删除操作
     */
    public void deleteOpe(){
        //删除
        if (collectActivity.delNum != 0) {
            collectActivity.snackBar = TSnackbar.make(collectActivity.tvDel, "删除收藏中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
            collectActivity.snackBar.setPromptThemBackground(Prompt.SUCCESS);
            collectActivity.snackBar.addIconProgressLoading(0, true, false);
            collectActivity.snackBar.show();
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
            collectActivity.delNum = num;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delError() {
        collectActivity.snackBar.setPromptThemBackground(Prompt.ERROR).setText("删除收藏失败").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(collectActivity.getContext()), collectActivity.getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delSuccessed() {
        collectActivity.snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("删除收藏成功").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(collectActivity.getContext()),collectActivity.getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delAll(boolean isAll) {
        switch (collectActivity.vpContent.getCurrentItem()) {
            case 0:
                collectActivity.isVideoAllSel = isAll;
                break;
            case 1:
                collectActivity.isNewsAllSel = isAll;
                break;
            case 2:
                collectActivity.isFlashAllSel = isAll;
                break;
            case 3:
                collectActivity.isAuthorAllSel = isAll;
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
                collectActivity.isVideoEdit = false;
                collectActivity.isVideoAllSel = false;
//                videoFragment.quitEdit(numListener);
                break;
            case 1:
                collectActivity.isNewsEdit = false;
                collectActivity.isNewsAllSel = false;
//                newsFragment.quitEdit(numListener);
                break;
            case 2:
                collectActivity.isFlashEdit = false;
                collectActivity.isFlashAllSel = false;
//                flashFragment.quitEdit(numListener);
                break;
            case 3:
                collectActivity.isAuthorEdit = false;
                collectActivity.isAuthorAllSel = false;
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
        collectActivity.delNum = 0;
        collectActivity.tvDel.setText("删除(" + collectActivity.delNum + ")");
        collectActivity.isAuthorAllSel = false;
        collectActivity.isFlashAllSel = false;
        collectActivity.isNewsAllSel = false;
        collectActivity.isVideoAllSel = false;
        switch (position) {
            case 0:
                if (collectActivity.isVideoEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(collectActivity.isVideoAllSel);
                editMode(collectActivity.isVideoEdit);
                break;
            case 1:
                if (collectActivity.isNewsEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(collectActivity.isNewsAllSel);
                editMode(collectActivity.isNewsEdit);
                break;
            case 2:
                if (collectActivity.isFlashEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(collectActivity.isFlashAllSel);
                editMode(collectActivity.isFlashEdit);
                break;
            case 3:
                if (collectActivity.isAuthorEdit) {
                    collectActivity.llDel.setVisibility(View.VISIBLE);
                    collectActivity.ivBarFunction.setText("取消");
                } else {
                    collectActivity.llDel.setVisibility(View.GONE);
                    collectActivity.ivBarFunction.setText("编辑");
                }
                collectActivity.ivSelAll.setSelected(collectActivity.isAuthorAllSel);
                editMode(collectActivity.isAuthorEdit);
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
