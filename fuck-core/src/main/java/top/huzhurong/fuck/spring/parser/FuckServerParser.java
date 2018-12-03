package top.huzhurong.fuck.spring.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Slf4j
public class FuckServerParser extends AbstractSingleBeanDefinitionParser {

    private Class aClass;

    public FuckServerParser(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return this.aClass;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        try {
            String anInterface = element.getAttribute("interface");
            builder.addPropertyValue("interfaceName", anInterface);

            String id = element.getAttribute("id");
            builder.addPropertyValue("id", id);

            String version = element.getAttribute("version");
            builder.addPropertyValue("version", version);

            String weight = element.getAttribute("weight");
            if (StringUtils.hasText(weight)) {
                builder.addPropertyValue("weight", NumberUtils.parseNumber(weight, Integer.class));
            }
            String serialization = element.getAttribute("serialization");
            if (StringUtils.hasText(serialization)) {
                builder.addPropertyValue("serialization", serialization);
            }
        } catch (Exception ex) {
            log.error("解析服务出错," + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        super.doParse(element, builder);
    }
}
