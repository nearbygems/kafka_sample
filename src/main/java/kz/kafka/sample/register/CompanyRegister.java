package kz.kafka.sample.register;

import kz.kafka.sample.model.web.Company;

public interface CompanyRegister {

  void sendToKafka(Company company);

}
