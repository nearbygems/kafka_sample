package kz.kafka.sample.register;

import kz.kafka.sample.model.kafka.CompanyKafka;

public interface CompanyRegister {

  void sendToKafka(CompanyKafka company);

}
