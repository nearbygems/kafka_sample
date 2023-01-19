package kz.nearbygems.kafka.producer;

import kz.nearbygems.kafka.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonProducer {

  @Value("${kafka.topic}")
  private String topic;

  private final KafkaTemplate<String, Person> template;

  public void send(Person person) {
    template.send(topic, person);
  }

}
