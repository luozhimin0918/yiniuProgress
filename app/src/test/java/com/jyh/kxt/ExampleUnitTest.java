package com.jyh.kxt;

import com.library.base.http.VarConstant;
import com.library.util.EncryptionUtils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.print(EncryptionUtils.parseJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJzdGF0dXMiOjEsIm1zZyI6Ilx1ODNiN1x1NTNkNlx1NjIxMFx1NTI5ZiIsImRhdGEiOnsic2VydmVyIjoid3M6XC9cLzEyMC4yNy4xOTUuNDo5NTAyIiwidG9rZW4iOiJnWGFtcklaM3A2dUdxWGVzcm9EV3k1YXBvYTJUaG9LY2Y0YTBsbl9kZWF1Q25LNW9pSGJkblpPc2I2bTZqOF9YZTh5a29wcDdpNWFWZHRQRWZwU3ZxSTJLcDZ5U2lwdWZuTkp2cGJ0N21OU1hxb2F1a21WeTJudWIyc3FWMEk2bWxtVzNvWkZsckotVHJYLWVzS0dzMm9HbWRxNkdoNXlYZnB5b2xYX01iS3VCZHRDZW40U2pjUSJ9fQ.qhaPu13QXX5I7P3sA263zlIiSbaRl21gq3Di8Wo-hlg", VarConstant.KEY));
    }
}