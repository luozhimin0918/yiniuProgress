package com.jyh.kxt.trading.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.trading.presenter.PublishPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.valuesfeng.picker.utils.PicturePickerUtils;

/**
 * 发布观点
 */
public class PublishActivity extends BaseActivity {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.publish_scroll_view) ScrollView publishScrollView;
    @BindView(R.id.iv_publish_content_size) TextView ivPublishContentSize;
    @BindView(R.id.publish_content_et) EditText publishContentEt;
    @BindView(R.id.publish_picture_add) ImageView publishPictureAdd;
    @BindView(R.id.publish_pictures_layout) public LinearLayout publishPicturesLayout;

    /**
     * 发布类型  0表示自己发布 1表示转发
     */
    private int publishType = 0;

    private PublishPresenter publishPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish, StatusBarColor.THEME1);

        publishType = getIntent().getIntExtra(IntentConstant.TYPE, 0);

        publishPresenter = new PublishPresenter(this);

        ivBarFunction.setText("发布");
        tvBarTitle.setText(publishType == 0 ? "发布观点" : "转发观点");


    }

    @OnClick({R.id.iv_bar_break, R.id.iv_publish_emoji,
                     R.id.iv_publish_market, R.id.iv_publish_picture,
                     R.id.iv_publish_arrows, R.id.iv_bar_function, R.id.publish_picture_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_publish_emoji: //表情包

                break;
            case R.id.iv_publish_market: //跳转行情回调

                break;
            case R.id.iv_publish_picture: //图库
                publishPresenter.showPictureStorage();
                break;
            case R.id.iv_publish_arrows://隐藏键盘

                break;
            case R.id.iv_bar_function://发布

                break;
            case R.id.publish_picture_add://添加图片

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            LayoutInflater mInflater = LayoutInflater.from(this);

            List<Uri> pictureUris = PicturePickerUtils.obtainResult(data);


            int currentPictureCount = publishPicturesLayout.getChildCount() + pictureUris.size();
            int residueCount = 4 - currentPictureCount;
            if (residueCount == 0) {
                publishPictureAdd.setVisibility(View.GONE);
            }

            for (Uri uri : pictureUris) {

                final RelativeLayout mItemImage = (RelativeLayout) mInflater.inflate(R.layout.item_publish_pictures, publishPicturesLayout, false);
                ImageView pictureBg = (ImageView) mItemImage.findViewById(R.id.publish_picture_bg);
                ImageView pictureClose = (ImageView) mItemImage.findViewById(R.id.publish_picture_close);

                publishPicturesLayout.addView(mItemImage, 0);
                Glide.with(this).load(uri).override(200, 200).into(pictureBg);

                pictureClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        publishPictureAdd.setVisibility(View.VISIBLE);
                        publishPicturesLayout.removeView(mItemImage);
                    }
                });
            }
        }
    }
}
