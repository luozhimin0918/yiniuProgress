package com.jyh.kxt.trading.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.util.IntentUtil;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonSimpleTextView;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.presenter.PublishPresenter;
import com.library.util.BitmapUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import io.valuesfeng.picker.utils.PicturePickerUtils;

/**
 * 发布观点
 */
public class PublishActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.publish_scroll_view) ScrollView publishScrollView;
    @BindView(R.id.iv_publish_content_size) public TextView ivPublishContentSize;
    @BindView(R.id.publish_content_et) public EmoticonsEditText publishContentEt;
    @BindView(R.id.publish_picture_add) ImageView publishPictureAdd;
    @BindView(R.id.publish_pictures_layout) public LinearLayout publishPicturesLayout;
    @BindView(R.id.publish_navigation) public RelativeLayout publishNavigation;
    @BindView(R.id.publish_emoje_content) public FrameLayout flEmoJe;
    @BindView(R.id.iv_publish_emoji) public ImageView ivEmoJeIcon;

    SoftKeyBoardListener.OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;
    //转发相关
    @BindView(R.id.publish_tran_layout) RelativeLayout publishTranLayout;
    @BindView(R.id.publish_tran_avatar) ImageView publishTranAvatar;
    @BindView(R.id.publish_tran_nickname) TextView publishTranNickname;
    @BindView(R.id.publish_tran_content) EmoticonSimpleTextView publishTranContent;

    public void setSoftKeyBoardListener(SoftKeyBoardListener.OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    /**
     * 保存本地的图片列表
     */
    private final String PICTURE_LIST = "picturePathList";
    /**
     * 保存本地的文字
     */
    private final String EDIT_CONTENT = "editContent";

    /**
     * 发布类型  0表示自己发布 1表示转发
     */
    public int publishType = 0;
    public ViewPointTradeBean transmitBean;

    private PublishPresenter publishPresenter;

    /**
     * 图片列表
     */
    private List<String> picturePathList = new ArrayList<>();
    public List<String> base64List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish, StatusBarColor.THEME1);

        publishType = getIntent().getIntExtra(IntentConstant.TYPE, 0);

        publishPresenter = new PublishPresenter(this);

        ivBarFunction.setText("发布");
        tvBarTitle.setText(publishType == 0 ? "发布观点" : "转发观点");

        if (publishType == 0) {
            //查找本地是否有草稿
            findDrafts();
        } else {
            try {
                publishTranLayout.setVisibility(View.VISIBLE);
                transmitBean = (ViewPointTradeBean) IntentUtil.getObject(IntentUtil.OBJECT);
                publishTranNickname.setText("@" + transmitBean.author_name);

                List<String> pictures = transmitBean.getPicture();
                if (pictures != null && pictures.size() != 0) {
                    Glide.with(this).load(pictures.get(0))
                            .asBitmap()
                            .error(R.drawable.umeng_socialize_delete)
                            .override(100, 100)
                            .into(publishTranAvatar);
                } else {
                    Glide.with(this).load(transmitBean.getAuthor_img())
                            .asBitmap()
                            .error(R.drawable.umeng_socialize_delete)
                            .override(100, 100)
                            .into(publishTranAvatar);
                }

                publishTranContent.convertToGif(transmitBean.getContent());
            } catch (Exception e) {
                e.printStackTrace();
                ToastView.makeText(this, "转发异常！");
            }
        }

        SoftKeyBoardListener.setListener(this, this);
    }

    @Override
    public void keyBoardShow(int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) publishNavigation.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, height);
        publishNavigation.setLayoutParams(layoutParams);
    }

    @Override
    public void keyBoardHide(int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) publishNavigation.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        publishNavigation.setLayoutParams(layoutParams);
        if (onSoftKeyBoardChangeListener != null) {
            onSoftKeyBoardChangeListener.keyBoardHide(height);
        }
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_publish_emoji,
                     R.id.iv_publish_market, R.id.iv_publish_picture,
                     R.id.iv_publish_arrows, R.id.iv_bar_function,
                     R.id.publish_picture_add})
    public void onClick(View view) {
        onClick(view.getId());
    }

    public void onClick(int viewId) {
        switch (viewId) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_publish_emoji: //表情包
                publishPresenter.showOrHideEmoJiView();
                break;
            case R.id.iv_publish_market: //跳转行情回调
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivityForResult(searchIntent, 1001);
                break;
            case R.id.iv_publish_picture: //图库
                publishPresenter.showPictureStorage();
                break;
            case R.id.iv_publish_arrows://隐藏键盘
                SystemUtil.closeSoftInputWindow(this);
                publishPresenter.hideEmoJeContent();
                break;
            case R.id.iv_bar_function://发布
                publishPresenter.postViewPoint();
                break;
            case R.id.publish_picture_add://添加图片
                publishPresenter.showPictureStorage();
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            List<Uri> pictureUris = PicturePickerUtils.obtainResult(data);
            addImageViewToLayout(pictureUris);
        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            QuoteItemJson mQuoteItem = data.getParcelableExtra("search");

            int index = publishContentEt.getSelectionStart();
            Editable editable = publishContentEt.getText();
            String editString = "$" + mQuoteItem.getCode() + " " + mQuoteItem.getName() + "$";

            SpannableString spannableString = new SpannableString(editString);
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue1)),
                    0,
                    editString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            editable.insert(index, spannableString);
        }
    }

    @Override
    public void onBackPressed() {
        if (publishType == 1) {
            super.onBackPressed();
            return;
        }
        String inputContent = publishContentEt.getText().toString();

        int childCount = publishPicturesLayout.getChildCount();
        if (!TextUtils.isEmpty(inputContent) || childCount > 1) {
            new AlertDialog.Builder(this, ThemeUtil.getAlertTheme(this))
                    .setPositiveButton("保存",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveDraught(false);
                                    PublishActivity.this.finish();
                                }
                            })
                    .setNegativeButton("不保存",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveDraught(true);
                                    PublishActivity.this.finish();
                                }
                            })
                    .setTitle("温馨提示")
                    .setMessage("是否保存草稿?")
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        publishPresenter.hideEmoJeContent();
    }

    /**
     * 来源1  则表示发布
     */
    public void saveDraught(boolean isResetNull) {
        if (isResetNull) {
            SPUtils.save(PublishActivity.this, PICTURE_LIST, "[]");
            SPUtils.save(PublishActivity.this, EDIT_CONTENT, "");
            return;
        }

        SPUtils.save(PublishActivity.this, PICTURE_LIST, picturePathList.size() == 0 ? "[]" : JSON.toJSONString(picturePathList));

        String inputValue = publishContentEt.getText().toString();
        SPUtils.save(PublishActivity.this, EDIT_CONTENT, TextUtils.isEmpty(inputValue) ? "" : inputValue);
    }

    /**
     * 查找草稿箱
     */
    public void findDrafts() {
        String uriList = SPUtils.getString(this, PICTURE_LIST);
        String inputValue = SPUtils.getString(this, EDIT_CONTENT);

        if (TextUtils.isEmpty(uriList) && TextUtils.isEmpty(inputValue)) {
            return;
        }
        List<Uri> localUriList = new ArrayList<>();
        List<String> uriPathList = JSONArray.parseArray(uriList, String.class);
        for (String itemUri : uriPathList) {
            localUriList.add(Uri.parse(itemUri));
        }
        if (localUriList.size() != 0) {
            addImageViewToLayout(localUriList);
        }

        if (!TextUtils.isEmpty(inputValue)) {
            //处理行情
            SpannableStringBuilder builder = new SpannableStringBuilder(inputValue);
            Matcher marketMatcher = Pattern.compile("\\$(.*?)\\$").matcher(inputValue);
            while (marketMatcher.find()) {
                int matcherStart = marketMatcher.start();//加上连接符号$
                int matcherEnd = marketMatcher.end();
                //标记起来是行情数据
                builder.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue1)),
                        matcherStart, matcherEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            publishContentEt.setText(builder);
            publishContentEt.setSelection(builder.length());
        }
    }

    private void addImageViewToLayout(List<Uri> pictureUris) {
        int currentPictureCount = publishPicturesLayout.getChildCount() + pictureUris.size();
        int residueCount = 4 - currentPictureCount;
        if (residueCount == 0) {
            publishPictureAdd.setVisibility(View.GONE);
        }

        LayoutInflater mInflater = LayoutInflater.from(this);
        for (Uri uri : pictureUris) {
            final RelativeLayout mItemImage = (RelativeLayout) mInflater.inflate(R.layout.item_publish_pictures, publishPicturesLayout, false);
            final ImageView pictureBg = (ImageView) mItemImage.findViewById(R.id.publish_picture_bg);
            ImageView pictureClose = (ImageView) mItemImage.findViewById(R.id.publish_picture_close);

            publishPicturesLayout.addView(mItemImage, 0);
            picturePathList.add(uri.toString());

            Glide.with(this).load(uri)
                    .asBitmap()
                    .error(R.drawable.umeng_socialize_delete)
                    .override(/*250, 250*/800,800)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            pictureBg.setImageBitmap(resource);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            String base64 = BitmapUtils.drawableToByte(baos.toByteArray());

                            base64List.add(base64);
                        }
                    });

            pictureClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int viewPosition = publishPicturesLayout.indexOfChild(mItemImage);

                    publishPictureAdd.setVisibility(View.VISIBLE);
                    publishPicturesLayout.removeView(mItemImage);

                    base64List.remove(viewPosition);
                    picturePathList.remove(viewPosition);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        base64List.clear();


    }
}
