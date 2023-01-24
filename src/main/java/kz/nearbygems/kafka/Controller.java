package kz.nearbygems.kafka;

import kz.nearbygems.kafka.model.Person;
import kz.nearbygems.kafka.producer.PersonProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("person")
public class Controller {

  private final PersonProducer producer;

  @PostMapping
  public void send(@RequestBody Person person) {
    producer.send(person);
  }

}
