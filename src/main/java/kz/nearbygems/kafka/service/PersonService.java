package kz.nearbygems.kafka.service;

import kz.nearbygems.kafka.model.Person;

import java.util.List;

public interface PersonService {

  void save(List<Person> persons);

}
