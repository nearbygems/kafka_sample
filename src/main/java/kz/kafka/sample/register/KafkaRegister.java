package kz.kafka.sample.register;

import kz.kafka.sample.model.mongo.CompanyDto;
import kz.kafka.sample.model.mongo.MessageDto;

public interface KafkaRegister {

  void comeCreateMessage(MessageDto kafka);

  void comeCreateCompany(CompanyDto kafka);

  void comeUpdateCompany(CompanyDto kafka);

}
