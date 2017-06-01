package com.jyh.kxt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.util.Base64;

import com.jyh.kxt.base.utils.Encrypt;
import com.library.base.http.VarConstant;
import com.library.util.EncryptionUtils;
import com.library.util.SystemUtil;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends AndroidTestCase {

    Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context = new MockContext();
    }

    @Test
    public void useAppContext() throws Exception {

    }
}
