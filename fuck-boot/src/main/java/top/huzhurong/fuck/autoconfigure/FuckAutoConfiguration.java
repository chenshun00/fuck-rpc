package top.huzhurong.fuck.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.huzhurong.fuck.spring.annotation.ReferenceAnnotationProcessor;
import top.huzhurong.fuck.spring.annotation.ServiceAnnotationProcessor;

import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/2
 */
@Configuration
public class FuckAutoConfiguration {

    @ConditionalOnProperty(name = "fuck.packages")
    @ConditionalOnClass(ConfigurationPropertySources.class)
    @Bean
    public ServiceAnnotationProcessor serviceAnnotationBeanPostProcessor(Environment environment) {
        Set<String> packagesToScan = environment.getProperty("fuck.packages", Set.class, emptySet());
        return new ServiceAnnotationProcessor(packagesToScan);
    }

    @ConditionalOnMissingBean
    @Bean()
    public ReferenceAnnotationProcessor referenceAnnotationBeanPostProcessor() {
        return new ReferenceAnnotationProcessor();
    }


}
