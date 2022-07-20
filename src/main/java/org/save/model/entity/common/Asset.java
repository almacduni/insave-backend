package org.save.model.entity.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "assets")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String companyName;
  private String ticker;
  private BigDecimal amount;
  private BigDecimal averagePrice;
  private BigDecimal totalPrice;
  private String logoUrl;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "portfolio_id", nullable = false)
  private Portfolio portfolio;
}
