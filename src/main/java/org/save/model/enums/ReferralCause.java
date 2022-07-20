package org.save.model.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReferralCause {
  FOR_CREATE_TRENDING_POST(BigDecimal.valueOf(0.5)),
  FOR_REGISTRY(BigDecimal.valueOf(0.05)),
  FOR_REFERRAL_REGISTRY(BigDecimal.valueOf(0.05)),
  FOR_CRYPTO_TRADE_ORDER(BigDecimal.valueOf(0.01));

  @Getter private final BigDecimal amount;
}
