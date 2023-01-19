package kz.nearbygems.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  public String id;
  public String username;
  public String firstName;
  public String lastName;

}
