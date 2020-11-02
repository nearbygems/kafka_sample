package kz.kafka.sample.register;

import kz.kafka.sample.model.kafka.MessageKafka;

public interface KafkaProducer {

  void sendMessage(MessageKafka kafka);

}
