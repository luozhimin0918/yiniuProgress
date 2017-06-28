package com.jyh.kxt.base.utils.photo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.io.File;

/**
 * Created by Mr'Dai on 2017/3/24.
 */

public class PhotoTailorUtil {

    private final String AUTHORITY = "com.kxt.fileprovider";
    private final int requestCameraCode = 1;
    private final int requestSelectCode = 2;

    private Activity mActivity;
    private OnCompleteListener onCompleteListener;

    public interface OnCompleteListener {
        void onPhotoComplete(Bitmap bitmap);
    }

    //拍照所存储的Uri 和 Path
    private File cameraTempFile;
    public Uri cameraUri24;
    public Uri cameraUri22;
    public String cameraPath;

    /**
     * 初始化路径
     */
    public void initPath(Activity mActivity) {
        this.mActivity = mActivity;

        cameraTempFile = new File(mActivity.getExternalCacheDir(), "cameraTemp.jpg");
        if (cameraTempFile.exists()) {
            cameraTempFile.delete();
        }
        if (Build.VERSION.SDK_INT < 23) {//6.0以下调用的拍照方法
            cameraUri22 = Uri.fromFile(cameraTempFile);
        } else {
            cameraUri24 = FileProvider.getUriForFile(mActivity, AUTHORITY, cameraTempFile);
        }

        cameraPath = cameraTempFile.getAbsolutePath();

    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        onCompleteListener = listener;
    }

    /**
     * 启动到相机, 开始拍照
     */
    public void startToCamera() {
        try {
            Camera.open().release();
        } catch (Exception e) {

            new AlertDialog.Builder(mActivity, ThemeUtil.getAlertTheme(mActivity))
                    .setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                                    mActivity.startActivity(intent);
                                }
                            })
                    .setNegativeButton("否",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setMessage("好像没有拍照权限喔,是否去设置?").show();
            return;
        }

        if (Build.VERSION.SDK_INT < 23) {//6.0以下调用的拍照方法
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri22);
            mActivity.startActivityForResult(intent, requestCameraCode);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri24);//将拍取的照片保存到指定URI
            mActivity.startActivityForResult(intent, requestCameraCode);
        }
    }


    public void selectFromPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(intent, requestSelectCode);
    }

    private void transformToBitmap() {
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT < 23) {
                uri = cameraUri22;
            } else {
                uri = cameraUri24;
            }
            Glide.with(mActivity)
                    .load(uri)
                    .asBitmap()
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            onCompleteListener.onPhotoComplete(resource);
                        }
                    });
        } catch (Exception ex) {
            ToastView.makeText3(mActivity, "选择图片失败");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCameraCode:
                transformToBitmap();
                break;
            case requestSelectCode:
                if (data != null) {
                    if (Build.VERSION.SDK_INT < 23) {
                        cameraUri22 = data.getData();
                    } else {
                        cameraUri24 = data.getData();
                    }
                    transformToBitmap();
                }
                break;
        }

    }

    private PopupWindow wait;

    public PopupWindow upLoadPop(View mView) {
        View waitView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_wait, null);
        TextView tips = (TextView) waitView.findViewById(R.id.tv_desc);
        tips.setText("请稍等~");

        wait = new PopupWindow(waitView);

        wait.setWidth(SystemUtil.dp2px(mActivity, 80));
        wait.setHeight(SystemUtil.dp2px(mActivity, 80));

        wait.setFocusable(true);
        wait.setOutsideTouchable(false);

        ColorDrawable dw = new ColorDrawable(0x00000000);
        wait.setBackgroundDrawable(dw);

        wait.setAnimationStyle(R.style.PopupWindow_Style1);
        wait.showAtLocation(mView, Gravity.CENTER, 0, 0);

        return wait;
    }
}
