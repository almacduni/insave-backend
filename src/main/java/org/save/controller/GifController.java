package org.save.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.client.TenorGifClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gifs")
@RequiredArgsConstructor
@Secured({"ROLE_USER"})
public class GifController {

  private final TenorGifClient tenorClient;

  @GetMapping("/{gifName}")
  public ResponseEntity<List<String>> searchGifsByName(@PathVariable String gifName) {
    return new ResponseEntity<>(tenorClient.searchGifsByName(gifName), HttpStatus.OK);
  }

  @GetMapping("/trending")
  public ResponseEntity<List<String>> getTendingGifs() {
    return new ResponseEntity<>(tenorClient.getTendingGifs(), HttpStatus.OK);
  }
}
