package kz.kafka.sample.model.mongo;

import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

@ToString
@FieldNameConstants
public class MessageDto {
  public ObjectId id;
  public String value;
}
