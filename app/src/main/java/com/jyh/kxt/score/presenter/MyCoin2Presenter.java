package com.jyh.kxt.score.presenter;

import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.SimplePopupWindow;
import com.jyh.kxt.score.anim.GoldPointEvaluator;
import com.jyh.kxt.score.anim.Rotate3dAnimation;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.ui.MyCoin2Activity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/9/6.
 */

public class MyCoin2Presenter extends BasePresenter {

    @BindObject private MyCoin2Activity myCoin2Activity;

    private VolleyRequest request;

    public MyCoin2Presenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    /**
     * 初始化金币接口
     */
    public void requestInitCoin(final boolean isRefresh) {
        //各种数据请求之后回调
        JSONObject param = request.getJsonParam();
        param.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());

        request.doGet(HttpConstant.CREDITS_MAIN, param, new HttpListener<MyCoinJson>() {
            @Override
            protected void onResponse(MyCoinJson myCoinJson) {

                if (myCoinJson == null) {
                    myCoin2Activity.plRootView.loadEmptyData();
                    return;
                }

                if (isRefresh) {
                    myCoin2Activity.refresh(myCoinJson);
                    myCoin2Activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myCoin2Activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    myCoin2Activity.init(myCoinJson);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (isRefresh) {
                    myCoin2Activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myCoin2Activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    myCoin2Activity.plRootView.loadError();
                }
            }
        });

    }

    /**
     * 请求签到
     *
     * @param punchCardView
     * @param signJsonList
     * @param position
     */
    private void requestPunchCard(final View punchCardView, List<SignJson> signJsonList, int position) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());
        final SignJson signJson = signJsonList.get(position);
        jsonParam.put(VarConstant.HTTP_CODE, signJson.getCode());
        request.doGet(HttpConstant.CREDITS_PUNCH_CARD, jsonParam, new HttpListener<String>() {

            @Override
            protected void onResponse(String s) {

                fuelPunchCardView(punchCardView);
                punchCardView.setOnClickListener(null);//点击事件清除
                myCoin2Activity.signed = true;
                myCoin2Activity.sign_state = 1;
                String award = signJson.getAward();
                int awardInt;
                try {
                    awardInt = award == null ? 0 : Integer.parseInt(award);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    awardInt = 0;
                }

                myCoin2Activity.punchCardSucceed(awardInt);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COIN_SIGN, new SignInfoJson(LoginUtils.getUserInfo
                        (mContext).getUid(), 1, myCoin2Activity.task_state)));
                ToastView.makeText(mContext, "签到成功!");
                playSound();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastView.makeText(mContext, "签到失败!");
            }
        });
    }

    /**
     * 初始化签到打卡记录
     *
     * @param punchCardJson
     */
    public void initPunchCard(PunchCardJson punchCardJson) {
        final int punchCardDays = punchCardJson.getDays();

        final List<SignJson> signJsonList = punchCardJson.getRules();

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);
        int widthScreen = screenDisplay.widthPixels;

        int itemMargin = SystemUtil.dp2px(mContext, 5);
        int tablePadding = SystemUtil.dp2px(mContext, 10);
        int itemWidth = (widthScreen - (itemMargin * 8) - (tablePadding * 2)) / 4;

        LayoutInflater factory = LayoutInflater.from(mContext);

        for (int position = 0; position < signJsonList.size(); position++) {
            SignJson signJson = signJsonList.get(position);

            final View punchCardView = factory.inflate(R.layout.item_punch_card, myCoin2Activity.flPunchCardTab, false);

            TextView tvSignScore = (TextView) punchCardView.findViewById(R.id.tv_sign1_score);
            TextView tvSignDay = (TextView) punchCardView.findViewById(R.id.tv_sign1_day);

            tvSignDay.setText(signJson.getDescription());
            tvSignScore.setText("+" + signJson.getAward());

            ViewGroup.LayoutParams layoutParams = punchCardView.getLayoutParams();
            if (position == 6) {
                layoutParams.width = itemWidth * 2 + tablePadding;
            } else {
                layoutParams.width = itemWidth;
            }
            punchCardView.setLayoutParams(layoutParams);
            myCoin2Activity.flPunchCardTab.addView(punchCardView, layoutParams);

            if (position < punchCardDays) {
                fuelPunchCardView(punchCardView);
            }

            final int finalPosition = position;
            punchCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    myCoin2Activity.drawerSignContent.animateClose();
                    playAnimation(v);
                    playSound();

                    if (!myCoin2Activity.signed) {
                        if (finalPosition == punchCardDays) {
                            punchCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPunchCard(punchCardView, signJsonList, finalPosition);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * 处理打卡记录
     */
    private void fuelPunchCardView(View punchCardView) {
        TextView tvSignScore = (TextView) punchCardView.findViewById(R.id.tv_sign1_score);
        TextView tvSignDay = (TextView) punchCardView.findViewById(R.id.tv_sign1_day);
        ImageView ivSignGou = (ImageView) punchCardView.findViewById(R.id.iv_sign1_gou);

        tvSignDay.setSelected(true);
        tvSignScore.setSelected(true);
        punchCardView.setSelected(true);
        ivSignGou.setVisibility(View.VISIBLE);
    }

    /**
     * 新消息声音
     */
    private SoundPool mSoundPool;

    private void playSound() {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        }
        mSoundPool.load(mContext, R.raw.money, 1);
        mSoundPool.play(1, 1, 1, 0, 0, 1);
    }

    private SimplePopupWindow simplePopupWindow;
    private boolean isAnimationStart = false;

    private void playAnimation(final View clickView) {

        simplePopupWindow = new SimplePopupWindow(myCoin2Activity, 1.0f, Gravity.CENTER, 0, WindowManager.LayoutParams.MATCH_PARENT);
        simplePopupWindow.setSimplePopupListener(
                new SimplePopupWindow.SimplePopupListener() {

                    private ImageView loadMoneyEncircle;
                    private ImageView loadMoneyInherent;
                    private TextView loadMoneyAdd;
                    private TextView loadMoneyAddTip;
                    private RelativeLayout loadMoneyLayout;

                    private View popupView;

                    @Override
                    public void onCreateView(final View popupView) {

                        this.popupView = popupView;

                        loadMoneyEncircle = ButterKnife.findById(popupView, R.id.load_money_encircle);
                        loadMoneyInherent = ButterKnife.findById(popupView, R.id.load_money_inherent);
                        loadMoneyAdd = ButterKnife.findById(popupView, R.id.load_money_add);
                        loadMoneyAddTip = ButterKnife.findById(popupView, R.id.load_money_add_tip);
                        loadMoneyLayout = ButterKnife.findById(popupView, R.id.load_money_layout);


                        popupView.addOnLayoutChangeListener(
                                new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v,
                                                               int left,
                                                               int top,
                                                               int right,
                                                               int bottom,
                                                               int oldLeft,
                                                               int oldTop,
                                                               int oldRight,
                                                               int oldBottom) {

                                        if (isAnimationStart) {
                                            return;
                                        }
                                        isAnimationStart = true;

                                        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);


                                        //移动 ->  平移 -> 缩放  -> 透明  动画
                                        Point startPoint = new Point();
                                        startPoint.x = (int) (clickView.getX() + clickView.getWidth() / 2);
                                        startPoint.y = (int) (clickView.getY() + clickView.getHeight() / 2);

                                        // 能否直接得到popupView 的位置
                                        Point endPoint = new Point();
                                        endPoint.x = screenDisplay.widthPixels / 2 - loadMoneyLayout.getWidth() / 2;
                                        endPoint.y = screenDisplay.heightPixels / 2 - loadMoneyLayout.getHeight() / 2;

                                        ValueAnimator anim = ValueAnimator.ofObject(new GoldPointEvaluator(), startPoint, endPoint);
                                        anim.setDuration(2 * 1000);

                                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                onUpdateDraw(animation);
                                            }
                                        });
                                        anim.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                playCircleAnimation();
                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                if (rotate3dAnimation != null) {
                                                    rotate3dAnimation.setRepeatCount(1);
                                                }
                                                if (mRotateAnimation != null) {
                                                    mRotateAnimation.setRepeatCount(2);
                                                    mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                        @Override
                                                        public void onAnimationStart(Animation animation) {

                                                        }

                                                        @Override
                                                        public void onAnimationEnd(Animation animation) {
                                                            loadMoneyEncircle.clearAnimation();
                                                            loadMoneyEncircle.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onAnimationRepeat(Animation animation) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                        anim.start();
                                    }
                                }

                        );
                    }

                    private void onUpdateDraw(ValueAnimator animation) {
                        float animatedFraction = animation.getAnimatedFraction();
                        if (animatedFraction < 0 || animatedFraction > 1) {
                            return;
                        }

                        Point currentPoint = (Point) animation.getAnimatedValue();

                        //animatedFraction来控制    缩放，透明，旋转

                        ViewHelper.setTranslationX(loadMoneyLayout, currentPoint.x);
                        ViewHelper.setTranslationY(loadMoneyLayout, currentPoint.y);

                        //缩放
                        ViewHelper.setScaleX(loadMoneyLayout, animatedFraction);
                        ViewHelper.setScaleY(loadMoneyLayout, animatedFraction);

                        float alphaFraction = animatedFraction + 0.2f;
                        if (alphaFraction <= 1) {
                            ViewHelper.setAlpha(loadMoneyLayout, alphaFraction);
                        }
                    }

                    private Rotate3dAnimation rotate3dAnimation;
                    private RotateAnimation mRotateAnimation;

                    private void playCircleAnimation() {
                        //开始3D旋转
                        rotate3dAnimation = new Rotate3dAnimation(
                                0,
                                360,
                                loadMoneyInherent.getWidth() / 2,
                                0,
                                0,
                                false);
                        rotate3dAnimation.setDuration(3000);
                        rotate3dAnimation.setRepeatCount(-1);
                        loadMoneyInherent.setAnimation(rotate3dAnimation);
                        rotate3dAnimation.start();

                        //平面旋转
                        mRotateAnimation = new RotateAnimation(0f,
                                360f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f);

                        LinearInterpolator linearInterpolator = new LinearInterpolator();
                        mRotateAnimation.setInterpolator(linearInterpolator);
                        mRotateAnimation.setDuration(1000);
                        //设置动画持续时间
                        mRotateAnimation.setRepeatCount(-1);
                        //设置重复次数
                        mRotateAnimation.setFillAfter(true);
                        //动画执行完后是否停留在执行完的状态
                        mRotateAnimation.setStartOffset(10);
                        //执行前的等待时间
                        loadMoneyEncircle.setAnimation(mRotateAnimation);
                        mRotateAnimation.start();
                    }


                    @Override
                    public void onDismiss() {

                    }
                }

        );

        simplePopupWindow.show(R.layout.view_load_money);
    }
}
