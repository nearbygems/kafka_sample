package kz.kafka.sample.register;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.model.kafka.company.CreateCompanyKafka;
import kz.kafka.sample.model.kafka.company.UpdateCompanyKafka;

public interface KafkaRegister {

  void comeCreateMessageKafka(MessageKafka kafka);

  void comeCreateCompanyKafka(CreateCompanyKafka kafka);

  void comeUpdateCompanyKafka(UpdateCompanyKafka kafka);

}
