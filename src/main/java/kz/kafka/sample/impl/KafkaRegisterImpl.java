package kz.kafka.sample.impl;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import kz.kafka.sample.model.mongo.CompanyDto;
import kz.kafka.sample.model.mongo.MessageDto;
import kz.kafka.sample.mongo.MongoAccess;
import kz.kafka.sample.register.KafkaRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaRegisterImpl implements KafkaRegister {

  // region Autowired fields
  @Autowired
  private MongoAccess mongoAccess;
  // endregion

  @Override
  public void comeCreateMessage(MessageDto message) {
    mongoAccess.messages().insertOne(message);
  }

  @Override
  public void comeCreateCompany(CompanyDto company) {
    mongoAccess.companies().insertOne(company);
  }

  @Override
  public void comeUpdateCompany(CompanyDto company) {
    mongoAccess.companies().updateOne(
      Filters.eq("_id", company.id),
      Updates.combine(company.updates()));
  }

}
