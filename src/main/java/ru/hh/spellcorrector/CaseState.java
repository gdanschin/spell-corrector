package ru.hh.spellcorrector;

public enum CaseState {

  DEFAULT {
    @Override
    String apply(String input) {
      return input;
    }
  },
  UPPER {
    @Override
    String apply(String input) {
      return input.toUpperCase();
    }
  },
  CAPITALIZED {
    @Override
    String apply(String input) {
      return new StringBuilder(input.length())
          .append(Character.toUpperCase(input.charAt(0)))
          .append(input.substring(1)).toString();
    }
  };

  abstract String apply(String input);

  public static CaseState getState(String input) {
    if (input.length() == 0) {
      return DEFAULT;
    }
    if (Character.isUpperCase(input.charAt(0))) {
      boolean hasUpper = false;
      boolean hasLower = false;
      char[] chars = input.toCharArray();
      for (int i = 1; i < chars.length; i++) {
        hasUpper = hasUpper || Character.isUpperCase(chars[i]);
        hasLower = hasLower || Character.isLowerCase(chars[i]);
        if (hasUpper && hasLower) {
          return DEFAULT;
        }
      }
      if (!hasUpper) {
        return CAPITALIZED;
      } else if (!hasLower) {
        return UPPER;
      }
    }

    return DEFAULT;
  }
}