package com.jyh.kxt.push;

import android.content.Context;
import android.content.Intent;

import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.library.manager.ActivityManager;

/**
 * Created by Administrator on 2017/7/24.
 */

public class PushUtil {
    public static void pushToMainActivity(Context context){
        boolean  isExitMain= ActivityManager.getInstance().isExistActivity(MainActivity.class);
          if(!isExitMain){
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(SpConstant.MAIN_ACTIVITY_FROM, 1);
                    context.startActivity(intent);
           }
    }
}
