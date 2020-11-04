package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.CompanyType;
import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyType implements UpdateCompany {

  public CompanyType type;

  public String path() {
    return CompanyDto.Fields.type;
  }

  public CompanyType value() {
    return type;
  }

}
