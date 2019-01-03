package top.huzhurong.fuck.spring.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
@Slf4j
public class FuckProtocolParser extends AbstractSingleBeanDefinitionParser {

    private AtomicLong atomicLong = new AtomicLong(0);

    private Class aClass;

    public FuckProtocolParser(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        try {
            String port = element.getAttribute("port");
            if (StringUtils.hasText(port)) {
                builder.addPropertyValue("port", NumberUtils.parseNumber(port, Integer.class));
            }
        } catch (Exception ex) {
            log.error("解析服务暴露端口失败," + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return this.aClass;
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
            throws BeanDefinitionStoreException {
        String id = super.resolveId(element, definition, parserContext);
        if (StringUtils.hasText(id))
            return id;
        return "protocol_" + atomicLong.getAndIncrement();
    }

}
