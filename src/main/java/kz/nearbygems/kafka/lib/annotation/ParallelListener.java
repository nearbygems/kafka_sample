package kz.nearbygems.kafka.lib.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ParallelListener {

  String id() default "";

  String[] topics() default {};

  String groupId() default "";

  String autoStartup() default "";

  String[] properties() default {};

}
