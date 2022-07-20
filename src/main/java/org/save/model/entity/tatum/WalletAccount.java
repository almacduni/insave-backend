package org.save.model.entity.tatum;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import lombok.Data;
import org.save.model.entity.common.Wallet;
import org.save.model.enums.CryptoCurrency;

@Entity
@Data
@Table(name = "cryptowallet_accounts")
public class WalletAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private CryptoCurrency cryptoCurrency;
  private String address;
  private String ledgerAccountId;
  private Integer index;

  @JsonBackReference
  @ManyToOne(
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "wallet_id")
  private Wallet wallet;
}
