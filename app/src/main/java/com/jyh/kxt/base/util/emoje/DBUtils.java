package com.jyh.kxt.base.util.emoje;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 数据库操作
 * @author shantf 
 * @Time 2015-08-06
 */
public class DBUtils {

 
    /**
     * 把数据库拷贝SD卡根目录
     * @param context 上下文
     * @param dbName 数据库名
     * @return 成功失败
     */
    public static boolean toSDWriteFile(Context context,String dbName){  
        String local_file;
        try {
            String sPackage = context.getPackageName();  
            File mSaveFile = new File("/data/data/" + sPackage + "/databases/");  
            if (!mSaveFile.exists()) {  
                mSaveFile.mkdirs();  
            }  
            local_file = mSaveFile.getAbsolutePath() + "/"+dbName;  
            mSaveFile = new File(local_file);  
            FileInputStream is=new FileInputStream(mSaveFile);
            
            String path = "";
            boolean hasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (hasSDCard) {
                path = Environment.getExternalStorageDirectory().getPath();
            } else {
                path = "/data/";
            }
            File file = new File(path, dbName);
            if(file.exists())
            {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileOutputStream fos = new FileOutputStream(file, true);  
            byte[] buffer = new byte[400000];  
            int count = 0;  
            while ((count = is.read(buffer)) > 0) {  
                fos.write(buffer, 0, count);  
            }  
            mSaveFile = null;  
            fos.close();  
            is.close();
        }catch (Exception e) {
            return false;
        }  
        return true;  
    }  
}
