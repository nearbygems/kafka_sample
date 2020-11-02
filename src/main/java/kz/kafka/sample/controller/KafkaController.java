package kz.kafka.sample.controller;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.register.KafkaProducer;
import kz.kafka.sample.util.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  // region Autowired fields
  @Autowired
  private KafkaProducer kafkaProducer;
  // endregion

  @GetMapping("/produce")
  public void produce(@RequestParam("message") String message) {

    var kafka = new MessageKafka();
    kafka.id = Ids.generateStr();
    kafka.value = message;

    kafkaProducer.sendMessage(kafka);
  }

}
