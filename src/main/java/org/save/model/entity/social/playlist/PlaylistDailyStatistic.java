package org.save.model.entity.social.playlist;

import java.time.LocalDate;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * <class>PlaylistTrending</class> keeps info about number of openings of playlists and dates when
 * they were made
 */
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Embeddable
@Data
public class PlaylistDailyStatistic {

  @NonNull private LocalDate day;

  @EqualsAndHashCode.Exclude private Long tapsCount;

  @PrePersist
  public void setDay() {
    this.day = LocalDate.now();
  }
}
