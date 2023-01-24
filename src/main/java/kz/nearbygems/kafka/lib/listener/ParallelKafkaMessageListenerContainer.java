package kz.nearbygems.kafka.lib.listener;

import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.lang.NonNull;

import java.util.*;

import static io.confluent.parallelconsumer.ParallelConsumerOptions.CommitMode;
import static io.confluent.parallelconsumer.ParallelConsumerOptions.ProcessingOrder;

public class ParallelKafkaMessageListenerContainer<K, V> extends AbstractMessageListenerContainer<K, V> {

  private ParallelStreamProcessor<K, V> parallelConsumer;

  public ParallelKafkaMessageListenerContainer(ConsumerFactory<? super K, ? super V> consumerFactory,
                                               ContainerProperties containerProperties) {

    super(consumerFactory, containerProperties);
  }

  @Override
  public @NonNull Map<String, Map<MetricName, ? extends Metric>> metrics() {
    return Collections.emptyMap();
  }

  private Properties propertiesFromProperties() {
    Properties propertyOverrides = getContainerProperties().getKafkaConsumerProperties();
    Properties props             = new Properties();
    props.putAll(propertyOverrides);
    Set<String> stringPropertyNames = propertyOverrides.stringPropertyNames();
    stringPropertyNames.forEach((name) -> {
      if (!props.contains(name)) {
        props.setProperty(name, propertyOverrides.getProperty(name));
      }
    });
    return props;
  }

  @Override
  protected void doStart() {

    if (isRunning()) {
      return;
    }

    if (isAutoStartup()) {

      final var listener = (BatchMessageListener<K, V>) getContainerProperties().getMessageListener();

      final var consumerProperties = propertiesFromProperties();
      consumerProperties.put("enable.auto.commit", "false");

      final var consumer = consumerFactory.createConsumer(getGroupId(),
                                                          getContainerProperties().getClientId(),
                                                          null,
                                                          consumerProperties);

      final var options = ParallelConsumerOptions.<K, V>builder()
                                                 .ordering(ProcessingOrder.KEY)
                                                 .maxConcurrency(16)
                                                 .consumer(consumer)
                                                 .commitMode(CommitMode.PERIODIC_CONSUMER_SYNC)
                                                 .build();

      parallelConsumer = ParallelStreamProcessor.createEosStreamProcessor(options);

      parallelConsumer.subscribe(Arrays.asList(Objects.requireNonNull(getContainerProperties().getTopics())));

      parallelConsumer.poll(context -> {
        final var records = context.getConsumerRecordsFlattened();
        listener.onMessage(records, null, consumer);
      });

      setRunning(true);
    }

  }

  @Override
  protected void doStop(final Runnable callback, boolean normal) {
    if (isRunning()) {
      parallelConsumer.close();
    }
  }

  @Override
  public String toString() {
    return "ParallelKafkaMessageListenerContainer [id=" + getBeanName() + "]";
  }

}
