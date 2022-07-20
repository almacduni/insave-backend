package org.save.util;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {

  private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String LOWERCASE = "qwertyuiopasdfghjklzxcvbnm";
  private static final String DIGITS = "0123456789";
  private static final String ALPHANUMERIC = UPPERCASE + LOWERCASE + DIGITS;

  public static StringBuffer generateString(int length) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < length; i++) {
      stringBuffer.append(
          ALPHANUMERIC.charAt(ThreadLocalRandom.current().nextInt(ALPHANUMERIC.length())));
    }
    return stringBuffer;
  }

  // username contains 7 chars. First char must be capital letter
  public static String generateUsername() {
    StringBuffer stringBuffer = StringUtils.generateString(6);
    stringBuffer.insert(
        0, UPPERCASE.charAt(ThreadLocalRandom.current().nextInt(UPPERCASE.length())));
    return stringBuffer.toString();
  }
}
