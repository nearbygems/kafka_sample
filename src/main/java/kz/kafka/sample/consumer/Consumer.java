package kz.kafka.sample.consumer;

import kz.kafka.sample.register.KafkaRegister;
import kz.kafka.sample.util.KafkaTopic;
import kz.kafka.sample.util.KafkaUtil;
import kz.kafka.sample.util.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Consumer {

  // region Autowired fields
  @Autowired
  private KafkaRegister kafkaRegister;
  // endregion

  @KafkaListener(topics = KafkaTopic.MESSAGE, groupId = KafkaUtil.GROUP_ID)
  public void listenMessages(String message) {

    var kafka = Objects.requireNonNull(Serializer.parseMessageKafka(message));

    kafkaRegister.comeCreateMessage(kafka.toDto());
  }

  @KafkaListener(topics = KafkaTopic.COMPANY, groupId = KafkaUtil.GROUP_ID)
  public void listenCompanies(String company) {

    var kafka = Objects.requireNonNull(Serializer.parseCompanyKafka(company));

    if (kafka.id == null) {
      kafkaRegister.comeCreateCompany(kafka.toDto());
    } else {
      kafkaRegister.comeUpdateCompany(kafka.toDto());
    }

  }

}
