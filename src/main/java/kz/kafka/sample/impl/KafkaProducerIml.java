package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.CompanyKafka;
import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.register.KafkaProducer;
import kz.kafka.sample.util.KafkaTopic;
import kz.kafka.sample.util.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerIml implements KafkaProducer {

  // region Autowired fields
  @Autowired
  private KafkaTemplate<String, String> template;
  // endregion

  private void send(String topic, String message) {
    template.send(topic, message);
  }

  @Override
  public void sendMessage(MessageKafka kafka) { send(KafkaTopic.MESSAGE, Serializer.toString(kafka)); }

  @Override
  public void sendCompany(CompanyKafka kafka) {
    send(KafkaTopic.COMPANY, Serializer.toString(kafka));
  }

}
