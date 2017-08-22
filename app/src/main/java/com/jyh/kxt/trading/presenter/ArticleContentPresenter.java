package com.jyh.kxt.trading.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.trading.adapter.VPImgAdapter;
import com.jyh.kxt.trading.json.ShareDictBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.PublishActivity;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpCallBack;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.widget.flowlayout.OptionFlowLayout;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/4.
 */

public class ArticleContentPresenter {
    public Context mContext;

    public ArticleContentPresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 作者头像
     */
    public void setAuthorImage(final RoundImageView rivUserAvatar, String authorImageUrl) {
        Glide.with(mContext)
                .load(authorImageUrl)
                .asBitmap()
                .override(80, 80).into(
                new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        rivUserAvatar.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 赞的处理
     */
    public void initTradeHandler(TextView tvZanView, boolean isFavour) {
        if (isFavour) {
            Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan2);
            tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        } else {
            Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_zan1);
            tvZanView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        }
    }

    /**
     * 转发信息的处理
     */
    public void initTransmitView(ViewGroup rlTransmitLayout,
                                 EmoticonSimpleTextView tvTransmitView,
                                 final ViewPointTradeBean forwardContent) {
        if (forwardContent != null) {
            rlTransmitLayout.setVisibility(View.VISIBLE);

            String authorInfo = "@ " + forwardContent.author_name + " ";
            StringBuffer contentBuffer = new StringBuffer(forwardContent.content);
            contentBuffer.insert(0, authorInfo);

            if (forwardContent.picture != null && forwardContent.picture.size() == 1) {
                contentBuffer.insert(contentBuffer.length(), "[图片]");
            } else {
                contentBuffer.insert(contentBuffer.length(), "[图片][图片]...");
            }

            SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(contentBuffer);
            spannableBuilder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.gray_btn_bg_color)),
                    0,
                    authorInfo.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvTransmitView.convertToGif(spannableBuilder);


            rlTransmitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewPointDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, forwardContent.o_id);
                    mContext.startActivity(intent);
                }
            });
        } else {
            rlTransmitLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化GridView 控件
     */
    public void initGridView(GridView mGridView) {
        mGridView.setMotionEventSplittingEnabled(false);
        mGridView.setNumColumns(3);
        mGridView.setBackgroundColor(Color.TRANSPARENT);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setCacheColorHint(0);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setGravity(Gravity.CENTER);
        mGridView.setVerticalScrollBarEnabled(false);
    }

    /**
     * 收藏
     *
     * @param tvSc
     * @param isCollect
     */
    public void setCollectState(TextView tvSc, boolean isCollect) {
//        Drawable drawableTop;
        if (isCollect) {
//            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_sc3);
            tvSc.setText("已收藏");
        } else {
//            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_sc);
            tvSc.setText("收藏");
        }
//        tvSc.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
    }

    public void setAttentionState(TextView tvGz, boolean isAttention) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
            return;
        }

//        Drawable drawableTop;
        if (isAttention) {
//            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_gz3);
            tvGz.setTag("true");
            tvGz.setText("已关注");
        } else {
//            drawableTop = ContextCompat.getDrawable(mContext, R.mipmap.icon_point_gz);
            tvGz.setTag("false");
            tvGz.setText("关注");
        }
//        tvGz.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
    }

    /**
     * 读取状态
     */
    public void requestGetGzState(final TextView tvGz, String authorId) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            IBaseView iBaseView = (IBaseView) mContext;
            VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("writer_id", authorId);
            mainParam.put("uid", userInfo.getUid());

            mVolleyRequest.doGet(HttpConstant.VIEW_POINT_IS_FOLLOW, mainParam, new HttpListener<JSONObject>() {
                @Override
                protected void onResponse(JSONObject jsonObject) {
                    boolean isFollow = jsonObject.getBoolean("is_follow");
                    setAttentionState(tvGz, isFollow);
                }
            });
        }
    }

    /**
     * 请求关注状态
     */
    public void requestAttentionState(String authorId, final boolean bool, final HttpCallBack httpCallBack) {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo != null) {
            IBaseView iBaseView = (IBaseView) mContext;
            VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
            JSONObject mainParam = mVolleyRequest.getJsonParam();
            mainParam.put("id", authorId);
            mainParam.put("uid", userInfo.getUid());
            mainParam.put("accessToken", userInfo.getToken());
            mainParam.put("type", "point");

            String attentionUrl;
            if (bool) {
                attentionUrl = HttpConstant.EXPLORE_BLOG_ADDFAVOR;
            } else {
                attentionUrl = HttpConstant.EXPLORE_BLOG_DELETEFAVOR;
            }

            mVolleyRequest.doGet(attentionUrl, mainParam, new HttpListener<String>() {
                @Override
                protected void onResponse(String jsonObject) {
                    ToastView.makeText3(mContext, bool ? "关注成功" : "取消成功");
                    if (httpCallBack != null) {
                        httpCallBack.onResponse(HttpCallBack.Status.SUCCESS);
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    ToastView.makeText3(mContext, bool ? "关注失败" : "取消失败");
                    if (httpCallBack != null) {
                        httpCallBack.onResponse(HttpCallBack.Status.ERROR);
                    }
                }
            });
        }
    }

    /**
     * GridView adapter
     */
    public void setPictureAdapter(GridView mGridViewPicture, List<String> gridList) {
        if (gridList == null) {
            gridList = new ArrayList<>();
        }

        final List<String> finalGridList = gridList;
        BaseListAdapter<String> pictureAdapter = new BaseListAdapter<String>(finalGridList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder mViewHolder;

                if (convertView == null) {
                    convertView = LayoutInflater
                            .from(mContext)
                            .inflate(R.layout.view_viewpoint_item_picture, parent, false);

                    mViewHolder = new ViewHolder();
                    convertView.setTag(mViewHolder);
                    mViewHolder.mPictureView = (ImageView) convertView.findViewById(R.id.item_picture);
                } else {
                    mViewHolder = (ViewHolder) convertView.getTag();
                }

                String imageUrl = finalGridList.get(position);

                Glide.with(mContext)
                        .load(imageUrl)
                        .asBitmap()
                        .placeholder(R.mipmap.icon_def_news)
                        .override(100, 80)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                mViewHolder.mPictureView.setImageBitmap(resource);
                            }
                        });
                return convertView;
            }

            class ViewHolder {
                ImageView mPictureView;
            }
        };
        mGridViewPicture.setAdapter(pictureAdapter);
        mGridViewPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String girdImageUrl = finalGridList.get(position);
                setGridViewItemClick(girdImageUrl, finalGridList, position);
            }
        });
    }

    /**
     * 点击GridView 弹出PopWindow
     */
    private PopupUtil imagePopupUtil;

    private void setGridViewItemClick(String girdImageUrl, List<String> gridList, int position) {

        imagePopupUtil = new PopupUtil((Activity) mContext);
        ViewPager inflate = (ViewPager) imagePopupUtil.createPopupView(R.layout.pop_viewpager);

        VPImgAdapter adapter = new VPImgAdapter(gridList, mContext);

        adapter.setOnClickLinstener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePopupUtil != null && imagePopupUtil.isShowing()) {
                    imagePopupUtil.dismiss();
                }
            }
        });
        inflate.setAdapter(adapter);
//        View inflate = imagePopupUtil.createPopupView(R.layout.pop_img);
//        final ImageView ivPop = (ImageView) inflate.findViewById(R.id.iv_pop);
        PopupUtil.Config config = new PopupUtil.Config();
        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;
        config.animationStyle = R.style.PopupWindow_Style1;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.MATCH_PARENT;
        imagePopupUtil.setConfig(config);
//
//        Glide.with(mContext).load(girdImageUrl)
//                .asBitmap()
//                .error(R.mipmap.icon_def_news)
//                .placeholder(R.mipmap.icon_def_news)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        ViewGroup.LayoutParams layoutParams = ivPop.getLayoutParams();
//                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                        ivPop.setLayoutParams(layoutParams);
//                        ivPop.setImageBitmap(resource);
//
        imagePopupUtil.showAtLocation(inflate, Gravity.CENTER, 0, 0);

        inflate.setCurrentItem(position);
//                    }
//                });
    }

    /**
     * 分享到平台上
     *
     * @param popupView
     */
    public void shareToPlatform(View popupView, final ShareDictBean shareDict) {

        View pyq = popupView.findViewById(R.id.iv_pyq);
        View weixin = popupView.findViewById(R.id.iv_wxhy);
        View sina = popupView.findViewById(R.id.iv_xl);
        View qq = popupView.findViewById(R.id.iv_qq);
        View zone = popupView.findViewById(R.id.iv_qq_kj);


        pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareAPI umShareAPI = UMShareAPI.get(mContext);
                if (umShareAPI.isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                    UmengShareTool.setShareContent((Activity) mContext,
                            shareDict.title,
                            shareDict.url,
                            shareDict.descript,
                            shareDict.img,
                            SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    ToastView.makeText3(mContext, "未安装微信");
                }
            }
        });
        weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareAPI umShareAPI = UMShareAPI.get(mContext);
                if (umShareAPI.isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN)) {
                    UmengShareTool.setShareContent((Activity) mContext,
                            shareDict.title,
                            shareDict.url,
                            shareDict.descript,
                            shareDict.img,
                            SHARE_MEDIA.WEIXIN);
                } else {
                    ToastView.makeText3(mContext, "未安装微信");
                }
            }
        });
        sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengShareTool.setShareContent((Activity) mContext,
                        shareDict.title,
                        shareDict.url,
                        shareDict.descript_sina,
                        shareDict.img,
                        SHARE_MEDIA.SINA);
            }
        });
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareAPI umShareAPI = UMShareAPI.get(mContext);
                if (umShareAPI.isInstall((Activity) mContext, SHARE_MEDIA.QQ)) {
                    UmengShareTool.setShareContent((Activity) mContext,
                            shareDict.title,
                            shareDict.url,
                            shareDict.descript,
                            shareDict.img,
                            SHARE_MEDIA.QQ);
                } else {
                    ToastView.makeText3(mContext, "未安装QQ");
                }
            }
        });
        zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareAPI umShareAPI = UMShareAPI.get(mContext);
                if (umShareAPI.isInstall((Activity) mContext, SHARE_MEDIA.QZONE)) {
                    UmengShareTool.setShareContent((Activity) mContext,
                            shareDict.title,
                            shareDict.url,
                            shareDict.descript,
                            shareDict.img,
                            SHARE_MEDIA.QQ);
                } else {
                    ToastView.makeText3(mContext, "未安装QQ");
                }
            }
        });
    }

    /**
     * 显示举报的Window
     *
     * @param viewPointTradeBean
     */
    SimplePopupWindow functionPopupWindow;

    public void showReportWindow(final String oid, final List<String> reportList) {
        final UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
            return;
        }
        if (functionPopupWindow != null) {
            functionPopupWindow.dismiss();
        }
        functionPopupWindow = new SimplePopupWindow((Activity) mContext);
        functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {

            private OptionFlowLayout mTagFlowLayout;
            private DiscolorButton mDiscolorButton;
            private ImageView mDiscolorCloseButton;

            @Override
            public void onCreateView(View popupView) {
                mTagFlowLayout = (OptionFlowLayout) popupView.findViewById(R.id.report_content);
                mDiscolorButton = (DiscolorButton) popupView.findViewById(R.id.report_btn);
                mDiscolorCloseButton = (ImageView) popupView.findViewById(R.id.report_btn_close);

                mTagFlowLayout.addOptionView(reportList, R.layout.item_point_jb_tv);
                mTagFlowLayout.setDefaultOption(0);
                mTagFlowLayout.setMinOrMaxCheckCount(1, 1);

                mDiscolorCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        functionPopupWindow.dismiss();
                    }
                });

                mDiscolorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> checkBoxText = mTagFlowLayout.getCheckBoxText();
                        if (checkBoxText.size() == 0) {
                            ToastView.makeText3(mContext, "投诉内容不能为空");
                            return;
                        }

                        String reportContent = checkBoxText.get(0);

                        //读取关注状态
                        IBaseView iBaseView = (IBaseView) mContext;
                        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
                        mVolleyRequest.setTag(getClass().getName());

                        JSONObject mainParam = mVolleyRequest.getJsonParam();
                        mainParam.put("id", oid);
                        mainParam.put("uid", userInfo.getUid());
                        mainParam.put("type", reportContent);
                        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_REPORT, mainParam, new HttpListener<String>() {
                            @Override
                            protected void onResponse(String s) {
                                ToastView.makeText3(mContext, "您的举报我们已经受理");
                                functionPopupWindow.dismiss();
                            }
                        });

                    }
                });
            }

            @Override
            public void onDismiss() {

            }
        });
        functionPopupWindow.show(R.layout.pop_point_report);
    }

    public void showFunctionWindow(final ViewPointTradeBean viewPointTradeBean) {
        if (functionPopupWindow != null) {
            functionPopupWindow.dismiss();
        }
        functionPopupWindow = new SimplePopupWindow((Activity) mContext);
        functionPopupWindow.setSimplePopupListener(new SimplePopupWindow.SimplePopupListener() {

            TextView tvSc;
            TextView tvGz;
            TextView tvTop;
            TextView tvShare;
            TextView tvDel;

            View.OnClickListener functionListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.point_function_sc:
                            viewPointTradeBean.isCollect = !viewPointTradeBean.isCollect;
                            boolean bool = TradeHandlerUtil.getInstance().saveState(mContext, viewPointTradeBean, 2, viewPointTradeBean
                                    .isCollect);
                            if (bool) {
                                setCollectState(tvSc, viewPointTradeBean.isCollect);
                            }

                            TradeHandlerUtil.EventHandlerBean scBean = new TradeHandlerUtil.EventHandlerBean(viewPointTradeBean.o_id);
                            scBean.collectState = viewPointTradeBean.isCollect ? 1 : 0;
                            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_COLLECT_CANCEL1, scBean));
                            break;
                        case R.id.point_function_gz:
                            final boolean isGz = !"true".equals(tvGz.getTag());
                            setAttentionState(tvGz, isGz);
                            requestAttentionState(viewPointTradeBean.author_id, isGz, new HttpCallBack() {
                                @Override
                                public void onResponse(Status status) {
                                    if (status == Status.ERROR) {
                                        setAttentionState(tvGz, !isGz);
                                    }
                                }
                            });
                            break;
                        case R.id.point_function_jb:
                            showReportWindow(viewPointTradeBean.o_id, viewPointTradeBean.report);
                            break;
                        case R.id.point_function_qx:
                            break;
                        case R.id.point_function_top:
                            //置顶
                            setTop(viewPointTradeBean);
                            break;
                        case R.id.point_function_share:
                            //转发
                            share(viewPointTradeBean);
                            break;
                        case R.id.point_function_del:
                            //删除
                            del(viewPointTradeBean);
                            break;
                    }
                    functionPopupWindow.dismiss();
                }
            };

            @Override
            public void onCreateView(View popupView) {
                boolean collectState = TradeHandlerUtil.getInstance().getCollectState(mContext, viewPointTradeBean.o_id);
                viewPointTradeBean.isCollect = collectState;

                tvSc = (TextView) popupView.findViewById(R.id.point_function_sc);
                tvSc.setOnClickListener(functionListener);
                setCollectState(tvSc, collectState);

                tvGz = (TextView) popupView.findViewById(R.id.point_function_gz);
                tvGz.setOnClickListener(functionListener);

                tvTop = (TextView) popupView.findViewById(R.id.point_function_top);
                tvTop.setOnClickListener(functionListener);
                if ("1".equals(viewPointTradeBean.is_top))
                    tvTop.setText("取消置顶");
                else
                    tvTop.setText("置顶");

                tvDel = (TextView) popupView.findViewById(R.id.point_function_del);
                tvDel.setOnClickListener(functionListener);

                tvShare = (TextView) popupView.findViewById(R.id.point_function_share);
                tvShare.setOnClickListener(functionListener);

                TextView tvJb = (TextView) popupView.findViewById(R.id.point_function_jb);
                tvJb.setOnClickListener(functionListener);
                popupView.findViewById(R.id.point_function_qx).setOnClickListener(functionListener);

                requestGetGzState(tvGz, viewPointTradeBean.author_id);

                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                if (userInfo != null && userInfo.getWriter_id() != null && userInfo.getWriter_id().equals(viewPointTradeBean.author_id)) {
                    tvGz.setVisibility(View.GONE);
                    tvJb.setVisibility(View.GONE);
                    tvTop.setVisibility(View.VISIBLE);
                    tvDel.setVisibility(View.VISIBLE);
                } else {
                    tvGz.setVisibility(View.VISIBLE);
                    tvJb.setVisibility(View.VISIBLE);
                    tvTop.setVisibility(View.GONE);
                    tvDel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

        functionPopupWindow.show(R.layout.pop_point_function);
    }

    /**
     * 删除
     *
     * @param viewPointTradeBean
     */
    public void del(ViewPointTradeBean viewPointTradeBean) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());
        JSONObject jsonParam = mVolleyRequest.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        final String o_id = viewPointTradeBean.o_id;
        if (userInfo != null) {
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
            jsonParam.put(VarConstant.HTTP_WRITER_ID, userInfo.getWriter_id());
            jsonParam.put(VarConstant.HTTP_ID, o_id);
        } else return;
//        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_DEL, jsonParam, new HttpListener<String>() {
//            @Override
//            protected void onResponse(String s) {
//                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_POINT_DEL, o_id));
//            }
//
//            @Override
//            protected void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//                ToastView.makeText3(mContext, "删除失败");
//            }
//        });
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_POINT_DEL, o_id));
    }

    /**
     * 转发
     *
     * @param viewPointTradeBean
     */
    public void share(ViewPointTradeBean viewPointTradeBean) {
        Intent intent = new Intent(mContext, PublishActivity.class);
        ((Activity) mContext).startActivityForResult(intent, 987);
    }

    /**
     * 置顶
     *
     * @param viewPointTradeBean
     */
    public void setTop(final ViewPointTradeBean viewPointTradeBean) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());
        JSONObject jsonParam = mVolleyRequest.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);

        final String o_id = viewPointTradeBean.o_id;
        if (userInfo != null) {
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
            jsonParam.put(VarConstant.HTTP_WRITER_ID, userInfo.getWriter_id());
            if ("1".equals(viewPointTradeBean.is_top)) {
            } else {
                jsonParam.put(VarConstant.HTTP_ID, o_id);
            }
        } else return;
        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_TOP, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_VIEW_POINT_TOP, o_id));
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if ("1".equals(viewPointTradeBean.is_top))
                    ToastView.makeText3(mContext, "取消置顶失败");
                else
                    ToastView.makeText3(mContext, "置顶失败");
            }
        });
    }
}
