package com.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * 项目名:Kxt
 * 类描述:图片处理类
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/20.
 */

public class BitmapUtils {
    /**
     * 图片byte转String
     *
     * @param bytes
     * @return
     */
    public static String drawableToByte(byte[] bytes) {
        if (null != bytes) {
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    /**
     * 图片String 转 bitmap
     *
     * @param bitmapStr
     * @return
     */
    public static Bitmap StringToBitmap(String bitmapStr) {
        try {
            byte[] decode = Base64.decode(bitmapStr, Base64.DEFAULT);
            return Bytes2Bimap(decode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * drawable 转 bitmap
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap drawable2Bitmap(Context context, int drawableId) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, drawableId);
    }

    /**
     * bitmap 转 drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * bitmap 转 byte[]
     *
     * @param bm
     * @param jpeg
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat jpeg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(jpeg, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte[] 转 bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 改变图片的亮度方法 0--原样 >0---调亮 <0---调暗
     *
     * @param imageView
     * @param brightness
     */
    public static void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount() / 1024 / 10;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount() / 1024 / 10;
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024 / 10;                //earlier version
    }

}
