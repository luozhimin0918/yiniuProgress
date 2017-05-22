package com.jyh.kxt.base.util;


import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.jyh.kxt.base.util.emoje.EmoticonTextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Mr'Dai on 2017/5/20.
 */

public class TextGifDrawable extends GifDrawable {
    private EmoticonTextView textView;

    public TextGifDrawable(@NonNull Resources res, @DrawableRes @RawRes int id) throws Resources.NotFoundException,
            IOException {
        super(res, id);
    }

    public TextGifDrawable(@NonNull AssetManager assets, @NonNull String assetName) throws IOException {
        super(assets, assetName);
    }

    public TextGifDrawable(@NonNull String filePath) throws IOException {
        super(filePath);
    }

    public TextGifDrawable(@NonNull File file) throws IOException {
        super(file);
    }

    public TextGifDrawable(@NonNull InputStream stream) throws IOException {
        super(stream);
    }

    public TextGifDrawable(@NonNull AssetFileDescriptor afd) throws IOException {
        super(afd);
    }

    public TextGifDrawable(@NonNull FileDescriptor fd) throws IOException {
        super(fd);
    }

    public TextGifDrawable(@NonNull byte[] bytes) throws IOException {
        super(bytes);
    }

    public TextGifDrawable(@NonNull ByteBuffer buffer) throws IOException {
        super(buffer);
    }

    public TextGifDrawable(@Nullable ContentResolver resolver, @NonNull Uri uri) throws IOException {
        super(resolver, uri);
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        textView.invalidate();
    }

    public void setTextView(EmoticonTextView textView) {
        this.textView = textView;
    }


}
