package kz.kafka.sample.controller;

import kz.kafka.sample.model.kafka.CompanyKafka;
import kz.kafka.sample.register.CompanyRegister;
import kz.kafka.sample.register.MessageRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  // region Autowired fields
  @Autowired
  private MessageRegister messageRegister;

  @Autowired
  private CompanyRegister companyRegister;
  // endregion

  @PostMapping("/message")
  public void produce(@RequestParam("message") String message) {
    messageRegister.sendToKafka(message);
  }

  @PostMapping("/company")
  public void produce(@RequestBody CompanyKafka company) {
    companyRegister.sendToKafka(company);
  }

}
