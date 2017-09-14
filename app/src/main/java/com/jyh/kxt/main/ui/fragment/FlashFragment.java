package com.jyh.kxt.main.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.presenter.FlashPresenter;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.util.NetUtils;
import com.library.util.SPUtils;
import com.library.widget.PageLoadLayout;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述: 快讯Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FlashFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.fab_top) public ImageView fabTop;
    @BindView(R.id.iv_ad) public ImageView ivAd;

    /**
     * 快讯筛选
     */
    private boolean onlyShowHigh = false;
    private boolean flashTop = false;
    private boolean flashSound = false;

    private View funView;
    private ImageView imgFiltrate;

    public FlashPresenter flashPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);
        flashPresenter = new FlashPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(flashPresenter);
        plRootView.setOnAfreshLoadListener(flashPresenter);
        lvContent.setOnRefreshListener(flashPresenter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) return;
                    List source = flashPresenter.adapter.getSource();
                    Object obj = source.get(position - 1);
                    if (obj != null && obj instanceof FlashJson) {
                        FlashJson flashJson = (FlashJson) obj;
                        String type = flashJson.getCode();
                        String content = flashJson.getContent();

                        MainActivity mainActivity = (MainActivity) getActivity();

                        switch (type) {
                            case VarConstant.SOCKET_FLASH_KUAIXUN:
                            case VarConstant.SOCKET_FLASH_CJRL:
                                JumpUtils.jump(mainActivity, VarConstant.OCLASS_FLASH, VarConstant.OACTION_DETAIL,
                                        flashJson.getUid(), null);
                                break;
                            case VarConstant.SOCKET_FLASH_KXTNEWS:
                                Flash_NEWS flash_news = JSON.parseObject(content, Flash_NEWS.class);
                                Flash_NEWS.Jump url = flash_news.getUrl();
                                JumpUtils.jump(mainActivity, url.getC(), VarConstant.OACTION_DETAIL, url.getI(), url.getU
                                        ());
                                break;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        lvContent.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > visibleItemCount) {
                    fabTop.setVisibility(View.VISIBLE);
                } else {
                    fabTop.setVisibility(View.GONE);
                }
            }
        });

        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvContent.getRefreshableView().setSelection(0);
            }
        });
        flashPresenter.init();
        flashPresenter.setAd();
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_FLASH_FILTRATE:
                flashPresenter.filtrate();
                break;
            case EventBusClass.EVENT_COLLECT_FLASH:
                FlashJson flash = (FlashJson) eventBus.intentObj;
                FastInfoAdapter adapter = flashPresenter.adapter;
                List<FlashJson> data = adapter.getData();
                for (FlashJson flashJson : data) {
                    if (flash.getSocre().equals(flashJson.getSocre())) {
                        flashJson.setColloct(!flashJson.isColloct());
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        flashPresenter.onDestroy();
        super.onDestroyView();
        try {
            getQueue().cancelAll(flashPresenter.getClass().getName());
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换的时候使用
     *
     * @param flActionBarFun
     */
    private Context mFragmentContext;
    private MainInitJson mainInitJson;

    public void onTabSelect(final FrameLayout flActionBarFun) {
        try {
            flActionBarFun.removeAllViews();
            mFragmentContext = flActionBarFun.getContext();

            if (funView == null) {
                String loadInit = SPUtils.getString(mFragmentContext, SpConstant.INIT_LOAD_APP_CONFIG);
                mainInitJson = JSON.parseObject(loadInit, MainInitJson.class);
                String advertUrl = mainInitJson.getIndex_ad().getIcon();

                funView = LayoutInflater.from(mFragmentContext).inflate(R.layout.action_bar_flash, flActionBarFun, false);

                ImageView imgAdvert = (ImageView) funView.findViewById(R.id.iv_right_icon);
                imgFiltrate = (ImageView) funView.findViewById(R.id.iv_right_icon1);

                //默认筛选位置
                imgFiltrate.setImageDrawable(ContextCompat.getDrawable(mFragmentContext, R.mipmap.icon_rili_sx));

                Glide.with(mFragmentContext).load(advertUrl).into(new GlideDrawableImageViewTarget(imgAdvert));

                //点击事件
                imgFiltrate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flashFiltrate();
                    }
                });

                //点击事件
                imgAdvert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.mainPresenter.showPopAdvertisement(mainInitJson.getIndex_ad());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            flActionBarFun.addView(funView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void flashFiltrate() {
        FastInfoAdapter adapter = flashPresenter.adapter;
        if (adapter == null || adapter.isAdapterNullData()) {
            ToastView.makeText3(getContext(), "暂无可筛选数据");
            return;
        }
        final PopupUtil filtratePopup = new PopupUtil(getActivity());
        View view = filtratePopup.createPopupView(R.layout.pop_flash_filtrate);

        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
        final OptionLayout olContent = (OptionLayout) view.findViewById(R.id.ol_content);

        olContent.setMinSelectCount(1);
        olContent.setMaxSelectCount(3);
        olContent.setSelectMode(OptionLayout.SelectMode.CheckMode);

        final SwitchCompat scHigh = (SwitchCompat) view.findViewById(R.id.sc_high);
        final SwitchCompat scTop = (SwitchCompat) view.findViewById(R.id.sc_top);
        final SwitchCompat scSound = (SwitchCompat) view.findViewById(R.id.sc_sound);

        Set<String> set = SPUtils.getStringSet(getContext(), SpConstant.FLASH_FILTRATE);
        scHigh.setChecked(onlyShowHigh = SPUtils.getBoolean(getContext(), SpConstant.FLASH_FILTRATE_HIGH));
        scTop.setChecked(flashTop = SPUtils.getBooleanTrue(getContext(), SpConstant.FLASH_FILTRATE_TOP));
        scSound.setChecked(flashSound = SPUtils.getBooleanTrue(getContext(), SpConstant.FLASH_FILTRATE_SOUND));
        if (set.size() == 0 || set.size() == 3) {
            set.clear();
            set.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array.flash_silver)));
            olContent.setSelectItemIndex(set);
        } else {
            olContent.setSelectItemIndex(set);
        }

        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;
        filtratePopup.setConfig(config);

        filtratePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        filtratePopup.setOnDismissListener(new PopupUtil.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> set = new HashSet<>();
                set.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array.flash_silver)));
                olContent.setSelectItemIndex(set);
                scHigh.setChecked(false);
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE, olContent.getSelectedMap());
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_HIGH, onlyShowHigh);
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_TOP, flashTop);
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_SOUND, flashSound);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_FLASH_FILTRATE, null));

                flashPresenter.filtrate();

                filtratePopup.dismiss();
            }
        });


        scHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlyShowHigh = isChecked;
            }
        });
        scTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flashTop = isChecked;
            }
        });
        scSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flashSound = isChecked;
            }
        });

    }

    @Override
    public void OnAfreshLoad() {
        flashPresenter.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flashPresenter.adapter != null) {
            flashPresenter.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        try {
            if (flashPresenter.adapter != null) {
                flashPresenter.adapter.notifyDataSetChanged();
            }
            lvContent.getRefreshableView().invalidatePinnedView();

            if (imgFiltrate != null) {
                imgFiltrate.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_rili_sx));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int oldNetStatus;

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        switch (netMobile) {
            case NetUtils.STATE_CONNECT_NONE:
                break;
            case NetUtils.STATE_CONNECT_WIFI:
            case NetUtils.STATE_CONNECT_MOBILE:
                if (oldNetStatus == NetUtils.STATE_CONNECT_NONE) {
                    //断开重连
                    flashPresenter.reConnection();
                }
                break;
        }
        oldNetStatus = netMobile;
    }
}
