package kz.kafka.sample.impl;

import kz.kafka.sample.model.kafka.MessageKafka;
import kz.kafka.sample.model.kafka.company.CreateCompanyKafka;
import kz.kafka.sample.model.kafka.company.UpdateCompanyKafka;
import kz.kafka.sample.model.kafka.company.updates.*;
import kz.kafka.sample.mongo.MongoAccess;
import kz.kafka.sample.register.KafkaRegister;
import kz.kafka.sample.util.Ids;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static java.util.stream.Collectors.toList;

@Component
public class KafkaRegisterImpl implements KafkaRegister {

  // region Autowired fields
  @Autowired
  private MongoAccess mongoAccess;
  // endregion

  @Override
  public void comeCreateMessageKafka(MessageKafka kafka) {
    mongoAccess.messages().insertOne(kafka.toDto());
  }

  @Override
  public void comeCreateCompanyKafka(CreateCompanyKafka kafka) {
    mongoAccess.companies().insertOne(kafka.toDto());
  }

  @Override
  public void comeUpdateCompanyKafka(UpdateCompanyKafka kafka) {

    List<Bson> updates = kafka
      .updates
      .stream()
      .flatMap(this::toStreamBson)
      .collect(toList());

    if (updates.isEmpty()) {
      return;
    }

    mongoAccess.companies().updateOne(eq("_id", Ids.toObjectId(kafka.id)), combine(updates));

  }

  private Stream<Bson> toStreamBson(UpdateCompany update) {

    if (update instanceof UpdateCompanyIsRemoved) {
      var u = (UpdateCompanyIsRemoved) update;
      return Stream.of(set(u.path(), u.value()));
    }

    if (update instanceof UpdateCompanyHeadquarters) {
      var u = (UpdateCompanyHeadquarters) update;
      return Stream.of(set(u.path(), u.value()));
    }

    if (update instanceof UpdateCompanyKeyPerson) {
      var u = (UpdateCompanyKeyPerson) update;
      return Stream.of(set(u.path(), u.value()));
    }

    if (update instanceof UpdateCompanyLink) {
      var u = (UpdateCompanyLink) update;
      return Stream.of(set(u.path(), u.value()));
    }

    if (update instanceof UpdateCompanyName) {
      var u = (UpdateCompanyName) update;
      return Stream.of(set(u.path(), u.value()));
    }

    if (update instanceof UpdateCompanyType) {
      var u = (UpdateCompanyType) update;
      return Stream.of(set(u.path(), u.value()));
    }

    throw new IllegalArgumentException("gxoh2cz5zc :: Cannot convert ot BSON " + update.getClass());
  }

}
