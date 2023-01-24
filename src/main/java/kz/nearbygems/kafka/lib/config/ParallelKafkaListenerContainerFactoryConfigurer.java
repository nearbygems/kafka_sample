package kz.nearbygems.kafka.lib.config;

import kz.nearbygems.kafka.lib.listener.ParallelKafkaListenerContainerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

import java.time.Duration;

public class ParallelKafkaListenerContainerFactoryConfigurer {

  private KafkaProperties                              properties;
  private MessageConverter                             messageConverter;
  private RecordFilterStrategy<Object, Object>         recordFilterStrategy;
  private KafkaTemplate<Object, Object>                replyTemplate;
  private KafkaAwareTransactionManager<Object, Object> transactionManager;
  private ConsumerAwareRebalanceListener               rebalanceListener;
  private ErrorHandler                                 errorHandler;
  private BatchErrorHandler                            batchErrorHandler;
  private CommonErrorHandler                           commonErrorHandler;
  private AfterRollbackProcessor<Object, Object>       afterRollbackProcessor;
  private RecordInterceptor<Object, Object>            recordInterceptor;

  void setKafkaProperties(KafkaProperties properties) {
    this.properties = properties;
  }

  void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  void setRecordFilterStrategy(RecordFilterStrategy<Object, Object> recordFilterStrategy) {
    this.recordFilterStrategy = recordFilterStrategy;
  }

  void setReplyTemplate(KafkaTemplate<Object, Object> replyTemplate) {
    this.replyTemplate = replyTemplate;
  }

  void setTransactionManager(KafkaAwareTransactionManager<Object, Object> transactionManager) {
    this.transactionManager = transactionManager;
  }

  void setRebalanceListener(ConsumerAwareRebalanceListener rebalanceListener) {
    this.rebalanceListener = rebalanceListener;
  }

  void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  void setBatchErrorHandler(BatchErrorHandler batchErrorHandler) {
    this.batchErrorHandler = batchErrorHandler;
  }

  public void setCommonErrorHandler(CommonErrorHandler commonErrorHandler) {
    this.commonErrorHandler = commonErrorHandler;
  }

  void setAfterRollbackProcessor(AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
    this.afterRollbackProcessor = afterRollbackProcessor;
  }

  void setRecordInterceptor(RecordInterceptor<Object, Object> recordInterceptor) {
    this.recordInterceptor = recordInterceptor;
  }

  public void configure(ParallelKafkaListenerContainerFactory<Object, Object> listenerFactory,
                        ConsumerFactory<Object, Object> consumerFactory) {
    listenerFactory.setConsumerFactory(consumerFactory);
    configureListenerFactory(listenerFactory);
    configureContainer(listenerFactory.getContainerProperties());
  }

  @SuppressWarnings("deprecation")
  private void configureListenerFactory(ParallelKafkaListenerContainerFactory<Object, Object> factory) {

    final var map        = PropertyMapper.get().alwaysApplyingWhenNonNull();
    final var properties = this.properties.getListener();

    map.from(this.messageConverter).to(factory :: setMessageConverter);
    map.from(this.recordFilterStrategy).to(factory :: setRecordFilterStrategy);
    map.from(this.replyTemplate).to(factory :: setReplyTemplate);

    if (properties.getType().equals(KafkaProperties.Listener.Type.BATCH)) {
      factory.setBatchListener(true);
      factory.setBatchErrorHandler(this.batchErrorHandler);
    } else {
      factory.setErrorHandler(this.errorHandler);
    }

    map.from(this.commonErrorHandler).to(factory :: setCommonErrorHandler);
    map.from(this.afterRollbackProcessor).to(factory :: setAfterRollbackProcessor);
    map.from(this.recordInterceptor).to(factory :: setRecordInterceptor);
  }

  @SuppressWarnings("deprecation")
  private void configureContainer(ContainerProperties container) {

    final var map        = PropertyMapper.get().alwaysApplyingWhenNonNull();
    final var properties = this.properties.getListener();

    map.from(properties :: getAckMode).to(container :: setAckMode);
    map.from(properties :: getClientId).to(container :: setClientId);
    map.from(properties :: getAckCount).to(container :: setAckCount);
    map.from(properties :: getAckTime).as(Duration :: toMillis).to(container :: setAckTime);
    map.from(properties :: getPollTimeout).as(Duration :: toMillis).to(container :: setPollTimeout);
    map.from(properties :: getNoPollThreshold).to(container :: setNoPollThreshold);
    map.from(properties.getIdleBetweenPolls()).as(Duration :: toMillis).to(container :: setIdleBetweenPolls);
    map.from(properties :: getIdleEventInterval).as(Duration :: toMillis).to(container :: setIdleEventInterval);
    map.from(properties :: getIdlePartitionEventInterval).as(Duration :: toMillis)
       .to(container :: setIdlePartitionEventInterval);
    map.from(properties :: getMonitorInterval).as(Duration :: getSeconds).as(Number :: intValue)
       .to(container :: setMonitorInterval);
    map.from(properties :: getLogContainerConfig).to(container :: setLogContainerConfig);
    map.from(properties :: isOnlyLogRecordMetadata).to(container :: setOnlyLogRecordMetadata);
    map.from(properties :: isMissingTopicsFatal).to(container :: setMissingTopicsFatal);
    map.from(properties :: isImmediateStop).to(container :: setStopImmediate);
    map.from(this.transactionManager).to(container :: setTransactionManager);
    map.from(this.rebalanceListener).to(container :: setConsumerRebalanceListener);
  }

}
