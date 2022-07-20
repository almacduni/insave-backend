package org.save.model.entity.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.Currency;

@Data
@Entity
@Table(name = "wallets")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "accounts")
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal amount; // balance

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private Currency currency;

  @JsonBackReference
  @OneToOne(cascade = CascadeType.ALL)
  private User user;

  @JsonBackReference
  @OneToMany(
      cascade = {CascadeType.ALL},
      mappedBy = "wallet")
  private List<WalletAccount> accounts;

  private String customerId; // inner user_id for WalletAccounts

  public Wallet(BigDecimal amount, Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public void addAccountToWallet(WalletAccount walletAccount) {
    if (accounts == null) {
      accounts = new ArrayList<>();
    }
    accounts.add(walletAccount);
    walletAccount.setWallet(this);
  }
}
