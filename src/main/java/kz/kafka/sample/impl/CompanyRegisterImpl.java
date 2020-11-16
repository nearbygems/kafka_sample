package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.CompanyKafka;
import kz.kafka.sample.register.CompanyRegister;
import kz.kafka.sample.register.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyRegisterImpl implements CompanyRegister {

  // region Autowired fields
  @Autowired
  private KafkaProducer kafkaProducer;
  // endregion

  @Override
  public void sendToKafka(CompanyKafka company) {
    kafkaProducer.sendCompany(company);
  }

}
