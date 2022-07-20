package org.save.repo.playlist;

import org.save.model.entity.social.playlist.PlaylistTrendingPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistTopRepository extends JpaRepository<PlaylistTrendingPoints, Long> {}
