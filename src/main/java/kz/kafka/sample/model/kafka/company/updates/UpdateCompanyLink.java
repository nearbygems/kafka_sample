package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyLink implements UpdateCompany {

  public String link;

  public String path() {
    return CompanyDto.Fields.link;
  }

  public String value() {
    return link;
  }

}