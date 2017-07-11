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
        String str = "";
        System.out.print(EncryptionUtils.parseJWT(str, VarConstant.KEY));
    }
}