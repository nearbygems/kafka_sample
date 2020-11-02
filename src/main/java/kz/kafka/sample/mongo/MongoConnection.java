package kz.kafka.sample.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class MongoConnection implements InitializingBean {

  private MongoDatabase database;

  public MongoDatabase database() {
    return database;
  }

  @Override
  public void afterPropertiesSet() {

    var pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();

    var mongoClientOptions = MongoClientOptions
      .builder()
      .codecRegistry(fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(pojoCodecProvider)))
      .build();

    var mongoClient = new MongoClient("localhost:27017", mongoClientOptions);

    database = mongoClient.getDatabase("sample");
  }
}
