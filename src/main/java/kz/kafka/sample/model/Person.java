package kz.kafka.sample.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@AllArgsConstructor
public class Person implements Serializable {
  public String surname;
  public String name;
}
