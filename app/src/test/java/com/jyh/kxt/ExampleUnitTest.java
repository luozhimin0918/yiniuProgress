package com.jyh.kxt;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String testString = "第一回 风雪惊变\r\n  钱塘江浩浩江水，日日夜夜无穷无休的从临安牛家村边绕过，东流入海。";
        Pattern p = Pattern.compile("(^\\s*第)(.{1,9})[章节卷集部篇回](\\s*)(.*)(\n|\r|\r\n)");
        Matcher matcher = p.matcher(testString);
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println("group" + i + " : " + matcher.start(i) + " - " + matcher.end(i));
                System.out.println(matcher.group(i));
            }
        }
    }
}