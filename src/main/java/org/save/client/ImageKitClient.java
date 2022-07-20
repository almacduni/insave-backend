package org.save.client;

import lombok.RequiredArgsConstructor;
import org.save.model.dto.user.UploadAvatarResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ImageKitClient {

  private final RestTemplate restTemplate;
  private final String UPLOAD_FILE_URL = "https://upload.imagekit.io/api/v1/files/upload";

  @Value("${IMAGE_KIT_PRIVATE_KEY}")
  private final String IMAGE_KIT_PRIVATE_KEY;

  public String uploadFileToStorage(
      Resource resource, String name, String path, Boolean useUniqueFileName) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(IMAGE_KIT_PRIVATE_KEY, "");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);
    body.add("fileName", name);
    body.add("folder", path);
    body.add("useUniqueFileName", useUniqueFileName);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    return restTemplate
        .postForEntity(UPLOAD_FILE_URL, requestEntity, UploadAvatarResponse.class)
        .getBody()
        .getUrl();
  }
}
