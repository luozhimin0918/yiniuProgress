package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.TextGifDrawable;
import com.jyh.kxt.base.utils.EmoJeUtil;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

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

public class EmoticonSimpleTextView extends TextView {

    private int emoJeSize = SystemUtil.dp2px(getContext(), 60);
    private int emoJeSize2 = SystemUtil.dp2px(getContext(), 25);

    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;


    public EmoticonSimpleTextView(Context context) {
        this(context, null);
    }

    public EmoticonSimpleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonSimpleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(LocalLinkMovementMethod.getInstance());
    }

    public boolean convertToGif(String text) {

        SpannableStringBuilder currentSpannable = new SpannableStringBuilder(text);
        Matcher marketMatcher = Pattern.compile("#&<(.*?)>&#").matcher(text);

        while (marketMatcher.find()) {
            final String marketName = marketMatcher.group(1);

            currentSpannable.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.BLUE);
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {
                    ToastView.makeText(getContext(), marketName);
                }

            }, marketMatcher.start(), marketMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            currentSpannable.replace(marketMatcher.start(), marketMatcher.end(), marketName);
        }

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

    /**
     * 网络上下载图片
     */
    private void startDownloadGif(final SpannableStringBuilder currentSpannable,
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
                            gifDrawable.setTextView(EmoticonSimpleTextView.this);

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);

        if (dontConsumeNonUrlClicks) {
            return linkHit;
        }
        return res;

    }

    public static class LocalLinkMovementMethod extends LinkMovementMethod {
        static LocalLinkMovementMethod sInstance;


        public static LocalLinkMovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new LocalLinkMovementMethod();
            }

            return sInstance;
        }

        @Override
        public boolean onTouchEvent(TextView widget,
                                    Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(
                        off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    if (widget instanceof EmoticonSimpleTextView) {
                        ((EmoticonSimpleTextView) widget).linkHit = true;
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public boolean performLongClick() {
        return false;
    }
}
