package kz.kafka.sample.mongo;

import com.mongodb.client.MongoCollection;
import kz.kafka.sample.model.mongo.MessageDto;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoAccess implements InitializingBean {

  // region Autowired fields
  @Autowired
  private MongoConnection mongoConnection;
  // endregion

  private MongoCollection<MessageDto> messages;

  @Override
  public void afterPropertiesSet() {
    messages = getCollection(MessageDto.class);
  }

  private <T> MongoCollection<T> getCollection(Class<T> aClass) {
    return mongoConnection.database().getCollection(aClass.getSimpleName(), aClass);
  }

  public MongoCollection<MessageDto> messages() {
    return messages;
  }
}
