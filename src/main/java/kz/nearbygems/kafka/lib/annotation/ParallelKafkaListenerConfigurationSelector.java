package kz.nearbygems.kafka.lib.annotation;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

@Order
public class ParallelKafkaListenerConfigurationSelector implements DeferredImportSelector {

  @Override
  public @NonNull String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
    return new String[]{ParallelKafkaBootstrapConfiguration.class.getName()};
  }

}
