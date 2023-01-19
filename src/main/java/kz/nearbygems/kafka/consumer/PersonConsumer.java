package kz.nearbygems.kafka.consumer;

import kz.nearbygems.kafka.model.Person;
import kz.nearbygems.kafka.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonConsumer {

  private final PersonService service;

  @KafkaListener(topics = "${kafka.topic}",
                 groupId = "${kafka.group}",
                 autoStartup = "${kafka.startup}",
                 batch = "true",
                 properties = {
                     "auto.offset.reset=earliest",
                     "max.poll.records=10",
                     "fetch.min.bytes=100",
                     "max.poll.interval.ms=1000",
                     "enable.auto.commit=true"
                 })
  public void consume(List<Person> persons) {
    service.save(persons);
  }

}
