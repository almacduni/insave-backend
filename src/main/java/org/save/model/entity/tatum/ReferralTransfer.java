package org.save.model.entity.tatum;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "referral_transfers")
public class ReferralTransfer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String date;

  private BigDecimal amount;

  private Long userId;

  private String reference;

  private String cause; // ReferralCause enum

  public ReferralTransfer(
      String date, BigDecimal amount, Long userId, String reference, String cause) {
    this.date = date;
    this.amount = amount;
    this.userId = userId;
    this.reference = reference;
    this.cause = cause;
  }
}
