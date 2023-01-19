package kz.nearbygems.kafka;

import kz.nearbygems.kafka.consumer.PersonConsumer;
import kz.nearbygems.kafka.model.Person;
import kz.nearbygems.kafka.producer.PersonProducer;
import kz.nearbygems.kafka.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@DirtiesContext
@EnableAutoConfiguration
@SpringBootTest(classes = {PersonConsumer.class, PersonProducer.class})
public class PersonKafkaTest {

  @MockBean
  private PersonService service;

  @Autowired
  private PersonProducer producer;

  @Test
  public void test() throws InterruptedException {

    Stream.iterate(1, n -> n + 1)
          .limit(10)
          .map(String :: valueOf)
          .map(id -> new Person(id, "nearbygems", "Bergen", "Asym"))
          .forEach(person -> producer.send(person));

    Thread.sleep(1000);

    verify(service).save(anyList());
  }

}
