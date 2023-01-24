package kz.nearbygems.kafka.lib.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.validation.Validator;

import java.util.List;

public class ParallelKafkaNullAwarePayloadArgumentResolver extends PayloadMethodArgumentResolver {

  ParallelKafkaNullAwarePayloadArgumentResolver(MessageConverter messageConverter, Validator validator) {
    super(messageConverter, validator);
  }

  @Override
  public Object resolveArgument(@NonNull MethodParameter parameter,
                                @NonNull Message<?> message) throws Exception {
    final var resolved = super.resolveArgument(parameter, message);
    if (resolved instanceof List) {
      final var list = ((List<?>) resolved);
      for (var i = 0; i < list.size(); i++) {
        if (list.get(i) instanceof KafkaNull) {
          list.set(i, null);
        }
      }
    }
    return resolved;
  }

  @Override
  protected boolean isEmptyPayload(Object payload) {
    return payload == null || payload instanceof KafkaNull;
  }

}
