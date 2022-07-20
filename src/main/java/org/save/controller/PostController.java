package org.save.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.Collection;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.social.post.PageablePostResponse;
import org.save.model.dto.social.post.PostCommentRequest;
import org.save.model.dto.social.post.PostCommentResponse;
import org.save.model.dto.social.post.PostRequest;
import org.save.model.dto.social.post.PostResponse;
import org.save.model.dto.user.UserStatisticPageResponse;
import org.save.service.post.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final ObjectMapper objectMapper;

  @SneakyThrows
  @Secured({"ROLE_USER"})
  @PostMapping
  public ResponseEntity<?> createPost(
      @RequestPart(required = false) MultipartFile[] multipleFiles,
      @RequestPart String postRequest,
      Principal principal) {
    PostRequest post = objectMapper.readValue(postRequest, PostRequest.class);
    postService.createPost(post, principal, multipleFiles);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping("/like")
  public ResponseEntity<?> likePost(@RequestParam Long postId, Principal principal) {
    postService.likePost(postId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping("/dislike")
  public ResponseEntity<?> disLikePost(@RequestParam Long postId, Principal principal) {
    postService.disLikedPost(postId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER"})
  @DeleteMapping("{postId}")
  public ResponseEntity<?> deletePostById(@PathVariable Long postId, Principal principal) {
    postService.deletePostById(postId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/latest")
  public ResponseEntity<PageablePostResponse> getLatestPosts(
      @RequestParam Integer page, @RequestParam Integer pageLimit, Principal principal) {
    PageablePostResponse latestPosts = postService.getLatestPosts(page, pageLimit, principal);

    return new ResponseEntity<>(latestPosts, HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @PostMapping("/comment")
  public ResponseEntity<?> createPostComment(
      @RequestBody @Valid PostCommentRequest postCommentRequest, Principal principal) {
    postService.createPostComment(postCommentRequest, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PutMapping("/comment/like")
  public ResponseEntity<?> likePostComment(@RequestParam Long commentId, Principal principal) {
    postService.likedPostComment(commentId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PutMapping("/comment/dislike")
  public ResponseEntity<?> disLikePostComment(@RequestParam Long commentId, Principal principal) {
    postService.dislikePostComment(commentId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER"})
  @DeleteMapping("/comment")
  public ResponseEntity<?> deletePostCommentById(
      @RequestParam Long commentId, Principal principal) {
    postService.deletePostCommentById(commentId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{postId}/comments")
  public ResponseEntity<Collection<PostCommentResponse>> getPostComments(
      @PathVariable Long postId, Principal principal) {
    final Collection<PostCommentResponse> postComments =
        postService.getPostComments(postId, principal);

    return new ResponseEntity<>(postComments, HttpStatus.OK);
  }

  @GetMapping("/trending")
  public ResponseEntity<PageablePostResponse> getTrendingPosts(
      @RequestParam Integer page, @RequestParam Integer pageLimit, Principal principal) {
    PageablePostResponse trendingPosts = postService.getTrendingPosts(page, pageLimit, principal);

    return new ResponseEntity<>(trendingPosts, HttpStatus.OK);
  }

  @GetMapping("/forYou")
  public ResponseEntity<PageablePostResponse> getForYouPosts(
      @RequestParam Integer page, @RequestParam Integer pageLimit, Principal principal) {
    PageablePostResponse forYouPosts = postService.getForYouPosts(principal, page, pageLimit);

    return new ResponseEntity<>(forYouPosts, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PutMapping("/{postId}/poll/vote")
  public ResponseEntity<?> voteInPostPoll(
      @PathVariable Long postId, @RequestParam String choiceId, Principal principal) {
    postService.voteInPostPoll(postId, choiceId, principal);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/statistic")
  public ResponseEntity<UserStatisticPageResponse> getStatisticPageForUser(Principal principal) {
    UserStatisticPageResponse userStatisticPage = postService.getUserStatistic(principal);

    return new ResponseEntity<UserStatisticPageResponse>(userStatisticPage, HttpStatus.OK);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
    return new ResponseEntity<>(postService.getPostById(postId), HttpStatus.OK);
  }
}
