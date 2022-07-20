package org.save.service.post;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.entity.social.post.Post;
import org.save.model.entity.social.post.PostForYouTrending;
import org.save.model.entity.social.post.PostHashtag;
import org.save.repo.post.PostForYouTrendingRepository;
import org.save.repo.post.PostHashtagRepository;
import org.save.repo.post.PostRepository;
import org.save.util.parsers.PostHashtagParser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * class <code>PostForYouCreatorService</code> is needed to calculate points for sorting posts to
 * create trending list for you with 100 elements limit
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class PostForYouCreatorService {

  private final PostRepository postRepository;
  private final PostForYouTrendingRepository trendingRepository;
  private final PostHashtagRepository postHashtagRepository;
  private final PostHashtagParser postHashtagParser;

  @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
  @Transactional
  public void calculateTrendingPosts() {

    final List<PostForYouTrending> trendingPostList = new ArrayList<>();

    postRepository
        .findAll()
        .forEach(
            post -> {
              PostForYouTrending postTrending = new PostForYouTrending();
              postTrending.setPost(post);
              postTrending.setPoints(calcPoints(post));
              trendingPostList.add(postTrending);
            });

    trendingPostList.sort(PostForYouTrending.PointsComparator);

    for (int i = 0; i < trendingPostList.size(); i++) {
      trendingPostList.get(i).setId((long) i + 1);
    }

    trimTrendingList(trendingPostList);

    postRepository.deleteStatisticByDate(LocalDate.now().minus(14, ChronoUnit.DAYS));
    trendingRepository.deleteAll();
    trendingRepository.saveAll(trendingPostList);
  }

  /**
   * method that trims param list to 100 elements or more if necessary, because limit for trending
   * is 100 elements. method checks is in list element with the same number of points like in last
   * hundredth element if such is exist method will add it to list even if the size of the list
   * exceeds 100
   *
   * @param postTrendingList
   */
  private void trimTrendingList(List<PostForYouTrending> postTrendingList) {
    if (postTrendingList.size() >= 100) {
      int lastIncomingIndex = 99;
      while (postTrendingList.get(lastIncomingIndex).getPoints() > 0
          && postTrendingList
              .get(lastIncomingIndex + 1)
              .getPoints()
              .equals(postTrendingList.get(lastIncomingIndex).getPoints())) {
        lastIncomingIndex++;
      }
      postTrendingList.subList(lastIncomingIndex + 1, postTrendingList.size()).clear();
    }
  }

  /**
   * calculates points based on daily statistic from post_daily_statistic table for sorting posts
   *
   * @param post
   * @return points for every incoming in param post
   */
  private long calcPoints(Post post) {
    Integer commentPoints =
        postRepository
            .countCommentsNumber(post.getId(), LocalDate.now().minus(14, ChronoUnit.DAYS))
            .orElse(0);
    if (commentPoints != 0) {
      commentPoints *= 8;
    }
    Integer likesPoints =
        postRepository
            .countNumberOfLikes(post.getId(), LocalDate.now().minus(14, ChronoUnit.DAYS))
            .orElse(0);
    if (commentPoints != 0) {
      likesPoints *= 4;
    }
    Integer openingsPoints =
        postRepository
            .countOpenings(post.getId(), LocalDate.now().minus(14, ChronoUnit.DAYS))
            .orElse(0);

    return commentPoints + likesPoints + openingsPoints;
  }

  public void parseAndSavePostHashtags(Post post) {

    final List<PostHashtag> postHashtagList = new ArrayList<>();

    postHashtagParser
        .getHashtagsFromText(post.getText())
        .forEach(
            hashtagString -> {
              final PostHashtag postHashtag = new PostHashtag();
              postHashtag.setPost(post);
              postHashtag.setName(hashtagString);

              postHashtagList.add(postHashtag);
            });

    postHashtagRepository.saveAll(postHashtagList);
  }
}
