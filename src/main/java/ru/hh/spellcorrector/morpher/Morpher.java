package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;

public abstract class Morpher {

  public abstract Iterable<String> variants(String source);

  private final Function<String, Iterable<String>> FUNCTION = new Function<String, Iterable<String>>() {
    @Override
    public Iterable<String> apply(String t) {
      return variants(t);
    }
  };

  final Function<String, Iterable<String>> asFunction() {
    return FUNCTION;
  }

  private static final Function<Morpher, Function<String, Iterable<String>>> TO_FUNC =
      new Function<Morpher, Function<String, Iterable<String>>>() {
        @Override
        public Function<String, Iterable<String>> apply(Morpher input) {
          return input.asFunction();
        }
      };

  static final Function<Morpher, Function<String, Iterable<String>>> toFunction() {
    return TO_FUNC;
  }
}
