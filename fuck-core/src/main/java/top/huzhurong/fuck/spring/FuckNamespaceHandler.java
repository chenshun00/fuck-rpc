package top.huzhurong.fuck.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import top.huzhurong.fuck.spring.bean.AddressBean;
import top.huzhurong.fuck.spring.bean.PortBean;
import top.huzhurong.fuck.spring.bean.ReferenceBean;
import top.huzhurong.fuck.spring.bean.ServiceBean;
import top.huzhurong.fuck.spring.parser.FuckAddressParser;
import top.huzhurong.fuck.spring.parser.FuckProtocolParser;
import top.huzhurong.fuck.spring.parser.FuckReferenceParser;
import top.huzhurong.fuck.spring.parser.FuckServerParser;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class FuckNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("protocol", new FuckProtocolParser(PortBean.class));
        registerBeanDefinitionParser("address", new FuckAddressParser(AddressBean.class));
        registerBeanDefinitionParser("service", new FuckServerParser(ServiceBean.class));
        registerBeanDefinitionParser("reference", new FuckReferenceParser(ReferenceBean.class));
    }
}
