package org.save.service.post;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.client.ImageKitClient;
import org.save.exception.InvalidArgumentException;
import org.save.exception.LimitOnPostsAndPostsCommentsException;
import org.save.exception.NoCredentialsException;
import org.save.exception.NoSuchObjectException;
import org.save.model.dto.social.post.PageablePostResponse;
import org.save.model.dto.social.post.PostCommentRequest;
import org.save.model.dto.social.post.PostCommentResponse;
import org.save.model.dto.social.post.PostRequest;
import org.save.model.dto.social.post.PostResponse;
import org.save.model.dto.user.UserStatisticPageResponse;
import org.save.model.entity.common.User;
import org.save.model.entity.social.post.Poll;
import org.save.model.entity.social.post.Poll.Choice;
import org.save.model.entity.social.post.Post;
import org.save.model.entity.social.post.PostComment;
import org.save.model.entity.social.post.PostCommentLike;
import org.save.model.entity.social.post.PostDailyStatistic;
import org.save.model.entity.social.post.PostForYouTrending;
import org.save.model.entity.social.post.PostLike;
import org.save.model.entity.social.post.PostTrending;
import org.save.repo.UserRepository;
import org.save.repo.post.PostCommentLikeRepository;
import org.save.repo.post.PostCommentRepository;
import org.save.repo.post.PostForYouTrendingRepository;
import org.save.repo.post.PostLikeRepository;
import org.save.repo.post.PostRepository;
import org.save.repo.post.PostTrendingRepository;
import org.save.service.finance.ReferralService;
import org.save.util.authenticator.Authenticator;
import org.save.util.mapper.PostMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

  private static final Integer MAX_POSTS_DURING_DAY = 5;
  private static final Integer MAX_POSTS_COMMENTS_DURING_DAY = 20;

  private Map<Object, String> postOrCommentMap;

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final PostCommentRepository postCommentRepository;
  private final PostCommentLikeRepository postCommentLikeRepository;
  private final PostTrendingRepository postTrendingRepository;
  private final PostForYouTrendingRepository postForYouTrendingRepository;

  private final PostForYouCreatorService postForYouCreatorService;
  private final ReferralService referralService;
  private final ImageKitClient imageKitClient;

  private final PostMapper postMapper;

  @PostConstruct
  private void init() {
    postOrCommentMap = new HashMap<>();
    postOrCommentMap.put(PostComment.class, "comment");
    postOrCommentMap.put(Post.class, "post");
  }

  public Post createPost(
      PostRequest postRequest, Principal principal, MultipartFile[] multipleFiles) {
    validatePost(postRequest, multipleFiles);
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "User with userName = " + principal.getName() + " not found"));
    checkLimit(Post.class, user);

    Post post = new Post();
    post.setUser(user);
    post.setDate(Instant.now());
    post.setText(postRequest.getText());
    Poll poll = new Poll();
    Set<Choice> choiceSet = new HashSet<>();
    if (postRequest.getPoll() != null) {
      List<String> choicesFromReq = postRequest.getPoll();
      choicesFromReq.stream()
          .forEach(
              choice -> {
                Choice newChoice = new Choice();
                newChoice.setTitle(choice);
                newChoice.setVoterIds(new HashSet<>());
                choiceSet.add(newChoice);
                newChoice.setUuid(UUID.randomUUID().toString());
              });
      poll.setChoices(choiceSet);
      post.setPoll(poll);
    }

    postRepository.save(post);
    if (multipleFiles != null) {
      List<String> pictureList = new ArrayList<>();
      for (MultipartFile picture : multipleFiles) {
        Resource resource = picture.getResource();
        String fileName = "picture";
        String filePath = "feed/post_" + post.getId();
        pictureList.add(imageKitClient.uploadFileToStorage(resource, fileName, filePath, true));
      }
      post.setPictures(pictureList);
    }

    PostDailyStatistic statistic = new PostDailyStatistic(LocalDate.now(), 0, 0, 0);
    List<PostDailyStatistic> trending = new ArrayList<>();
    trending.add(statistic);
    post.setTrending(trending);

    if (postRequest.getGifUrl() != null) {
      post.setGifUrl(postRequest.getGifUrl());
    }

    Post savedPost = postRepository.save(post);
    if (savedPost.getText() != null) {
      postForYouCreatorService.parseAndSavePostHashtags(savedPost);
    }
    return savedPost;
  }

  public void deletePostById(Long postId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    postRepository
        .findPostByUserIdAndPostId(userId, postId)
        .orElseThrow(
            () ->
                new NoCredentialsException(
                    "can't delete post with provided id: "
                        + postId
                        + " by user with provided id: "
                        + userId));
    postRepository.deleteById(postId);
  }

  public PageablePostResponse getLatestPosts(Integer page, Integer pageLimit, Principal principal) {
    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }
    Long userId = getActualUserId(principal);
    PageRequest postRequest = PageRequest.of(page - 1, pageLimit);

    Page<Post> pageablePost = postRepository.findLimited(postRequest);
    List<PostResponse> posts =
        pageablePost.stream()
            .map(post -> postMapper.convertToPostResponse(userId, post))
            .collect(Collectors.toList());

    return PageablePostResponse.builder()
        .posts(posts)
        .currentPage(pageablePost.getNumber() + 1)
        .totalCount(pageablePost.getTotalElements())
        .offset(pageablePost.getSize())
        .build();
  }

  public void likePost(Long postId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());

    Optional<PostLike> postLikeOptional =
        postLikeRepository.findPostLikeByPostIdAndUserId(postId, userId);

    if (!postLikeOptional.isPresent()) {
      Post post =
          postRepository
              .findById(postId)
              .orElseThrow(
                  () -> new NoSuchObjectException("there is no post with provided id: " + postId));
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () -> new NoSuchObjectException("there is no user with provided id: " + userId));

      PostLike postLike = new PostLike();
      postLike.setUser(user);
      postLike.setPost(post);
      increaseLikesCount(post);

      postLikeRepository.save(postLike);
    }
  }

  private void increaseOpeningsCount(Post post) {
    if (post.getTrending().contains(new PostDailyStatistic(LocalDate.now()))) {
      int lastIndex = post.getTrending().size() - 1;
      PostDailyStatistic dailyStatistic = post.getTrending().get(lastIndex);
      dailyStatistic.setOpenings(dailyStatistic.getOpenings() + 1);
    } else {
      post.getTrending().add(new PostDailyStatistic(LocalDate.now(), 1, 0, 0));
    }
  }

  private void increaseLikesCount(Post post) {
    if (post.getTrending().contains(new PostDailyStatistic(LocalDate.now()))) {
      int lastIndex = post.getTrending().size() - 1;
      PostDailyStatistic dailyStatistic = post.getTrending().get(lastIndex);
      dailyStatistic.setLikesCount(dailyStatistic.getLikesCount() + 1);
    } else {
      post.getTrending().add(new PostDailyStatistic(LocalDate.now(), 0, 1, 0));
    }
  }

  private void increaseCommentsCount(Post post) {
    if (post.getTrending().contains(new PostDailyStatistic(LocalDate.now()))) {
      int lastIndex = post.getTrending().size() - 1;
      PostDailyStatistic dailyStatistic = post.getTrending().get(lastIndex);
      dailyStatistic.setCommentsCount(dailyStatistic.getCommentsCount() + 1);
    } else {
      post.getTrending().add(new PostDailyStatistic(LocalDate.now(), 0, 0, 1));
    }
    postRepository.save(post);
  }

  public void disLikedPost(Long postId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());

    Optional<PostLike> postLikeOptional =
        postLikeRepository.findPostLikeByPostIdAndUserId(postId, userId);

    if (postLikeOptional.isPresent()) {
      postLikeRepository.delete(postLikeOptional.get());
    }
  }

  public void createPostComment(PostCommentRequest postCommentRequest, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new NoSuchObjectException("User with id = " + userId + " not found"));
    checkLimit(PostComment.class, user);
    Post post =
        postRepository
            .findById(postCommentRequest.getPostId())
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "Post with id = " + postCommentRequest.getPostId() + " not found"));

    PostComment postComment = new PostComment();
    postComment.setDate(Instant.now());
    postComment.setUser(user);
    postComment.setPost(post);
    postComment.setText(postCommentRequest.getText());
    postComment.setReply(postCommentRequest.getIsReply());
    postComment.setGifUrl(postCommentRequest.getGifUrl());
    increaseCommentsCount(post);
    if (postCommentRequest.getIsReply()) {
      postComment.setReplyTo(postCommentRequest.getReplyTo());
    }
    postCommentRepository.save(postComment);
  }

  public void deletePostCommentById(Long postCommentId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    postCommentRepository
        .findByUser(userId, postCommentId)
        .orElseThrow(
            () ->
                new NoCredentialsException(
                    "user with provided id cant delete comment " + "with provided comment id"));
    postCommentRepository.deleteById(postCommentId);
  }

  public void likedPostComment(Long postCommentId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());

    Optional<PostCommentLike> postCommentLikeOptional =
        postCommentLikeRepository.findPostCommentLikeByCommentIdAndUserId(postCommentId, userId);

    if (!postCommentLikeOptional.isPresent()) {

      PostComment postComment =
          postCommentRepository
              .findById(postCommentId)
              .orElseThrow(
                  () ->
                      new NoSuchObjectException("Post with id = " + postCommentId + " not found"));
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () -> new NoSuchObjectException("User with id = " + userId + " non found"));

      PostCommentLike postCommentLike = new PostCommentLike();

      postCommentLike.setUser(user);
      postCommentLike.setComment(postComment);

      postComment.getLikes().add(postCommentLike);

      postCommentRepository.save(postComment);
    }
  }

  public void dislikePostComment(Long postCommentId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());

    postCommentLikeRepository
        .findPostCommentLikeByCommentIdAndUserId(postCommentId, userId)
        .ifPresent(postCommentLikeRepository::delete);
  }

  public Collection<PostCommentResponse> getPostComments(Long postId, Principal principal) {
    Long userId = getActualUserId(principal);

    return postCommentRepository.findAllByPostIdOrderByIdDesc(postId).stream()
        .map(postComment -> postMapper.convertToPostCommentResponse(userId, postComment))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PageablePostResponse getTrendingPosts(
      Integer page, Integer pageLimit, Principal principal) {
    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }

    Long userId = getActualUserId(principal);
    PageRequest postRequest = PageRequest.of(page - 1, pageLimit);
    Page<PostTrending> pageablePost = postTrendingRepository.findPageable(postRequest);

    List<PostResponse> posts =
        pageablePost.stream()
            .map(postTrending -> postMapper.convertToPostResponse(userId, postTrending.getPost()))
            .collect(Collectors.toList());

    return PageablePostResponse.builder()
        .posts(posts)
        .currentPage(pageablePost.getNumber() + 1)
        .totalCount(pageablePost.getTotalElements())
        .offset(pageablePost.getSize())
        .build();
  }

  @Transactional(readOnly = true)
  public PageablePostResponse getForYouPosts(Principal principal, Integer page, Integer pageLimit) {
    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }

    Long userId = getActualUserId(principal);
    PageRequest postRequest = PageRequest.of(page - 1, pageLimit);
    Page<PostForYouTrending> pageablePost = postForYouTrendingRepository.findPageable(postRequest);

    List<PostResponse> posts =
        pageablePost.stream()
            .map(postTrending -> postMapper.convertToPostResponse(userId, postTrending.getPost()))
            .collect(Collectors.toList());

    return PageablePostResponse.builder()
        .posts(posts)
        .currentPage(pageablePost.getNumber() + 1)
        .totalCount(pageablePost.getTotalElements())
        .offset(pageablePost.getSize())
        .build();
  }

  public void voteInPostPoll(Long postId, String choiceId, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found"));

    post.getPoll()
        .getChoices()
        .forEach(
            choice -> {
              if (choice.getVoterIds().contains(userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already voted");
              }
            });

    for (Choice choice : post.getPoll().getChoices()) {
      if (choice.getUuid().equals(choiceId)) {
        choice.getVoterIds().add(userId);
        break;
      }
    }

    postRepository.save(post);
  }

  // TODO add functionality for other fields
  public UserStatisticPageResponse getUserStatistic(Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    UserStatisticPageResponse userStatisticPage = new UserStatisticPageResponse();
    userStatisticPage.setFriendsInvitedCount(userRepository.getCountInvitedFriends(userId));
    userStatisticPage.setPostsPublishedCount(postRepository.getCountPosts(userId));
    userStatisticPage.setPostsLikes(postLikeRepository.getCountLikesByUserId(userId));
    userStatisticPage.setPostsComments(postCommentRepository.getCountCommentsByUserId(userId));

    return userStatisticPage;
  }

  public PostResponse getPostById(Long postId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> new NoSuchObjectException("Post with id = " + postId + " not found"));
    increaseOpeningsCount(post);

    return postMapper.convertToPostResponse(postId, post);
  }

  private void checkLimit(Object postOrComment, User user) {
    int createdPostsDuringDayNumber;
    int limit;
    switch (postOrCommentMap.get(postOrComment)) {
      case "post":
        createdPostsDuringDayNumber =
            postRepository
                .countByDateAfterAndUser(Instant.now().minus(1, ChronoUnit.DAYS), user)
                .orElse(0);
        limit = MAX_POSTS_DURING_DAY;
        break;
      case "comment":
        createdPostsDuringDayNumber =
            postCommentRepository
                .countByDateAfterAndUser(Instant.now().minus(1, ChronoUnit.DAYS), user)
                .orElse(0);
        limit = MAX_POSTS_COMMENTS_DURING_DAY;
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + postOrCommentMap.get(postOrComment));
    }
    if (createdPostsDuringDayNumber >= limit) {
      throw new LimitOnPostsAndPostsCommentsException(
          "You have reached limit on creating posts or comments");
    }
  }

  /**
   * Used in methods where necessary to indicate whether objects like: {@link Post}, {@link
   * PostComment} have been liked by authenticated user
   *
   * @param principal of {@link Principal} type - user credentials
   * @return {@link Long} userId or 0L if user not authenticated
   */
  private Long getActualUserId(Principal principal) {
    if (Authenticator.isUserAuthenticated()) {
      return userRepository.getUerIdByUsername(principal.getName());
    } else {
      return 0L;
    }
  }

  private void validatePost(PostRequest postRequest, MultipartFile[] multipleFiles) {
    if (postRequest.getText() == null
        || postRequest.getText().length() > 300) { // 300 max symbols in text, text is mandatory
      throw new InvalidArgumentException("Text must be mandatory or less then 300 symbols");
    }

    if (multipleFiles != null && postRequest.getGifUrl() != null) {
      throw new InvalidArgumentException("You can add only picture or gif");
    }
  }
}
