package org.save.model.entity.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.save.model.dto.portfolio.PerformanceHistoryItemDto;

@Data
@Entity
@Table(name = "performance")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class Performance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal value;
  private BigDecimal change;
  private BigDecimal changesPercentage;;

  @Type(type = "json")
  @Column(columnDefinition = "json")
  private List<PerformanceHistoryItemDto> history = new LinkedList<>();

  @JsonBackReference @OneToOne private Portfolio portfolio;
}
