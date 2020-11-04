package kz.kafka.sample.consumer;

import kz.kafka.sample.model.kafka.company.CreateCompanyKafka;
import kz.kafka.sample.model.kafka.company.UpdateCompanyKafka;
import kz.kafka.sample.register.KafkaRegister;
import kz.kafka.sample.util.JsonWorker;
import kz.kafka.sample.util.KafkaTopic;
import kz.kafka.sample.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

  // region Autowired fields
  @Autowired
  private KafkaRegister kafkaRegister;
  // endregion

  @KafkaListener(topics = KafkaTopic.MESSAGE, groupId = KafkaUtil.GROUP_ID)
  public void listenMessages(String message) {
    kafkaRegister.comeCreateMessageKafka(JsonWorker.parseMessageKafka(message));
  }

  @KafkaListener(topics = KafkaTopic.COMPANY, groupId = KafkaUtil.GROUP_ID)
  public void listenCompanies(String company) {

    var kafka = JsonWorker.parseCompanyKafka(company);

    if (kafka instanceof CreateCompanyKafka) {
      kafkaRegister.comeCreateCompanyKafka((CreateCompanyKafka) kafka);
    }

    if (kafka instanceof UpdateCompanyKafka) {
      kafkaRegister.comeUpdateCompanyKafka((UpdateCompanyKafka) kafka);
    }

  }

}
