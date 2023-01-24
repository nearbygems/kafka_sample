package kz.nearbygems.kafka;

import kz.nearbygems.kafka.lib.annotation.EnableParallelKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableParallelKafka
@SpringBootApplication
public class KafkaSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaSampleApplication.class, args);
  }

}
