package kz.kafka.sample.model.kafka;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import kz.kafka.sample.model.mongo.CompanyDto;
import kz.kafka.sample.util.Ids;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
public class CompanyKafka implements Serializable {

  public String id;
  public String name;
  public CompanyType type;
  public Date foundationDate;
  public City headquarters;
  public Person keyPerson;
  public String link;
  public boolean isRemoved;

  public CompanyDto toDto() {
    var ret = new CompanyDto();
    ret.id = Ids.toObjectId(id);
    ret.name = name;
    ret.type = type;
    ret.foundationDate = foundationDate;
    ret.headquarters = headquarters;
    ret.keyPerson = keyPerson;
    ret.link = link;
    ret.isRemoved = isRemoved;
    return ret;
  }

}
