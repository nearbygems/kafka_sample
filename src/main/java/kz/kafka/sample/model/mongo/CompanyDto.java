package kz.kafka.sample.model.mongo;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

import java.util.Date;

@ToString
@FieldNameConstants
public class CompanyDto {
  public ObjectId id;
  public String name;
  public boolean isRemoved;
  public CompanyType type;
  public Date foundationDate;
  public City headquarters;
  public Person keyPerson;
  public String link;
  public Date happenedAt;
}
