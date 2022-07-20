package org.save.service.serach;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.model.entity.user.PersonalData;
import org.save.repo.PersonalDataRepository;
import org.save.repo.UserRepository;
import org.save.util.authenticator.Authenticator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

  private static final int HISTORY_LIMIT = 10;
  private final PersonalDataRepository personalDataRepository;
  private final UserRepository userRepository;

  public void addToHistory(String ticker, Principal principal) {
    if (!Authenticator.isUserAuthenticated()) {
      return;
    }
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    PersonalData personalData = personalDataRepository.findPersonalDataByUserId(userId);
    if (personalData != null) {
      if (personalData.getTickersSearchHistory() == null) {
        personalData.setTickersSearchHistory("");
      }
      List<String> history =
          new ArrayList<>(Arrays.asList(personalData.getTickersSearchHistory().split(",")));

      history.remove(ticker);
      if (history.size() == HISTORY_LIMIT) {
        history.remove(0);
      }
      history.add(ticker);

      StringBuilder response = new StringBuilder();
      for (String h : history) {
        if (!h.equals("")) response.append(h).append(",");
      }
      personalDataRepository.addToHistory(userId, response.toString());
    }
  }

  public List<String> getHistory(Long id) {
    PersonalData personalData = personalDataRepository.findPersonalDataByUserId(id);
    if (personalData != null) {
      if (personalData.getTickersSearchHistory() != null) {
        List<String> history =
            Arrays.asList(
                personalDataRepository
                    .findPersonalDataByUserId(id)
                    .getTickersSearchHistory()
                    .split(","));
        Collections.reverse(history);
        return history;
      }
    }
    return new ArrayList<>();
  }
}
