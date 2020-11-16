package kz.kafka.sample.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@AllArgsConstructor
public class City implements Serializable {
  public String name;
  public String country;
}
