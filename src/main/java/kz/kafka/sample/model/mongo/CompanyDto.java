package kz.kafka.sample.model.mongo;

import com.mongodb.client.model.Updates;
import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public List<Bson> updates() {
    return Stream.of(
      Updates.set(CompanyDto.Fields.name, name),
      Updates.set(CompanyDto.Fields.isRemoved, isRemoved),
      Updates.set(CompanyDto.Fields.type, type),
      Updates.set(CompanyDto.Fields.foundationDate, foundationDate),
      Updates.set(CompanyDto.Fields.headquarters, headquarters),
      Updates.set(CompanyDto.Fields.keyPerson, keyPerson),
      Updates.set(CompanyDto.Fields.link, link),
      Updates.set(CompanyDto.Fields.happenedAt, happenedAt))
      .collect(Collectors.toList());
  }
}
