package kz.kafka.sample.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kafka.sample.model.kafka.CompanyKafka;
import kz.kafka.sample.model.kafka.MessageKafka;
import lombok.SneakyThrows;

import java.util.Objects;

public class Serializer {

  public static final ObjectMapper mapper = new ObjectMapper();

  @SneakyThrows
  public static String toString(Object object) {
    return mapper.writeValueAsString(object);
  }

  @SneakyThrows
  public static MessageKafka parseMessageKafka(String message) {
    if (isNullOrEmpty(message)) { return null; }
    return mapper.readValue(message.getBytes(), MessageKafka.class);
  }

  @SneakyThrows
  public static CompanyKafka parseCompanyKafka(String company) {
    if (isNullOrEmpty(company)) { return null; }
    return mapper.readValue(company.getBytes(), CompanyKafka.class);
  }

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty() || str.isBlank() || Objects.equals(str, "null");
  }

}