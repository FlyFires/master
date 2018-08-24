package online.gameservice.meizu.com.gamepublishsdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        int  version = 1010000;
        int a1 = version / 1000000;
        int b1 = (version - a1 * 1000000) / 1000;
        int c1 = (version - a1 * 1000000 - b1 * 1000) / 1000;
        System.out.print(a1 + "." + b1 + c1);
    }

}