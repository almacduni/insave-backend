package org.save;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtil {
  private static final int RANDOM_STRING_LENGTH = 16;
  private static final int RANDOM_INT_DEFAULT_BOUND = 1_000_000;
  private static final Random RANDOM = new Random();

  public static String randomString() {
    return randomString(RANDOM_STRING_LENGTH);
  }

  public static String randomString(int length) {
    return RandomStringUtils.randomAlphabetic(length);
  }

  public static String randomEmail() {
    return randomString() + "@gmail.com";
  }

  public static String randomPhone() {
    return RandomStringUtils.randomNumeric(7);
  }

  public static int randomInt() {
    return randomInt(RANDOM_INT_DEFAULT_BOUND);
  }

  public static int randomInt(int max) {
    return RANDOM.nextInt(max);
  }
}
