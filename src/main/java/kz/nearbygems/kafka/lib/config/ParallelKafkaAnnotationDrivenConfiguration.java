package kz.nearbygems.kafka.lib.config;

import kz.nearbygems.kafka.lib.annotation.EnableParallelKafka;
import kz.nearbygems.kafka.lib.annotation.ParallelListenerAnnotationBeanPostProcessor;
import kz.nearbygems.kafka.lib.listener.ParallelKafkaListenerContainerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableParallelKafka.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class ParallelKafkaAnnotationDrivenConfiguration {

  private final KafkaProperties                              properties;
  private final RecordMessageConverter                       messageConverter;
  private final RecordFilterStrategy<Object, Object>         recordFilterStrategy;
  private final BatchMessageConverter                        batchMessageConverter;
  private final KafkaTemplate<Object, Object>                kafkaTemplate;
  private final KafkaAwareTransactionManager<Object, Object> transactionManager;
  private final ConsumerAwareRebalanceListener               reBalanceListener;
  private final ErrorHandler                                 errorHandler;
  private final BatchErrorHandler                            batchErrorHandler;
  private final CommonErrorHandler                           commonErrorHandler;
  private final AfterRollbackProcessor<Object, Object>       afterRollbackProcessor;
  private final RecordInterceptor<Object, Object>            recordInterceptor;

  public ParallelKafkaAnnotationDrivenConfiguration(KafkaProperties properties,
                                                    ObjectProvider<RecordMessageConverter> messageConverter,
                                                    ObjectProvider<RecordFilterStrategy<Object, Object>> recordFilterStrategy,
                                                    ObjectProvider<BatchMessageConverter> batchMessageConverter,
                                                    ObjectProvider<KafkaTemplate<Object, Object>> kafkaTemplate,
                                                    ObjectProvider<KafkaAwareTransactionManager<Object, Object>> kafkaTransactionManager,
                                                    ObjectProvider<ConsumerAwareRebalanceListener> rebalanceListener,
                                                    ObjectProvider<ErrorHandler> errorHandler,
                                                    ObjectProvider<BatchErrorHandler> batchErrorHandler,
                                                    ObjectProvider<CommonErrorHandler> commonErrorHandler,
                                                    ObjectProvider<AfterRollbackProcessor<Object, Object>> afterRollbackProcessor,
                                                    ObjectProvider<RecordInterceptor<Object, Object>> recordInterceptor) {

    this.properties             = properties;
    this.messageConverter       = messageConverter.getIfUnique();
    this.recordFilterStrategy   = recordFilterStrategy.getIfUnique();
    this.batchMessageConverter  = batchMessageConverter
        .getIfUnique(() -> new BatchMessagingMessageConverter(this.messageConverter));
    this.kafkaTemplate          = kafkaTemplate.getIfUnique();
    this.transactionManager     = kafkaTransactionManager.getIfUnique();
    this.reBalanceListener      = rebalanceListener.getIfUnique();
    this.errorHandler           = errorHandler.getIfUnique();
    this.batchErrorHandler      = batchErrorHandler.getIfUnique();
    this.commonErrorHandler     = commonErrorHandler.getIfUnique();
    this.afterRollbackProcessor = afterRollbackProcessor.getIfUnique();
    this.recordInterceptor      = recordInterceptor.getIfUnique();
  }

  @Bean
  @ConditionalOnMissingBean
  ParallelKafkaListenerContainerFactoryConfigurer parallelKafkaListenerContainerFactoryConfigurer() {
    final var configurer = new ParallelKafkaListenerContainerFactoryConfigurer();
    configurer.setKafkaProperties(properties);
    final var messageConverterToUse = (properties.getListener().getType().equals(KafkaProperties.Listener.Type.BATCH))
                                      ? batchMessageConverter : messageConverter;
    configurer.setMessageConverter(messageConverterToUse);
    configurer.setRecordFilterStrategy(recordFilterStrategy);
    configurer.setReplyTemplate(kafkaTemplate);
    configurer.setTransactionManager(transactionManager);
    configurer.setRebalanceListener(reBalanceListener);
    configurer.setErrorHandler(errorHandler);
    configurer.setBatchErrorHandler(batchErrorHandler);
    configurer.setCommonErrorHandler(commonErrorHandler);
    configurer.setAfterRollbackProcessor(afterRollbackProcessor);
    configurer.setRecordInterceptor(recordInterceptor);
    return configurer;
  }

  @Bean
  @ConditionalOnMissingBean(name = ParallelListenerAnnotationBeanPostProcessor.PARALLEL_KAFKA_LISTENER_CONTAINER)
  ParallelKafkaListenerContainerFactory<?, ?> parallelKafkaListenerContainerFactory(
      ParallelKafkaListenerContainerFactoryConfigurer configurer,
      ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
    final var factory = new ParallelKafkaListenerContainerFactory<>();
    configurer.configure(factory, kafkaConsumerFactory
        .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties())));
    return factory;
  }

}