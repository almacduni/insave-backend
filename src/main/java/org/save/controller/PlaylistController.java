package org.save.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.playlist.CategoriesResponse;
import org.save.model.dto.playlist.CreateOrUpdatePlaylistRequest;
import org.save.model.dto.playlist.CreatePlaylistCategoryRequest;
import org.save.model.dto.playlist.ExplorePageablePlaylistResponse;
import org.save.model.dto.playlist.PageablePlaylistResponse;
import org.save.model.dto.playlist.PlaylistCategoryResponse;
import org.save.model.dto.playlist.PlaylistResponse;
import org.save.service.playlist.PlaylistService;
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
@RequestMapping("/playlists")
public class PlaylistController {

  private final PlaylistService playlistService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public PageablePlaylistResponse getAllPlaylists(
      @RequestParam Integer page, @RequestParam Integer pageLimit) {
    return playlistService.getAllPlaylists(page, pageLimit);
  }

  @PostMapping("/category")
  public ResponseEntity<PlaylistCategoryResponse> createCategory(
      @Valid @RequestBody CreatePlaylistCategoryRequest category) {
    PlaylistCategoryResponse categoryResponse = playlistService.createCategory(category);

    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @SneakyThrows
  @Secured({"ROLE_USER"})
  @PostMapping
  public ResponseEntity<?> createPlaylist(
      @RequestPart(required = false) MultipartFile multipleFile,
      @RequestPart String playlistRequest,
      Principal principal) {
    CreateOrUpdatePlaylistRequest playlist =
        objectMapper.readValue(playlistRequest, CreateOrUpdatePlaylistRequest.class);
    playlistService.createPlaylist(playlist, multipleFile, principal);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Secured({"ROLE_ADMIN"})
  @PutMapping("/{id}")
  public ResponseEntity<?> addPlaylistToCategory(
      @RequestParam Long categoryId, @PathVariable Long id) {
    playlistService.addPlaylistToCategory(categoryId, id);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long id) {
    playlistService.getPlaylistById(id);

    return new ResponseEntity<>(playlistService.getPlaylistById(id), HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @PutMapping("/{id}/tickers")
  public ResponseEntity<String> addTickersToPlaylist(
      @RequestParam List<Long> tickersId, @PathVariable Long id, Principal principal) {
    playlistService.addTickersToPlaylist(tickersId, id, principal);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePlaylist(@PathVariable Long id, Principal principal) {
    playlistService.deletePlaylist(id, principal);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @SneakyThrows
  @Secured({"ROLE_USER"})
  @PutMapping("/{id}/update")
  public ResponseEntity<?> updatePlaylist(
      @RequestPart String request,
      @RequestPart(required = false) MultipartFile multipleFile,
      @PathVariable Long id,
      Principal principal) {
    CreateOrUpdatePlaylistRequest playlist =
        objectMapper.readValue(request, CreateOrUpdatePlaylistRequest.class);
    playlistService.updatePlaylist(playlist, id, multipleFile, principal);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @DeleteMapping("/ticker/{id}")
  public ResponseEntity<?> deleteTickerFromPlaylist(
      @PathVariable Long id, @RequestParam Long tickerId, Principal principal) {
    playlistService.deleteTicker(tickerId, id, principal);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/categories")
  public ResponseEntity<CategoriesResponse> getCategories(
      @RequestParam Integer page,
      @RequestParam Integer pageLimit,
      @RequestParam Integer playlistLimit) {

    return new ResponseEntity<>(
        playlistService.getCategories(page, pageLimit, playlistLimit), HttpStatus.OK);
  }

  @GetMapping("/category")
  public ResponseEntity<ExplorePageablePlaylistResponse> getPlaylistsByCategory(
      @RequestParam Long categoryId, @RequestParam Integer page, @RequestParam Integer pageLimit) {

    return new ResponseEntity<>(
        playlistService.getPlaylistsByCategory(categoryId, page, pageLimit), HttpStatus.OK);
  }
}
