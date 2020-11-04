package kz.kafka.sample.model.kafka.company.updates;

import kz.kafka.sample.model.City;
import kz.kafka.sample.model.mongo.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyHeadquarters implements UpdateCompany {

  public City headquarters;

  public String path() {
    return CompanyDto.Fields.headquarters;
  }

  public City value() {
    return headquarters;
  }

}
