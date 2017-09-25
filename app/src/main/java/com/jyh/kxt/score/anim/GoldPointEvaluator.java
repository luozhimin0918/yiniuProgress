package com.jyh.kxt.score.anim;

import android.graphics.Point;
import android.util.Log;

import com.nineoldandroids.animation.TypeEvaluator;

/**
 * Created by Mr'Dai on 2017/9/22.
 */

public class GoldPointEvaluator implements TypeEvaluator {

    // 复写evaluate（）
    // 在evaluate（）里写入对象动画过渡的逻辑
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {

        // 将动画初始值startValue 和 动画结束值endValue 强制类型转换成Point对象
        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;

        // 根据fraction来计算当前动画的x和y的值
        int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
        int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));

        // 将计算后的坐标封装到一个新的Point对象中并返回
        return new Point(x, y);
    }

}