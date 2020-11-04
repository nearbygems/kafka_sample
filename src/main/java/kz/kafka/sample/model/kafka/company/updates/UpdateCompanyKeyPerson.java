package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.Person;
import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyKeyPerson implements UpdateCompany {

  public Person keyPerson;

  public String path() {
    return CompanyDto.Fields.keyPerson;
  }

  public Person value() {
    return keyPerson;
  }

}
