package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.mongo.MongoAccess;
import kz.kafka.sample.register.MessageKafkaRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageKafkaRegisterImpl implements MessageKafkaRegister {

  // region Autowired fields
  @Autowired
  private MongoAccess mongoAccess;
  // endregion

  @Override
  public void comeCreateMessageKafka(MessageKafka kafka) {
    mongoAccess.messages().insertOne(kafka.toDto());
    System.out.println("Done");
  }
}
