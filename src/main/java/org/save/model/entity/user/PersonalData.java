package org.save.model.entity.user;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.entity.common.User;
import org.save.model.entity.social.picture.Picture;
import org.save.model.enums.DocumentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personal_data", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class PersonalData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String streetAddress;

  @Column(nullable = false)
  private String postCode;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String date;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DocumentType documentType;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Picture> pictures;

  @Column private String tickersSearchHistory;
}
