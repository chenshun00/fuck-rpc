package top.huzhurong.fuck.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.*;
import top.huzhurong.fuck.spring.bean.AddressBean;
import top.huzhurong.fuck.spring.bean.PortBean;
import top.huzhurong.fuck.spring.bean.ServiceBean;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/2
 */
public class ServiceAnnotationProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    private Set<String> packages;
    private ClassLoader classLoader;
    private BeanDefinitionRegistry registry;
    private Environment environment;
    private ResourceLoader resourceLoader;

    public ServiceAnnotationProcessor(Set<String> packages) {
        this.packages = packages;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;

        if (StringUtils.hasText(environment.getProperty("fuck.protocol.port"))) {
            BeanDefinitionBuilder builder = rootBeanDefinition(PortBean.class);
            builder.addPropertyValue("port", environment.getProperty("fuck.protocol.port", "12345"));
            registry.registerBeanDefinition("portBean", builder.getBeanDefinition());
        }

        BeanDefinitionBuilder registryBean = rootBeanDefinition(AddressBean.class);
        registryBean.addPropertyValue("address", environment.getProperty("fuck.registry.address", "127.0.0.1:2181"));
        registry.registerBeanDefinition("addressBean", registryBean.getBeanDefinition());

        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packages);
        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
        }
    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;
        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }
        if (beanNameGenerator == null) {
            beanNameGenerator = new AnnotationBeanNameGenerator();
        }
        return beanNameGenerator;

    }

    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, true, this.environment, this.resourceLoader);
        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);
        scanner.setBeanNameGenerator(beanNameGenerator);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

        for (String packageToScan : packagesToScan) {
            // Registers @Service Bean first
            scanner.scan(packageToScan);
            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders = findServiceBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);
            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {
                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerServiceBean(beanDefinitionHolder, registry);
                }
            }
        }
    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {
        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
        return resolveClass(beanDefinition);
    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        return resolveClassName(beanClassName, classLoader);
    }

    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);
        Service service = findAnnotation(beanClass, Service.class);
        if (service == null) {
            return;
        }
        Class<?> interfaceClass = resolveServiceInterfaceClass(beanClass, service);
        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();
        AbstractBeanDefinition serviceBeanDefinition = buildServiceBeanDefinition(service, interfaceClass, annotatedServiceBeanName);
        // ServiceBean Bean name
        String beanName = generateServiceBeanName(service, interfaceClass, annotatedServiceBeanName);

        if (check(beanName, serviceBeanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(beanName, serviceBeanDefinition);
        }
    }

    protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return !(existingDefinition instanceof ScannedGenericBeanDefinition) || newDefinition.getSource().equals(existingDefinition.getSource()) || newDefinition.equals(existingDefinition);  // scanned equivalent class twice
    }

    private boolean check(String beanName, BeanDefinition beanDefinition) {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        existingDef = existingDef.getOriginatingBeanDefinition();
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new RuntimeException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    private static final String SEPARATOR = ":";

    private String generateServiceBeanName(Service service, Class<?> interfaceClass, String annotatedServiceBeanName) {
        StringBuilder beanNameBuilder = new StringBuilder(ServiceBean.class.getSimpleName());
        beanNameBuilder.append(SEPARATOR).append(annotatedServiceBeanName);
        String interfaceClassName = interfaceClass.getName();
        beanNameBuilder.append(SEPARATOR).append(interfaceClassName);
        String version = service.version();
        if (StringUtils.hasText(version)) {
            beanNameBuilder.append(SEPARATOR).append(version);
        }
        return beanNameBuilder.toString();
    }

    public static <T> T[] of(T... values) {
        return values;
    }

    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }

    private void addValue(BeanDefinitionBuilder builder, String propertyName, Object beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName.toString());
        builder.addPropertyValue(propertyName, resolvedBeanName);
    }

    private AbstractBeanDefinition buildServiceBeanDefinition(Service service, Class<?> interfaceClass, String annotatedServiceBeanName) {
        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);
        //propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(service, environment, ignoreAttributeNames));
        // References "ref" property to annotated-@Service Bean
        addValue(builder, "impl", annotatedServiceBeanName);
        // Set interface
        service.interfaceClass();
        addValue(builder, "interfaceName", interfaceClass.getName());
        addValue(builder, "version", service.version());
        addValue(builder, "weight", service.weight());
        addValue(builder, "serialization", service.serialization());

        addPropertyReference(builder, "protocolPort", "portBean");
        addPropertyReference(builder, "register", "addressBean");
        return builder.getBeanDefinition();
    }

    private ManagedList<RuntimeBeanReference> toRuntimeBeanReferences(String... beanNames) {
        ManagedList<RuntimeBeanReference> runtimeBeanReferences = new ManagedList<>();
        if (!ObjectUtils.isEmpty(beanNames)) {
            for (String beanName : beanNames) {
                String resolvedBeanName = environment.resolvePlaceholders(beanName);
                runtimeBeanReferences.add(new RuntimeBeanReference(resolvedBeanName));
            }
        }
        return runtimeBeanReferences;
    }

    private Class<?> resolveServiceInterfaceClass(Class<?> annotatedServiceBeanClass, Service service) {
        Class<?> interfaceClass = service.interfaceClass();
        if (void.class.equals(interfaceClass)) {
            interfaceClass = null;
            String interfaceClassName = service.interfaceName();
            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = resolveClassName(interfaceClassName, classLoader);
                }
            }
        }
        if (interfaceClass == null) {
            Class<?>[] allInterfaces = annotatedServiceBeanClass.getInterfaces();
            if (allInterfaces.length > 0) {
                interfaceClass = allInterfaces[0];
            }
        }
        Assert.notNull(interfaceClass, "@Service interfaceClass() or interfaceName() or interface class must be present!");
        Assert.isTrue(interfaceClass.isInterface(),
                "The type that was annotated @Service is not an interface!");

        return interfaceClass;
    }

    private Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>(beanDefinitions.size());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);
        }
        return beanDefinitionHolders;
    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
