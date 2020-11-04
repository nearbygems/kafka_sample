package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.register.KafkaProducer;
import kz.kafka.sample.register.MessageRegister;
import kz.kafka.sample.util.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MessageRegisterImpl implements MessageRegister {

  // region Autowired fields
  @Autowired
  private KafkaProducer kafkaProducer;
  // endregion

  @Override
  public void sendToKafka(String message) {

    var kafka = new MessageKafka();
    kafka.id = Ids.generateStr();
    kafka.value = message;
    kafka.createdAt = new Date();

    kafkaProducer.sendMessage(kafka);

  }

}
