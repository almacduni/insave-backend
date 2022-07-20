package org.save.util.picture;

import java.util.List;
import java.util.stream.Collectors;
import org.save.model.entity.social.picture.Picture;

public class PictureCreator {

  public static List<Picture> createPictures(List<String> pictures) {

    return pictures.stream().map(PictureCreator::createPicture).collect(Collectors.toList());
  }

  public static Picture createPicture(String picture) {
    Picture newPicture = new Picture();
    newPicture.setPicture(picture);

    return newPicture;
  }
}
