package kz.kafka.sample.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kafka.sample.model.kafka.MessageKafka;

import java.util.Objects;

public class JsonWorker {

  private final static ObjectMapper mapper = new ObjectMapper();

  public static String toJson(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static MessageKafka parseMessageKafka(String json) {
    if (isNullOrEmpty(json)) {
      return null;
    }

    try {
      return mapper.readValue(json, MessageKafka.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isNullOrEmpty(String str) {
    if (str == null || str.isEmpty() || str.isBlank() || Objects.equals(str, "null")) {
      return true;
    }
    return false;
  }

}