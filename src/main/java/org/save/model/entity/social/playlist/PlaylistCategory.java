package org.save.model.entity.social.playlist;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(exclude = "playlists")
@ToString(exclude = "playlists")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "playlist_categories")
public class PlaylistCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  private int serialNumber;

  private String category;

  @JsonManagedReference
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "playlist_category",
      joinColumns = {@JoinColumn(name = "category_id")},
      inverseJoinColumns = {@JoinColumn(name = "playlist_id")})
  private List<Playlist> playlists;
}
