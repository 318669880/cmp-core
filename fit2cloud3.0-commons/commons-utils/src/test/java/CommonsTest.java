import com.fit2cloud.commons.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class CommonsTest {
    @Test
    public void test1() {
        System.out.println(StringUtils.isEmpty(""));
    }

    @Test
    public void test2() {
        String s = HttpClientUtil.get("http://baidu.com", null);
        System.out.println(s);
    }
}
