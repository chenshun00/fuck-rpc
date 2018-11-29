package top.huzhurong.fuck.serialization;

import org.junit.Before;

import java.util.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
public class BaseTest {

    protected User user = new User();

    @Before
    public void before() {
        user.setDate(new Date());
        user.setName("chenshun00");
        user.setStrings(Arrays.asList("12", "23", "34", "45", "68sdfsdfsdklfj"));
        {
            Map<String, String> param = new HashMap<>();
            param.put("chen", "shun");
            param.put("ji", "shu");
            param.put("jichu", "dalao");
            user.setMaps(param);
        }
        {
            Map<String, List<String>> listMap = new HashMap<>();
            listMap.put("chen", Arrays.asList("chen", "shun", "00", "123"));
            listMap.put("shun", Arrays.asList("cc", "ss", "11", "321"));
            user.setListMap(listMap);
        }
    }

}
