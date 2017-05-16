package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.utils.EmoJeUtil;
import com.library.util.SystemUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mr'Dai on 2017/5/10.
 */

public class EmoticonTextView extends TextView {

    private int emoJeSize = SystemUtil.dp2px(getContext(), 60);
    private int emoJeSize2 = SystemUtil.dp2px(getContext(), 25);

    /**
     * 是否开启循环
     */
    private volatile boolean isLoopInvalidate = false;
    /**
     * 刷新时间
     */
    private int postInvalidateDelayed = 100;

    public EmoticonTextView(Context context) {
        this(context, null);
    }

    public EmoticonTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean convertToGif(String text) {

        SpannableString currentSpannable = new SpannableString(text);

        boolean isFindMatcher = false;
        Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(text);
        try {
            while (matcher.find()) {
                isFindMatcher = true;

                String group = matcher.group(1);

                String[] splitGroup = group.split(",");
                if (splitGroup.length < 2) {
                    continue;
                }
                String emoJeName = splitGroup[0];
                String emoJeUrl = splitGroup[1];


                EmojeBean emoJeBean = EmoJeUtil.getInstance().getEmoJeBean(emoJeName);
                if (emoJeBean != null) { //是否本地存在这个EmoJe表情
                    /**
                     * 配置EmoJe基本信息
                     * 设置EmoJe的来源,本地或者是网络
                     */
                    GifDrawable gifDrawable = new GifDrawable(
                            getContext().getAssets(),
                            emoJeBean.getGroupName() + "/" + emoJeName + "." + emoJeBean.getSuffixName());

                    if ("default".equals(emoJeBean.getGroupName()) ||
                            "paobing".equals(emoJeBean.getGroupName()) ||
                            "baolixiong".equals(emoJeBean.getGroupName())) {

                        gifDrawable.setBounds(0, 0, emoJeSize2, emoJeSize2);
                    } else {
                        gifDrawable.setBounds(0, 0, emoJeSize, emoJeSize);
                    }

                    ImageSpan mEmoJeImageSpan = new ImageSpan(gifDrawable, ImageSpan.ALIGN_BASELINE);
                    currentSpannable.setSpan(
                            mEmoJeImageSpan,
                            matcher.start(), //这里因为没有加上中括号
                            matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    isFindMatcher = false;
                    startDownloadGif(currentSpannable, matcher.start(), matcher.end(), emoJeUrl);
                }
            }
        } catch (Exception e) {
        }
        setText(currentSpannable);


        if (isFindMatcher) {
            isLoopInvalidate = true;
            startLoopInvalidate();
        } else {
            isLoopInvalidate = false;
            stopLoopInvalidate();
        }
        return isFindMatcher;
    }

    private void startDownloadGif(final SpannableString currentSpannable,
                                  final int matcherStart,
                                  final int matcherEnd,
                                  final String emoJeUrl) {

        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    FutureTarget<File> fileFutureTarget;
                    fileFutureTarget = Glide.with(getContext()).load(emoJeUrl).downloadOnly(240, 240);
                    File myFile = fileFutureTarget.get();

                    subscriber.onNext(myFile);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File gifFile) {
                        try {
                            GifDrawable gifDrawable = new GifDrawable(gifFile);
                            gifDrawable.setBounds(0, 0, gifDrawable.getIntrinsicWidth(), gifDrawable
                                    .getIntrinsicHeight());

                            ImageSpan mEmoJeImageSpan = new ImageSpan(gifDrawable, ImageSpan.ALIGN_BASELINE);
                            currentSpannable.setSpan(
                                    mEmoJeImageSpan,
                                    matcherStart,
                                    matcherEnd,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                            setText(currentSpannable);

                            isLoopInvalidate = true;
                            startLoopInvalidate();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    public void startLoopInvalidate() {
        if (!isLoopInvalidate) {
            return;
        }

        isLoopInvalidate = true;
        postDelayed(actionTextRunnable, postInvalidateDelayed);

    }

    public void stopLoopInvalidate() {
        isLoopInvalidate = false;

        removeCallbacks(actionTextRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopLoopInvalidate();
    }

    Runnable actionTextRunnable = new Runnable() {
        @Override
        public void run() {

            CharSequence text = getText();
            setText(text);

//            invalidate(); //避免刷新导致的EmoJe白屏没问题

            startLoopInvalidate();
        }
    };
}
