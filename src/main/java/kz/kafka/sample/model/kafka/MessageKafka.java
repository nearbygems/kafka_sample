package kz.kafka.sample.model.kafka;

import kz.kafka.sample.model.mongo.MessageDto;
import kz.kafka.sample.util.Ids;

public class MessageKafka {
  public String id;
  public String value;

  public MessageDto toDto() {
    var ret = new MessageDto();
    ret.id = Ids.toObjectId(id);
    ret.value = value;
    return ret;
  }
}
