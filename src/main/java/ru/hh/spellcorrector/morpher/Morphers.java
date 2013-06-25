package ru.hh.spellcorrector.morpher;

import com.google.common.base.Optional;
import ru.hh.spellcorrector.dict.Dictionary;

import java.util.Set;

import static java.util.Arrays.asList;
import static ru.hh.spellcorrector.morpher.Charsets.ENG;
import static ru.hh.spellcorrector.morpher.Charsets.RUS;

public class Morphers {

  public static Morpher levenshteinStep() {
    return sum(delete(), transpose(), insert(), replace());
  }

  static Morpher testLevensteinStep(String alphabet) {
    return sum(delete(), transpose(), testInsert(alphabet), testReplace(alphabet));
  }

  public static Morpher delete() {
    return Delete.instance();
  }

  public static Morpher transpose() {
    return Transpose.instance();
  }

  public static Morpher insert() {
    return insert(RUS, ENG);
  }

  public static Morpher insert(String... alphabet) {
    return insert(asList(alphabet));
  }

  public static Morpher insert(Iterable<String> alphabets) {
    return new Insert(Charsets.of(alphabets));
  }

  static Morpher testInsert(final String alphabet) {
    return new Insert(testCharsets(alphabet));
  }

  public static Morpher replace() {
    return replace(RUS, ENG);
  }

  public static Morpher replace(String... alphabets) {
    return replace(asList(alphabets));
  }

  public static Morpher replace(Iterable<String> alphabets) {
    return new Replace(Charsets.of(alphabets));
  }

  static Morpher testReplace(final String alphabet) {
    return new Replace(testCharsets(alphabet));
  }

  private static Charsets testCharsets(final String alphabet) {
    return new Charsets(asList(alphabet)) {
      @Override
      public Optional<Set<Character>> guess(String word) {
        return Optional.of(charsets.get(0));
      }
    };
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

  public static Morpher slowCompose(Morpher... morphers) {
    return slowCompose(asList(morphers));
  }

  public static Morpher slowCompose(Iterable<? extends Morpher> morphers) {
    return new Composition(morphers, true);
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
