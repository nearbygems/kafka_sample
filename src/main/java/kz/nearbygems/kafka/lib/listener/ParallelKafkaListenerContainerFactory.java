package kz.nearbygems.kafka.lib.listener;

import kz.nearbygems.kafka.lib.listener.ParallelKafkaMessageListenerContainer;
import org.springframework.kafka.config.AbstractKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Objects;

public class ParallelKafkaListenerContainerFactory<K, V>
    extends AbstractKafkaListenerContainerFactory<ParallelKafkaMessageListenerContainer<K, V>, K, V> {

  @Override
  protected @NonNull ParallelKafkaMessageListenerContainer<K, V> createContainerInstance(
      KafkaListenerEndpoint endpoint) {
    TopicPartitionOffset[] topicPartitions = endpoint.getTopicPartitionsToAssign();
    if (topicPartitions != null && topicPartitions.length > 0) {
      ContainerProperties properties = new ContainerProperties(topicPartitions);
      return new ParallelKafkaMessageListenerContainer<>(getConsumerFactory(), properties);
    } else {
      Collection<String>  topics = endpoint.getTopics();
      ContainerProperties properties;
      if (!topics.isEmpty()) {
        properties = new ContainerProperties(topics.toArray(new String[0]));
      } else {
        properties = new ContainerProperties(Objects.requireNonNull(endpoint.getTopicPattern()));
      }
      return new ParallelKafkaMessageListenerContainer<>(getConsumerFactory(), properties);
    }
  }

  @Override
  protected void initializeContainer(@NonNull ParallelKafkaMessageListenerContainer<K, V> instance,
                                     @NonNull KafkaListenerEndpoint endpoint) {

    super.initializeContainer(instance, endpoint);
  }

}