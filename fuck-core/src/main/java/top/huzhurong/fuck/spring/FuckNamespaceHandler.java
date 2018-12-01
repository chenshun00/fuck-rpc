package top.huzhurong.fuck.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.spring.parser.FuckAddressParser;
import top.huzhurong.fuck.spring.parser.FuckReferenceParser;
import top.huzhurong.fuck.spring.parser.FuckServerParser;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class FuckNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("fuck-address", new FuckAddressParser(ZkRegister.class));
        registerBeanDefinitionParser("fuck-service", new FuckServerParser());
        registerBeanDefinitionParser("fuck-reference", new FuckReferenceParser());
    }
}
