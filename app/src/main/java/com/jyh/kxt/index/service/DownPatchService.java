package com.jyh.kxt.index.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.json.PatchJson;
import com.library.util.FileUtils;
import com.library.util.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mr'Dai on 2017/6/23.
 */

public class DownPatchService extends Service {

    @Override
    public void onCreate() {
        android.os.Debug.waitForDebugger();
        super.onCreate();
        String patchInfo = SPUtils.getString(this, SpConstant.PATCH_INFO);
        PatchJson patchJson = JSONObject.parseObject(patchInfo, PatchJson.class);

        downPatch(patchJson);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downPatch(PatchJson patchJson) {
        try {
            String saveFilePath = FileUtils.getVersionNameFilePath(this);
            File file = new File(saveFilePath + patchJson.getPatch_code());
            if (file.exists()) {
                return;
            }

            URL url = new URL(patchJson.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream input = conn.getInputStream();
            FileOutputStream output = new FileOutputStream(file);
            //读取大文件
            byte[] buffer = new byte[4 * 1024];
            while (input.read(buffer) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
