package kz.kafka.sample.model.kafka.company;

import lombok.ToString;

import java.util.Date;

@ToString
public abstract class CompanyKafka {
  public String id;
  public Date happenedAt;
}
