package kz.kafka.sample.controller;

import kz.kafka.sample.consumer.MyTopicConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  @Autowired
  private KafkaTemplate<String, String> template;

  @Autowired
  private MyTopicConsumer myTopicConsumer;

  @GetMapping("/produce")
  public void produce(@RequestParam("message") String message) {
    template.send("myTopic", message);
  }

  @GetMapping("/messages")
  public List<String> getMessages() {
    return myTopicConsumer.getMessages();
  }

}
