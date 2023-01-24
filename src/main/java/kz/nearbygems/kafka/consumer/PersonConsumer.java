package kz.nearbygems.kafka.consumer;

import kz.nearbygems.kafka.lib.annotation.ParallelListener;
import kz.nearbygems.kafka.model.Person;
import kz.nearbygems.kafka.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonConsumer {

  private final PersonService service;

  @ParallelListener(topics = "${kafka.topic}",
                    groupId = "${kafka.group}",
                    autoStartup = "${kafka.startup}",
                    properties = {
                        "auto.offset.reset=earliest",
                        "max.poll.records=1",
                        "fetch.min.bytes=1",
                        "max.poll.interval.ms=1000"
                    })
  public void consume(List<Person> persons) {
    service.save(persons);
  }

}
