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
 * @since 2018/12/1
 */
@Slf4j
public class FuckAddressParser extends AbstractSingleBeanDefinitionParser {

    private AtomicLong atomicLong = new AtomicLong(0);

    private Class aClass;

    public FuckAddressParser(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return this.aClass;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        try {
            String address = element.getAttribute("address");
            builder.addPropertyValue("address", address);
            String session = element.getAttribute("session");
            if (StringUtils.hasText(session)) {
                Integer integer = NumberUtils.parseNumber(session, Integer.class);
                builder.addPropertyValue("session", integer);
            }
            String connection = element.getAttribute("connection");
            if (StringUtils.hasText(connection)) {
                Integer integer = NumberUtils.parseNumber(connection, Integer.class);
                builder.addPropertyValue("connection", integer);
            }
        } catch (Exception ex) {
            log.error("解析 fuck-address 标签失败," + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
            throws BeanDefinitionStoreException {
        String id = super.resolveId(element, definition, parserContext);
        if (StringUtils.hasText(id))
            return id;
        return "address_" + atomicLong.getAndIncrement();
    }
}
