package ru.hh.spellcorrector.morpher;

import ru.hh.spellcorrector.dict.Dictionary;

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

  public static Morpher language(Dictionary dict, boolean shortCircuit) {
    return language(dict, 0.5, shortCircuit);
  }

  public static Morpher language(Dictionary dict, double power, boolean shortCircuit) {
    return new Language(dict, power, shortCircuit);
  }

  public static Morpher keyboard() {
    return new Keyboard(0.3);
  }

  public static Morpher split() {
    return new Split();
  }
}
