package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.TextGifDrawable;
import com.jyh.kxt.base.utils.EmoJeUtil;
import com.jyh.kxt.base.utils.GlideCircleTransform;
import com.library.util.SystemUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public EmoticonTextView(Context context) {
        this(context, null);
    }

    public EmoticonTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean convertToGif(int type, CommentBean commentBean, int nickNameLength, String text, String avatarUrl) {
        final SpannableString currentSpannable = new SpannableString(text);

        try {
            if ('@' == currentSpannable.charAt(0) && type == 2) {

                if (avatarUrl == null) {
                    avatarUrl = commentBean.getParent_member_picture();
                }

                Glide.with(getContext())
                        .load(avatarUrl)
                        .asBitmap()
                        .transform(new GlideCircleTransform(getContext()))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), resource);
                                int firstImgHeight = SystemUtil.dp2px(getContext(), 20);
                                bitmapDrawable.setBounds(0, 0, firstImgHeight, firstImgHeight);

                                ImageSpan mEmoJeImageSpan = new ImageSpan(bitmapDrawable, ImageSpan.ALIGN_BASELINE);
                                currentSpannable.setSpan(
                                        mEmoJeImageSpan,
                                        0, //这里因为没有加上中括号
                                        1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                setText(currentSpannable);
                            }
                        });
            }
        } catch (Exception e) {
        }

        int color = ContextCompat.getColor(getContext(), R.color.blue);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
        currentSpannable.setSpan(redSpan, 0, nickNameLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

                    String assetName = emoJeBean.getGroupName() + "/" + emoJeName + "." + emoJeBean.getSuffixName();
                    AssetManager assets = getContext().getAssets();
                    AssetFileDescriptor assetFileDescriptor = assets.openFd(assetName);
                    TextGifDrawable gifDrawable = new TextGifDrawable(assetFileDescriptor);
                    gifDrawable.setTextView(this);

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
                    startDownloadGif(currentSpannable, matcher.start(), matcher.end(), emoJeUrl, emoJeName);
                }
            }
        } catch (Exception e) {
        }


        setText(currentSpannable);
        return isFindMatcher;
    }

    public boolean convertToGif(int type, CommentBean commentBean, int nickNameLength, String text) {
        return convertToGif(type, commentBean, nickNameLength, text, null);
    }

    /**
     * 网络上下载图片
     */
    private void startDownloadGif(final SpannableString currentSpannable,
                                  final int matcherStart,
                                  final int matcherEnd,
                                  final String emoJeUrl,
                                  final String emoJeName) {

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
                            TextGifDrawable gifDrawable = new TextGifDrawable(gifFile);
                            gifDrawable.setTextView(EmoticonTextView.this);

                            if ("default".equals(emoJeName) ||
                                    "paobing".equals(emoJeName) ||
                                    "baolixiong".equals(emoJeName)) {

                                gifDrawable.setBounds(0, 0, emoJeSize2, emoJeSize2);
                            } else {
                                gifDrawable.setBounds(0, 0, emoJeSize, emoJeSize);
                            }
                            ImageSpan mEmoJeImageSpan = new ImageSpan(gifDrawable, ImageSpan.ALIGN_BASELINE);
                            currentSpannable.setSpan(
                                    mEmoJeImageSpan,
                                    matcherStart, //这里因为没有加上中括号
                                    matcherEnd,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            setText(currentSpannable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
