package kz.kafka.sample.model.kafka.company;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import kz.kafka.sample.model.mongo.CompanyDto;
import kz.kafka.sample.model.web.Company;
import kz.kafka.sample.util.Ids;
import lombok.ToString;

import java.util.Date;

@ToString
public class CreateCompanyKafka extends CompanyKafka {

  public boolean isRemoved;
  public String name;
  public CompanyType type;
  public Date foundationDate;
  public City headquarters;
  public Person keyPerson;
  public String link;

  public static CreateCompanyKafka of(Company company) {
    var ret = new CreateCompanyKafka();
    ret.id = Ids.generateStr();
    ret.name = company.name;
    ret.type = company.type;
    ret.foundationDate = company.foundationDate;
    ret.headquarters = company.headquarters;
    ret.keyPerson = company.keyPerson;
    ret.link = company.link;
    ret.happenedAt = new Date();
    return ret;
  }

  public CompanyDto toDto() {
    var ret = new CompanyDto();
    ret.id = Ids.toObjectId(id);
    ret.name = name;
    ret.type = type;
    ret.foundationDate = foundationDate;
    ret.headquarters = headquarters;
    ret.keyPerson = keyPerson;
    ret.link = link;
    ret.happenedAt = happenedAt;
    return ret;
  }

}
