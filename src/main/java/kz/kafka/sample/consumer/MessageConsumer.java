package kz.kafka.sample.consumer;

import kz.kafka.sample.register.MessageKafkaRegister;
import kz.kafka.sample.util.JsonWorker;
import kz.kafka.sample.util.KafkaTopic;
import kz.kafka.sample.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

  // region Autowired fields
  @Autowired
  private MessageKafkaRegister messageKafkaRegister;
  // endregion

  @KafkaListener(topics = KafkaTopic.MESSAGE, groupId = KafkaUtil.GROUP_ID)
  public void listen(String message) {
    messageKafkaRegister.comeCreateMessageKafka(JsonWorker.parseMessageKafka(message));
  }

}
