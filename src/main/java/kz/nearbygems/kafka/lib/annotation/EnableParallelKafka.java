package kz.nearbygems.kafka.lib.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ParallelKafkaListenerConfigurationSelector.class)
public @interface EnableParallelKafka {
}
