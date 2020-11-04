package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyIsRemoved implements UpdateCompany {

  public boolean isRemoved;

  public String path() {
    return CompanyDto.Fields.isRemoved;
  }

  public boolean value() {
    return isRemoved;
  }

}
