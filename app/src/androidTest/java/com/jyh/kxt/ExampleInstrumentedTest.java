package com.jyh.kxt;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.util.Log;
import android.util.Patterns;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;

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

        Matcher m = Patterns.WEB_URL.matcher("");

        while (m.find()) {
            int start = m.start();
            int end = m.end();
            Log.e("ExampleTest", "setUp: "+ m.group(1));
        }
    }

    @Test
    public void useAppContext() throws Exception {

    }
}
