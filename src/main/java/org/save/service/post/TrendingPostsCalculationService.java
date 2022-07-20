package org.save.service.post;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.entity.social.post.Post;
import org.save.model.entity.social.post.PostTrending;
import org.save.model.enums.ReferralCause;
import org.save.repo.post.PostRepository;
import org.save.repo.post.PostTrendingRepository;
import org.save.service.finance.ReferralService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional
public class TrendingPostsCalculationService {

  private final PostRepository postRepository;
  private final PostTrendingRepository postTrendingRepository;
  private final ReferralService referralService;

  @Scheduled(cron = "@midnight")
  @Transactional
  public void storeTrendingPosts() {
    List<PostTrending> trendingPosts =
        postRepository.findAllByDateIsAfter(Instant.now().minus(2, ChronoUnit.DAYS)).stream()
            .map(this::createTrendingPost)
            .sorted(PostTrending.PointsComparator)
            .collect(Collectors.toList());

    for (int i = 0; i < trendingPosts.size(); i++) {
      trendingPosts.get(i).setId((long) i + 1);
    }

    postTrendingRepository.deleteAll();
    postTrendingRepository.saveAll(trendingPosts);

    log.info("Save {} trending posts", trendingPosts.size());

    List<Long> topThreePostCreatorIds = getTopThreePostsOwners(trendingPosts);
    sendTokensForTrendingPosts(topThreePostCreatorIds);
  }

  private PostTrending createTrendingPost(Post post) {
    PostTrending postTrending = new PostTrending();
    postTrending.setPost(post);
    postTrending.setPoints(calculatePostPoints(post));
    return postTrending;
  }

  private List<Long> getTopThreePostsOwners(List<PostTrending> trendingPosts) {
    return trendingPosts.stream()
        .map(post -> post.getPost().getUser().getId())
        .distinct()
        .limit(3)
        .collect(Collectors.toList());
  }

  private void sendTokensForTrendingPosts(List<Long> userIds) {
    userIds.forEach(
        id ->
            referralService.sendTokensFromOurWallet(
                id,
                ReferralCause.FOR_CREATE_TRENDING_POST.getAmount(),
                ReferralCause.FOR_CREATE_TRENDING_POST));
  }

  private long calculatePostPoints(Post post) {
    // The later the post is made, the less the factor.
    // Relevance factor is between 0 and 4
    long relevanceFactor =
        Math.max(4 - (post.getDate().until(Instant.now(), ChronoUnit.HOURS) / 12), 0);
    long commentsPoints = post.getComments().size() * 10L;
    long likesPoints = post.getLikes().size() * 5L;

    return 20L + relevanceFactor * (commentsPoints + likesPoints);
  }
}
