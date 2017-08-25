package com.jyh.kxt.trading.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.library.util.ConvertUtils;
import com.library.util.FileUtils;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/15.
 */

public class VPImgAdapter extends PagerAdapter {

    private List<String> urls;
    private Context mContext;
    private List<View> views = new ArrayList<>();
    private View.OnClickListener onClickLinstener;

    public VPImgAdapter(List<String> urls, Context mContext) {
        this.urls = urls;
        this.mContext = mContext;
        LayoutInflater from = LayoutInflater.from(mContext);
        for (String url : urls) {
            views.add(from.inflate(R.layout.pop_img, null, false));
        }
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = views.get(position);
        final ImageView ivPop = (ImageView) view.findViewById(R.id.iv_pop);
        View downBitmapToSd = view.findViewById(R.id.iv_download);

        ivPop.setOnClickListener(onClickLinstener);

        Glide.with(mContext).load(urls.get(position))
                .asBitmap()
                .error(R.mipmap.icon_def_news)
                .placeholder(R.mipmap.icon_def_news)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivPop.setImageBitmap(resource);
                    }
                });
        downBitmapToSd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String httpUrl = urls.get(position);
                    String md5Key = ConvertUtils.md5(httpUrl);

                    File bitmapFile = FileUtils.getSDSaveFilePath(mContext, "bitmap");
                    String bitmapSavePath = bitmapFile.getPath() + File.separator + md5Key + ".jpg";

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ivPop.getDrawable();
                    Bitmap imageBitmap = bitmapDrawable.getBitmap();

                    FileOutputStream fos = new FileOutputStream(new File(bitmapSavePath));
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    ToastView.makeText3(mContext, "图片已保存至:" + bitmapSavePath);
                } catch (Exception e) {
                    e.printStackTrace();

                    TSnackbar.make(v, "图片保存失败!",
                            TSnackbar.LENGTH_SHORT,
                            TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                            .setPromptThemBackground(Prompt.WARNING)
                            .show();
                }
            }
        });

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(view, layoutParams);
        return view;
    }

    public void setOnClickLinstener(View.OnClickListener onClickLinstener) {
        this.onClickLinstener = onClickLinstener;
    }
}
