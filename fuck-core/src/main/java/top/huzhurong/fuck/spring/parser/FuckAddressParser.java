package top.huzhurong.fuck.spring.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class FuckAddressParser implements BeanDefinitionParser {

    private Class aClass;

    public FuckAddressParser(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setParentName(null);
        rootBeanDefinition.setBeanClass(this.aClass);
        rootBeanDefinition.setTargetType(this.aClass);
        rootBeanDefinition.setLazyInit(false);
        String address = element.getAttribute("address");
        rootBeanDefinition.getPropertyValues().addPropertyValue("address", address);
        parserContext.getRegistry().registerBeanDefinition("zkRegister", rootBeanDefinition);
        return rootBeanDefinition;
    }
}
