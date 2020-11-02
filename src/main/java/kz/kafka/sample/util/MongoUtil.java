package kz.kafka.sample.util;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import java.util.Optional;

public class MongoUtil {

  public static <T> Optional<T> one(MongoIterable<T> iterable) {
    try (MongoCursor<T> iterator = iterable.iterator()) {
      if (iterator.hasNext()) {
        Optional<T> ret = Optional.ofNullable(iterator.next());
        if (iterator.hasNext()) {
          throw new RuntimeException();
        }
        return ret;
      } else {
        return Optional.empty();
      }
    }
  }

}
