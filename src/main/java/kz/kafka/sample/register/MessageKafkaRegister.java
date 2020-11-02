package kz.kafka.sample.register;

import kz.kafka.sample.model.kafka.MessageKafka;

public interface MessageKafkaRegister {

  void comeCreateMessageKafka(MessageKafka kafka);

}
