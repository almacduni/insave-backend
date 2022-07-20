package org.save.repo.playlist;

import org.save.model.entity.social.playlist.PlaylistCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistCategoryRepository extends JpaRepository<PlaylistCategory, Long> {

  PlaylistCategory findByCategory(String category);

  boolean existsByCategory(String category);
}
