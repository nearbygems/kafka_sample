package kz.kafka.sample.register;

public interface MessageRegister {

  void sendToKafka(String message);
}
