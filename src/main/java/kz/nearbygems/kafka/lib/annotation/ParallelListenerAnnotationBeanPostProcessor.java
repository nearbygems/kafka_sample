package kz.nearbygems.kafka.lib.annotation;

import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.log.LogAccessor;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.ContainerGroupSequencer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class ParallelListenerAnnotationBeanPostProcessor<K, V> implements BeanPostProcessor,
                                                                          Ordered,
                                                                          ApplicationContextAware,
                                                                          InitializingBean,
                                                                          SmartInitializingSingleton {

  public static final String PARALLEL_KAFKA_LISTENER_CONTAINER = "parallelKafkaListenerContainerFactory";

  private static final String UNCHECKED           = "unchecked";
  private static final String THE_LEFT            = "The [";
  private static final String RESOLVED_TO_LEFT    = "Resolved to [";
  private static final String RIGHT_FOR_LEFT      = "] for [";
  private static final String GENERATED_ID_PREFIX = "org.springframework.kafka.KafkaListenerEndpointContainer#";

  private final KafkaHandlerMethodFactoryAdapter messageHandlerMethodFactory = new KafkaHandlerMethodFactoryAdapter();

  private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

  private final KafkaListenerEndpointRegistrar registrar = new KafkaListenerEndpointRegistrar();

  private final LogAccessor logger = new LogAccessor(LogFactory.getLog(getClass()));

  private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();

  private final AtomicInteger counter = new AtomicInteger();

  private final Charset charset = StandardCharsets.UTF_8;

  private ApplicationContext            applicationContext;
  private BeanExpressionContext         expressionContext;
  private KafkaListenerEndpointRegistry endpointRegistry;
  private BeanFactory                   beanFactory;
  private AnnotationEnhancer            enhancer;

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
    applicationContext = context;
    if (context instanceof ConfigurableApplicationContext) {
      setBeanFactory(((ConfigurableApplicationContext) context).getBeanFactory());
    } else {
      setBeanFactory(context);
    }
  }

  public void setBeanFactory(BeanFactory factory) {
    beanFactory = factory;
    if (factory instanceof ConfigurableListableBeanFactory) {
      resolver          = ((ConfigurableListableBeanFactory) factory).getBeanExpressionResolver();
      expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) factory, null);
    }
  }

  @Override
  public void afterPropertiesSet() {}

  @Override
  public void afterSingletonsInstantiated() {

    registrar.setBeanFactory(beanFactory);

    if (beanFactory instanceof ListableBeanFactory) {
      final var instances = ((ListableBeanFactory) this.beanFactory).getBeansOfType(KafkaListenerConfigurer.class);
      for (var configurer : instances.values()) {
        configurer.configureKafkaListeners(this.registrar);
      }
    }

    if (registrar.getEndpointRegistry() == null) {
      if (endpointRegistry == null) {
        Assert.state(this.beanFactory != null, "BeanFactory must be set to find endpoint registry by bean name");
        endpointRegistry = beanFactory.getBean(KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                                               KafkaListenerEndpointRegistry.class);
      }
      registrar.setEndpointRegistry(this.endpointRegistry);
    }

    registrar.setContainerFactoryBeanName(PARALLEL_KAFKA_LISTENER_CONTAINER);

    final var handlerMethodFactory = registrar.getMessageHandlerMethodFactory();

    if (handlerMethodFactory != null) {
      messageHandlerMethodFactory.setHandlerMethodFactory(handlerMethodFactory);
    } else {
      addFormatters(messageHandlerMethodFactory.service);
    }
    registrar.afterPropertiesSet();

    applicationContext.getBeansOfType(ContainerGroupSequencer.class, false, false).values()
                      .forEach(ContainerGroupSequencer :: initialize);
  }

  @Override
  public Object postProcessAfterInitialization(@NonNull final Object bean,
                                               @NonNull final String beanName) throws BeansException {

    if (!nonAnnotatedClasses.contains(bean.getClass())) {

      final var annotatedMethods = findAnnotatedMethods(AopUtils.getTargetClass(bean));

      if (!annotatedMethods.isEmpty()) {

        annotatedMethods.forEach((key, value) -> value.forEach(v -> processKafkaListener(v, key, bean)));

      }

    }
    return bean;
  }

  private Map<Method, Set<ParallelListener>> findAnnotatedMethods(Class<?> targetClass) {
    return MethodIntrospector.selectMethods(targetClass, (MethodIntrospector.MetadataLookup<Set<ParallelListener>>)
        method -> {
          var methods = findListenerAnnotations(method);
          return !methods.isEmpty() ? methods : null;
        });
  }

  private Set<ParallelListener> findListenerAnnotations(Method method) {

    final var listeners = new HashSet<ParallelListener>();

    var ann = AnnotatedElementUtils.findMergedAnnotation(method, ParallelListener.class);

    if (ann != null) {
      ann = enhance(method, ann);
      listeners.add(ann);
    }

    return listeners;
  }

  private ParallelListener enhance(AnnotatedElement element, ParallelListener ann) {
    return enhancer == null ? ann :
           AnnotationUtils.synthesizeAnnotation(enhancer.apply(AnnotationUtils.getAnnotationAttributes(ann), element),
                                                ParallelListener.class,
                                                null);
  }

  protected void processKafkaListener(ParallelListener kafkaListener, Method methodToUse, Object bean) {

    MethodKafkaListenerEndpoint<K, V> endpoint = new MethodKafkaListenerEndpoint<>();
    String[]                          topics   = resolveTopics(kafkaListener);
    endpoint.setMethod(methodToUse);

    processListener(endpoint, kafkaListener, bean, topics);
  }

  protected void processListener(MethodKafkaListenerEndpoint<?, ?> endpoint,
                                 ParallelListener kafkaListener, Object bean, String[] topics) {

    processKafkaListenerAnnotation(endpoint, kafkaListener, bean, topics);

    registrar.registerEndpoint(endpoint, null);
  }

  private void processKafkaListenerAnnotation(MethodKafkaListenerEndpoint<?, ?> endpoint,
                                              ParallelListener kafkaListener, Object bean, String[] topics) {

    endpoint.setBean(bean);
    endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    endpoint.setId(getEndpointId(kafkaListener));
    endpoint.setGroupId(getEndpointGroupId(kafkaListener, endpoint.getId()));
    endpoint.setTopics(topics);
    endpoint.setBatchListener(true);

    Optional.ofNullable(kafkaListener.autoStartup())
            .filter(StringUtils :: hasText)
            .map(this :: resolveExpressionAsBoolean)
            .ifPresent(endpoint :: setAutoStartup);

    resolveKafkaProperties(endpoint, kafkaListener.properties());
  }

  @SuppressWarnings(UNCHECKED)
  private void resolveKafkaProperties(MethodKafkaListenerEndpoint<?, ?> endpoint, String[] propertyStrings) {
    if (propertyStrings.length > 0) {

      final var properties = new Properties();

      for (var property : propertyStrings) {

        final var value = resolveExpression(property);

        if (value instanceof String) {
          loadProperty(properties, property, value);
        } else if (value instanceof String[]) {
          for (var prop : (String[]) value) {
            loadProperty(properties, prop, prop);
          }
        } else if (value instanceof Collection) {
          final var values = (Collection<?>) value;
          if (values.size() > 0 && values.iterator().next() instanceof String) {
            for (var prop : (Collection<String>) value) {
              loadProperty(properties, prop, prop);
            }
          }
        } else {
          final var message = "[properties] must resolve to a String, a String[] or Collection<String>";
          throw new IllegalStateException(message);
        }
      }

      endpoint.setConsumerProperties(properties);
    }
  }

  private void loadProperty(Properties properties, String property, Object value) {
    try {
      properties.load(new StringReader((String) value));
    } catch (IOException e) {
      logger.error(e, () -> "Failed to load property " + property + ", continuing...");
    }
  }

  private String getEndpointId(ParallelListener kafkaListener) {
    if (StringUtils.hasText(kafkaListener.id())) {
      return resolveExpressionAsString(kafkaListener.id(), "id");
    } else {
      return GENERATED_ID_PREFIX + counter.getAndIncrement();
    }
  }

  private String getEndpointGroupId(ParallelListener kafkaListener, String id) {
    if (StringUtils.hasText(kafkaListener.groupId())) {
      return resolveExpressionAsString(kafkaListener.groupId(), "groupId");
    }
    return id;
  }

  private String[] resolveTopics(ParallelListener kafkaListener) {
    final var topics = kafkaListener.topics();
    final var result = new ArrayList<String>();
    for (var topic : topics) {
      resolveAsString(resolveExpression(topic), result);
    }
    return result.toArray(new String[0]);
  }

  @SuppressWarnings(UNCHECKED)
  private void resolveAsString(Object resolvedValue, List<String> result) {
    if (resolvedValue instanceof String[]) {
      for (var object : (String[]) resolvedValue) {
        resolveAsString(object, result);
      }
    } else if (resolvedValue instanceof String) {
      result.add((String) resolvedValue);
    } else if (resolvedValue instanceof Iterable) {
      for (var object : (Iterable<Object>) resolvedValue) {
        resolveAsString(object, result);
      }
    } else {
      final var message = String.format("@ParallelListener can't resolve '%s' as a String", resolvedValue);
      throw new IllegalArgumentException(message);
    }
  }

  private String resolveExpressionAsString(String value, String attribute) {
    final var resolved = resolveExpression(value);
    if (resolved instanceof String) {
      return (String) resolved;
    } else if (resolved != null) {
      throw new IllegalStateException(THE_LEFT + attribute + "] must resolve to a String. "
                                      + RESOLVED_TO_LEFT + resolved.getClass() + RIGHT_FOR_LEFT + value + "]");
    }
    return null;
  }

  private Boolean resolveExpressionAsBoolean(String value) {
    final var resolved = resolveExpression(value);
    Boolean   result   = null;
    if (resolved instanceof Boolean) {
      result = (Boolean) resolved;
    } else if (resolved instanceof String) {
      result = Boolean.parseBoolean((String) resolved);
    } else if (resolved != null) {
      throw new IllegalStateException(
          THE_LEFT + "autoStartup" + "] must resolve to a Boolean or a String that can be parsed as a Boolean. "
          + RESOLVED_TO_LEFT + resolved.getClass() + RIGHT_FOR_LEFT + value + "]");
    }
    return result;
  }

  private Object resolveExpression(String value) {
    return resolver.evaluate(resolve(value), this.expressionContext);
  }

  private String resolve(String value) {
    if (beanFactory != null && beanFactory instanceof ConfigurableBeanFactory) {
      return ((ConfigurableBeanFactory) beanFactory).resolveEmbeddedValue(value);
    }
    return value;
  }

  private void addFormatters(FormatterRegistry registry) {
    for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
      registry.addConverter(converter);
    }
    for (ConverterFactory<?, ?> converter : getBeansOfType(ConverterFactory.class)) {
      registry.addConverterFactory(converter);
    }
    for (GenericConverter converter : getBeansOfType(GenericConverter.class)) {
      registry.addConverter(converter);
    }
    for (Formatter<?> formatter : getBeansOfType(Formatter.class)) {
      registry.addFormatter(formatter);
    }
  }

  private <T> Collection<T> getBeansOfType(Class<T> type) {
    if (beanFactory instanceof ListableBeanFactory) {
      return ((ListableBeanFactory) beanFactory)
          .getBeansOfType(type)
          .values();
    } else {
      return Collections.emptySet();
    }
  }

  public interface AnnotationEnhancer extends BiFunction<Map<String, Object>, AnnotatedElement, Map<String, Object>> {}

  private class KafkaHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {

    private final DefaultFormattingConversionService service = new DefaultFormattingConversionService();

    private MessageHandlerMethodFactory handlerMethodFactory;

    public void setHandlerMethodFactory(MessageHandlerMethodFactory kafkaHandlerMethodFactory1) {
      handlerMethodFactory = kafkaHandlerMethodFactory1;
    }

    @Override
    public @NonNull InvocableHandlerMethod createInvocableHandlerMethod(@NonNull Object bean, @NonNull Method method) {
      return getHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
    }

    private MessageHandlerMethodFactory getHandlerMethodFactory() {
      if (handlerMethodFactory == null) {
        handlerMethodFactory = createDefaultMessageHandlerMethodFactory();
      }
      return handlerMethodFactory;
    }

    private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {

      final var defaultFactory = new DefaultMessageHandlerMethodFactory();

      Validator validator = ParallelListenerAnnotationBeanPostProcessor.this.registrar.getValidator();

      if (validator != null) {
        defaultFactory.setValidator(validator);
      }

      defaultFactory.setBeanFactory(ParallelListenerAnnotationBeanPostProcessor.this.beanFactory);

      service.addConverter(new BytesToStringConverter(ParallelListenerAnnotationBeanPostProcessor.this.charset));
      service.addConverter(new BytesToNumberConverter());

      defaultFactory.setConversionService(service);

      final var messageConverter = new GenericMessageConverter(this.service);

      defaultFactory.setMessageConverter(messageConverter);

      final var resolvers = new ArrayList<>(ParallelListenerAnnotationBeanPostProcessor.this
                                                .registrar.getCustomMethodArgumentResolvers());

      resolvers.add(new ParallelKafkaNullAwarePayloadArgumentResolver(messageConverter, validator));

      defaultFactory.setCustomArgumentResolvers(resolvers);
      defaultFactory.afterPropertiesSet();

      return defaultFactory;
    }

  }

  private static class BytesToStringConverter implements Converter<byte[], String> {

    private final Charset charset;

    BytesToStringConverter(Charset chars) {
      charset = chars;
    }

    @Override
    public String convert(@NonNull byte[] source) {
      return new String(source, charset);
    }

  }

  private static final class BytesToNumberConverter implements ConditionalGenericConverter {

    BytesToNumberConverter() {}

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
      final var pairs = new HashSet<ConvertiblePair>();
      pairs.add(new ConvertiblePair(byte[].class, long.class));
      pairs.add(new ConvertiblePair(byte[].class, int.class));
      pairs.add(new ConvertiblePair(byte[].class, short.class));
      pairs.add(new ConvertiblePair(byte[].class, byte.class));
      pairs.add(new ConvertiblePair(byte[].class, Long.class));
      pairs.add(new ConvertiblePair(byte[].class, Integer.class));
      pairs.add(new ConvertiblePair(byte[].class, Short.class));
      pairs.add(new ConvertiblePair(byte[].class, Byte.class));
      return pairs;
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, @NonNull TypeDescriptor sourceType, TypeDescriptor targetType) {
      byte[] bytes = (byte[]) source;
      assert bytes != null;
      if (targetType.getType().equals(long.class) || targetType.getType().equals(Long.class)) {
        Assert.state(bytes.length >= 8, "At least 8 bytes needed to convert a byte[] to a long");
        return ByteBuffer.wrap(bytes).getLong();
      } else if (targetType.getType().equals(int.class) || targetType.getType().equals(Integer.class)) {
        Assert.state(bytes.length >= 4, "At least 4 bytes needed to convert a byte[] to an integer");
        return ByteBuffer.wrap(bytes).getInt();
      } else if (targetType.getType().equals(short.class) || targetType.getType().equals(Short.class)) {
        Assert.state(bytes.length >= 2, "At least 2 bytes needed to convert a byte[] to a short");
        return ByteBuffer.wrap(bytes).getShort();
      } else if (targetType.getType().equals(byte.class) || targetType.getType().equals(Byte.class)) {
        Assert.state(bytes.length >= 1, "At least 1 byte needed to convert a byte[] to a byte");
        return ByteBuffer.wrap(bytes).get();
      }
      return null;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
      if (sourceType.getType().equals(byte[].class)) {
        Class<?> target = targetType.getType();
        return target.equals(long.class) || target.equals(int.class) || target.equals(short.class) // NOSONAR
               || target.equals(byte.class) || target.equals(Long.class) || target.equals(Integer.class)
               || target.equals(Short.class) || target.equals(Byte.class);
      } else {
        return false;
      }
    }

  }

}
