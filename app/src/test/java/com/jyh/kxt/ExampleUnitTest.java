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
        System.out.print(EncryptionUtils.parseJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdGF0dXMiOjAsIm1zZyI6Ilx1NTNjMlx1NjU3MFx1OTUxOVx1OGJlZjpjb250ZW50IiwiZGF0YSI6W119.EZc8_jgVcemugAM_7cOiAwhgsYGYdOVvjXBFEyJ-xt8", VarConstant.KEY));
    }
}