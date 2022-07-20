package org.save.model.entity.watchlist;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.save.model.entity.common.User;

@Data
@Entity
@Table(name = "watchlists")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watchlist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "watchlist_id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Type(type = "json")
  @Column(name = "tickers", columnDefinition = "json")
  private List<String> tickers;
}
