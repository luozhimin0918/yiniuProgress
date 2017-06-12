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
        System.out.print(EncryptionUtils.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJzeXN0ZW1cIjpcImFuZHJvaWRcIixcInZlcnNpb25cIjpcIjYuMC4wXCJ9In0.MoO4BMZqrevif7ForeSXTZ55TNof4qTcp60YLZbUCfs", VarConstant.KEY));
    }
}