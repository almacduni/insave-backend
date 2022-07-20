package org.save.util.parsers;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostHashtagParser {

  public Set<String> getHashtagsFromText(String text) {

    final Pattern hashtagPattern = Pattern.compile("#[a-zA-Zа-яА-Я0-9]+");
    final Matcher matcher = hashtagPattern.matcher(text);

    final Set<String> hashtagSet = new HashSet<>();

    while (matcher.find()) {
      hashtagSet.add(text.substring(matcher.start(), matcher.end()));
    }

    return hashtagSet;
  }
}
