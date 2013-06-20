package ru.hh.spellcorrector.morpher;

import static java.util.Arrays.asList;

public class Morphers {

  public static Morpher levenshteinStep(String alphabet) {
    return sum(delete(), transpose(), insert(alphabet), replace(alphabet));
  }

  public static Morpher delete() {
    return Delete.instance();
  }

  public static Morpher transpose() {
    return Transpose.instance();
  }

  public static Morpher insert(String alphabet) {
    return new Insert(alphabet);
  }

  public static Morpher replace(String alphabet) {
    return new Replace(alphabet);
  }

  public static Morpher uniq(Morpher morpher) {
    return new Uniq(morpher);
  }

  public static Morpher sum(Morpher... morphers) {
    return sum(asList(morphers));
  }

  public static Morpher sum(Iterable<? extends Morpher> morphers) {
    return new Sum(morphers);
  }

  public static Morpher compose(Morpher... morphers) {
    return compose(asList(morphers));
  }

  public static Morpher compose(Iterable<? extends Morpher> morphers) {
    return new Composition(morphers, false);
  }

}
