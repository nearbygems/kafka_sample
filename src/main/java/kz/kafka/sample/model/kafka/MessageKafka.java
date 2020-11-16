package kz.kafka.sample.model.kafka;

import kz.kafka.sample.model.mongo.MessageDto;
import kz.kafka.sample.util.Ids;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
public class MessageKafka implements Serializable {

  public String id;
  public String value;
  public Date createdAt;

  public MessageDto toDto() {
    var ret = new MessageDto();
    ret.id = Ids.toObjectId(id);
    ret.value = value;
    ret.createdAt = createdAt;
    return ret;
  }

}
