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
        String str = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJ2ZXJzaW9uXCI6XCI2LjAuMFwiLFwic3lzdGVtXCI6XCJhbmRyb2lkXCJ9In0.aiXlxNgKLe_C6Phg5_OXZOq66IU5tzBrgw7_1GnDdHw";
        System.out.print(EncryptionUtils.parseJWT(str, VarConstant.KEY));
    }
}