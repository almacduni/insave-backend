package org.save.model.entity.social.post;

import java.time.LocalDate;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Danik
 * @create 05.09.2021-11:07 class <code>PostDailyStatistic</code> is needed to save statistic about
 *     number of openings, likes, comments within a specified number of days
 */
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Embeddable
@Data
public class PostDailyStatistic {

  @NonNull private LocalDate day;

  @EqualsAndHashCode.Exclude private Integer openings;

  @EqualsAndHashCode.Exclude private Integer likesCount;

  @EqualsAndHashCode.Exclude private Integer commentsCount;
}
