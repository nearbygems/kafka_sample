package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyName implements UpdateCompany {

  public String name;

  public String path() {
    return CompanyDto.Fields.name;
  }

  public String value() {
    return name;
  }

}
