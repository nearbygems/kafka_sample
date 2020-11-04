package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.company.CreateCompanyKafka;
import kz.kafka.sample.model.kafka.company.UpdateCompanyKafka;
import kz.kafka.sample.model.web.Company;
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
  public void sendToKafka(Company company) {

    if (company.id == null) {
      createCompany(company);
    } else {
      updateCompany(company);
    }

  }

  private void createCompany(Company company) {

    var create = CreateCompanyKafka.of(company);

    kafkaProducer.sendMessage(create);

  }

  private void updateCompany(Company company) {

    var update = UpdateCompanyKafka.of(company.id);

    update.updateName(company.name);
    update.updateHeadquarters(company.headquarters);
    update.updateIsRemoved(company.isRemoved);
    update.updateKeyPerson(company.keyPerson);
    update.updateLink(company.link);
    update.updateType(company.type);

    kafkaProducer.sendMessage(update);

  }

}
