package kz.nearbygems.kafka.service.impl;

import kz.nearbygems.kafka.model.Person;
import kz.nearbygems.kafka.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

  @Override
  public void save(List<Person> persons) {

    persons.stream()
           .map(Person :: getUsername)
           .map(username -> "Saved person with username [" + username + "]")
           .forEach(System.out :: println);
  }

}
