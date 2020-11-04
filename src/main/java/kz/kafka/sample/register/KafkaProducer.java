package kz.kafka.sample.register;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.model.kafka.company.CompanyKafka;

public interface KafkaProducer {

  void sendMessage(MessageKafka kafka);

  void sendMessage(CompanyKafka kafka);

}
