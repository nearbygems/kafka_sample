package kz.nearbygems.kafka.lib.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

public class ParallelKafkaBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

  public static final String KAFKA_LISTENER_ANNOTATION_REGISTRY_BEAN_NAME =
      "kz.ks.search.annotation.ParallelListenerAnnotationBeanPostProcessor";

  @Override
  public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                      BeanDefinitionRegistry registry) {

    if (!registry.containsBeanDefinition(KAFKA_LISTENER_ANNOTATION_REGISTRY_BEAN_NAME)) {
      registry.registerBeanDefinition(KAFKA_LISTENER_ANNOTATION_REGISTRY_BEAN_NAME,
                                      new RootBeanDefinition(ParallelListenerAnnotationBeanPostProcessor.class));
    }

  }

}
