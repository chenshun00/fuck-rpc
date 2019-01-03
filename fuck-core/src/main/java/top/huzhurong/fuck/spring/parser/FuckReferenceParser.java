package top.huzhurong.fuck.spring.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Slf4j
public class FuckReferenceParser extends AbstractSingleBeanDefinitionParser {

    private Class aClass;

    public FuckReferenceParser(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return this.aClass;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        try {
            String id = element.getAttribute("id");
            builder.addPropertyValue("id", id);
            String anInterface = element.getAttribute("interface");
            builder.addPropertyValue("interfaceName", anInterface);
            String version = element.getAttribute("version");
            builder.addPropertyValue("version", version);
            String timeout = element.getAttribute("timeout");

            String loadBalance = element.getAttribute("loadBalance");
            if (StringUtils.hasText(loadBalance)) {
                builder.addPropertyValue("loadBalance", loadBalance);
            }

            if (StringUtils.hasText(timeout)) {
                builder.addPropertyValue("timeout", NumberUtils.parseNumber(timeout, Integer.class));
            }
        } catch (Exception ex) {
            log.error("解析引用服务失败," + element.getAttribute("interface") + "---" + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
}
