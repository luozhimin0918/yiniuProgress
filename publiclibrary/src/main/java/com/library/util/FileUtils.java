package com.library.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * @author Mr'Dai
 * @date 2016/5/16 16:00
 * @Title: MobileLibrary
 * @Package com.library.util
 * @Description:
 */
public class FileUtils {
    /**
     * 将图片保存到Android文件夹下的Cache目录下面
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = getSaveFilePath(context);
        File file = new File(cachePath + File.separator + uniqueName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 得到文件存储路径
     */
    public static String getSaveFilePath(Context context) {
        String cachePath;
        cachePath = context.getApplicationContext().getFilesDir().getPath();
        return cachePath;
    }

    /**
     * 得到文件当前版本存储路径
     */
    public static String getVersionNameFilePath(Context context) {

        String versionName = SystemUtil.getVersionName(context);

        String cachePath;
        cachePath = context.getApplicationContext().getFilesDir().getPath() + "/" + versionName + "/";
        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return cachePath;
    }

    public static String getSDSaveFilePath(Context context) {
        String sdCachePath = "";
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable())) {
            sdCachePath = context.getExternalCacheDir().getPath();
            File file = new File(sdCachePath);
            if (!file.exists()) {
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sdCachePath;
    }

    public static void saveWebPage(Context mContext, String url, String content) {
        String urlMd5Name = ConvertUtils.md5(url);
        String filePath = getSaveFilePath(mContext) + File.separator + urlMd5Name + ".txt";
        try {
            File file = new File(filePath);
            if (file.exists()) {

                return;
            }
            File dir = new File(file.getParent());
            dir.mkdirs();
            file.createNewFile();
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getWebPage(Context mContext, String url) {

        StringBuffer stringBuffer = new StringBuffer();

        String urlMd5Name = ConvertUtils.md5(url);
        String filePath = getSaveFilePath(mContext) + File.separator + urlMd5Name + ".txt";
        try {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader bre = new BufferedReader(new FileReader(file));//此时获取到的bre就是整个文件的缓存流
                String str;
                while ((str = bre.readLine()) != null) { // 判断最后一行不存在，为空结束循环
                    stringBuffer.append(str);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
