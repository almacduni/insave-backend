package org.save.model.entity.common;

import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

  private Long id;
  private String ticker;

  @Column(name = "bought_date")
  private Date boughtDate;

  private String industry;
  private String country;
  private String phone;
  private String url;
  private String name;
  private String logo;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  private double shareOutstanding;
  private String description;
}
