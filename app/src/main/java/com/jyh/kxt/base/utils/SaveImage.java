package com.jyh.kxt.base.utils;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/14.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.library.widget.window.ToastView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/***
 * 功能：用线程保存图片
 *
 * @author wangyp
 */
public class SaveImage extends AsyncTask<String, Void, String> {

    private Context mContext;

    public SaveImage(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, "图片保存中");
                }
            });
            String sdcard = Environment.getExternalStorageDirectory()
                    .toString();
            File file = new File(sdcard + "/Download");
            if (!file.exists()) {
                file.mkdirs();
            }
            String imgurl = params[0];
            int idx = imgurl.lastIndexOf(".");
            String ext = imgurl.substring(idx);
            file = new File(sdcard + "/Download/" + new Date().getTime()
                    + ext);
            InputStream inputStream = null;
            URL url = new URL(imgurl);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            if (conn.getResponseCode() == 200) {
                inputStream = conn.getInputStream();
            }
            byte[] buffer = new byte[4096];
            int len = 0;
            FileOutputStream outStream = new FileOutputStream(file);
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            result = "图片已保存至：" + file.getAbsolutePath();
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(file)));
        } catch (Exception e) {
            result = "保存失败！" + e.getLocalizedMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        ToastView.makeText3(mContext, result);
    }
}