package kz.kafka.sample.model.web;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.Person;
import lombok.ToString;

import java.util.Date;

@ToString
public class Company {
  public String id;
  public String name;
  public CompanyType type;
  public Date foundationDate;
  public City headquarters;
  public Person keyPerson;
  public String link;
  public boolean isRemoved;
}
