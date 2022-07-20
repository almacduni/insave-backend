package org.save.util.mapper;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.social.post.PostCommentResponse;
import org.save.model.dto.social.post.PostResponse;
import org.save.model.entity.social.post.Poll;
import org.save.model.entity.social.post.Post;
import org.save.model.entity.social.post.PostComment;
import org.save.repo.post.PostCommentLikeRepository;
import org.save.repo.post.PostCommentRepository;
import org.save.repo.post.PostLikeRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMapper {

  private final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ").withZone(ZoneOffset.UTC);

  private final String USER_LOGO_URL = "https://i.postimg.cc/GmvHTNq6/Group-157.png";

  private final PostLikeRepository postLikeRepository;
  private final PostCommentLikeRepository postCommentLikeRepository;
  private final PostCommentRepository postCommentRepository;

  public PostResponse convertToPostResponse(Long userId, Post post) {
    List<PostCommentResponse> postCommentResponse =
        postCommentRepository.findAllByPostIdOrderByIdDesc(post.getId()).stream()
            .map(postComment -> convertToPostCommentResponse(userId, postComment))
            .collect(Collectors.toList());

    return new PostResponse(
        post.getId(),
        dateTimeFormatter.format(post.getDate()),
        post.getUser().getId(),
        post.getUser().getUsername(),
        USER_LOGO_URL,
        postLikeRepository.getCountLikesByPostId(post.getId()),
        postCommentLikeRepository.getCountCommentsByPostId(post.getId()),
        isPostLikedByUser(post.getId(), userId),
        post.getText(),
        post.getVideoUrl(),
        convertToPollResponse(userId, post.getPoll()),
        post.getPictures(),
        postCommentResponse,
        post.getGifUrl());
  }

  public PostCommentResponse convertToPostCommentResponse(Long userId, PostComment postComment) {
    return new PostCommentResponse(
        postComment.getId(),
        dateTimeFormatter.format(postComment.getDate()),
        postComment.getUser().getId(),
        postComment.getUser().getUsername(),
        USER_LOGO_URL,
        postComment.getLikes().size(),
        isPostCommentLikedByUser(postComment.getId(), userId),
        postComment.getText(),
        postComment.getGifUrl(),
        postComment.getReplyTo(),
        postComment.isReply());
  }

  private PostResponse.PollResponse convertToPollResponse(Long userId, Poll poll) {

    if (poll == null) {
      return null;
    }

    Set<PostResponse.PollResponse.ChoiceResponse> choiceResponses = new HashSet<>();

    poll.getChoices()
        .forEach(
            choice -> {
              choiceResponses.add(
                  new PostResponse.PollResponse.ChoiceResponse(
                      choice.getUuid(),
                      choice.getTitle(),
                      choice.getVoterIds().size(),
                      choice.getVoterIds().contains(userId)));
            });

    return new PostResponse.PollResponse(choiceResponses);
  }

  private boolean isPostLikedByUser(Long postId, Long userId) {
    if (userId == 0L) {
      return false;
    }
    return postLikeRepository.existsByPostIdAndUserId(postId, userId);
  }

  private boolean isPostCommentLikedByUser(Long commentId, Long userId) {
    if (userId == 0L) {
      return false;
    }
    return postCommentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
  }
}
