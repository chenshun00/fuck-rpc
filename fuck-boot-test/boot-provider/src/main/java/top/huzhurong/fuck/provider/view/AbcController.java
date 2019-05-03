package top.huzhurong.fuck.provider.view;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.huzhurong.fuck.provider.impl.AbcImpl;

import javax.annotation.Resource;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@Controller
public class AbcController implements ApplicationContextAware {

    @Resource
    private AbcImpl abcImpl;

    @RequestMapping("abc")
    @ResponseBody
    public Object request() {
        System.out.println(abcImpl.good());
        return "11";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;
}
