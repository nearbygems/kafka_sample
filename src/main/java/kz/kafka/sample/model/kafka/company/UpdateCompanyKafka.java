package kz.kafka.sample.model.kafka.company;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import kz.kafka.sample.model.kafka.company.updates.*;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ToString
public class UpdateCompanyKafka extends CompanyKafka {

  public List<UpdateCompany> updates = new ArrayList<>();

  public static UpdateCompanyKafka of(String id) {
    var ret = new UpdateCompanyKafka();
    ret.id = id;
    ret.happenedAt = new Date();
    return ret;
  }

  public UpdateCompanyKafka updateIsRemoved(Boolean removed) {
    if (removed != null) {
      updates.add(new UpdateCompanyIsRemoved(removed));
    }
    return this;
  }

  public UpdateCompanyKafka updateHeadquarters(City headquarters) {
    updates.add(new UpdateCompanyHeadquarters(headquarters));
    return this;
  }

  public UpdateCompanyKafka updateKeyPerson(Person keyPerson) {
    updates.add(new UpdateCompanyKeyPerson(keyPerson));
    return this;
  }

  public UpdateCompanyKafka updateLink(String link) {
    updates.add(new UpdateCompanyLink(link));
    return this;
  }

  public UpdateCompanyKafka updateName(String name) {
    updates.add(new UpdateCompanyName(name));
    return this;
  }

  public UpdateCompanyKafka updateType(CompanyType type) {
    updates.add(new UpdateCompanyType(type));
    return this;
  }

}
