package top.huzhurong.fuck.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import top.huzhurong.fuck.spring.bean.ReferenceBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/2
 */
public class ReferenceAnnotationProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware, BeanClassLoaderAware,
        DisposableBean {

    private final ConcurrentMap<String, ReferenceInjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);
    private final ConcurrentMap<String, ReferenceBean> referenceBeansCache = new ConcurrentHashMap<>();

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {

        InjectionMetadata metadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @Reference dependencies failed", ex);
        }
        return pvs;
    }

    private ReferenceInjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {
        Collection<ReferenceFieldElement> fieldElements = findFieldReferenceMetadata(beanClass);
        return new ReferenceInjectionMetadata(beanClass, fieldElements);
    }


    private List<ReferenceFieldElement> findFieldReferenceMetadata(final Class<?> beanClass) {
        final List<ReferenceFieldElement> elements = new LinkedList<ReferenceFieldElement>();
        ReflectionUtils.doWithFields(beanClass, field -> {
            Reference reference = getAnnotation(field, Reference.class);
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            elements.add(new ReferenceFieldElement(field, reference));
        });
        return elements;
    }


    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        ReferenceInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private static class ReferenceInjectionMetadata extends InjectionMetadata {

        private final Collection<ReferenceFieldElement> fieldElements;

        public ReferenceInjectionMetadata(Class<?> targetClass, Collection<ReferenceFieldElement> fieldElements) {
            super(targetClass, combine(fieldElements));
            this.fieldElements = fieldElements;
        }

        private static <T> Collection<T> combine(Collection<? extends T>... elements) {
            List<T> allElements = new ArrayList<T>();
            for (Collection<? extends T> e : elements) {
                allElements.addAll(e);
            }
            return allElements;
        }

        public Collection<ReferenceFieldElement> getFieldElements() {
            return fieldElements;
        }
    }


    /**
     * {@link Reference} {@link Field} {@link InjectionMetadata.InjectedElement}
     */
    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final Reference reference;

        private volatile ReferenceBean referenceBean;

        protected ReferenceFieldElement(Field field, Reference reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Class<?> referenceClass = field.getType();
            if (reference == null) {
                return;
            }
            referenceBean = buildReferenceBean(reference, referenceClass);
            ReflectionUtils.makeAccessible(field);
            field.set(bean, referenceBean.getObject());
        }
    }

    private ReferenceBean buildReferenceBean(Reference reference, Class<?> referenceClass) throws Exception {
        String referenceBeanCacheKey = generateReferenceBeanCacheKey(reference, referenceClass);
        ReferenceBean referenceBean = referenceBeansCache.get(referenceBeanCacheKey);
        if (referenceBean == null) {
            ReferenceBeanBuilder beanBuilder = ReferenceBeanBuilder.create(reference, classLoader, applicationContext).interfaceClass(referenceClass);
            referenceBean = beanBuilder.build();
            referenceBeansCache.putIfAbsent(referenceBeanCacheKey, referenceBean);
        }
        return referenceBean;
    }

    private String generateReferenceBeanCacheKey(Reference reference, Class<?> beanClass) {
        String interfaceName = beanClass.getName();
        String key = interfaceName + "/" + reference.version();
        Environment environment = applicationContext.getEnvironment();
        key = environment.resolvePlaceholders(key);
        return key;
    }
}
