package com.jyh.kxt;

import android.support.test.runner.AndroidJUnit4;

import com.jyh.kxt.base.constant.VarConstant;
import com.library.util.EncryptionUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        String str = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJhdWQiOiJodHRwOlwvXC93d3cua3h0LmNvbSIsInN0YXR1cyI6MCwi" +
                "bXNnIjoiXHU1M2MyXHU2NTcwXHU5NTE5XHU4YmVmOmNvbnRlbnQiLCJkYXRhIjpbXX0" +
                ".yz1NISOmAcDMaPZdHCy5Ujv5wQqStChluVoEV1huuls";

        EncryptionUtils.parseJWT(str, VarConstant.KEY);
    }
}
