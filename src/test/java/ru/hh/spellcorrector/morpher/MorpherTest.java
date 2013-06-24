package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.Correction;

import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static ru.hh.spellcorrector.morpher.Morphers.compose;
import static ru.hh.spellcorrector.morpher.Morphers.delete;
import static ru.hh.spellcorrector.morpher.Morphers.insert;
import static ru.hh.spellcorrector.morpher.Morphers.levenshteinStep;
import static ru.hh.spellcorrector.morpher.Morphers.replace;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class MorpherTest {

  static final String alphabet = "XYZ";

  @Test
  public void deleteTest() {
    assertEquals(correctionSet(delete().corrections("abcd")), set("abc", "abd", "acd", "bcd"));
  }

  @Test
  public void transposeTest() {
    assertEquals(correctionSet(transpose().corrections("abcd")), set("abdc", "acbd", "bacd"));
  }

  @Test
  public void insertTest() {
    assertEquals(correctionSet(insert(alphabet).corrections("abcd")), set(
        "abcdX", "abcdY", "abcdZ",
        "abcXd", "abcYd", "abcZd",
        "abXcd", "abYcd", "abZcd",
        "aXbcd", "aYbcd", "aZbcd",
        "Xabcd", "Yabcd", "Zabcd"

    ));
  }

  @Test
  public void replaceTest() {
    assertEquals(correctionSet(replace(alphabet).corrections("abcd")), set(
        "abcX", "abcY", "abcZ",
        "abXd", "abYd", "abZd",
        "aXcd", "aYcd", "aZcd",
        "Xbcd", "Ybcd", "Zbcd"

    ));
  }

  @Test
  public void levenshteinTest() {
    assertEquals(correctionSet(levenshteinStep(alphabet).corrections("abcd")), set(
        "abc", "abd", "acd", "bcd",
        "abdc", "acbd", "bacd",
        "abcdX", "abcdY", "abcdZ",
        "abcXd", "abcYd", "abcZd",
        "abXcd", "abYcd", "abZcd",
        "aXbcd", "aYbcd", "aZbcd",
        "Xabcd", "Yabcd", "Zabcd",
        "abcX", "abcY", "abcZ",
        "abXd", "abYd", "abZd",
        "aXcd", "aYcd", "aZcd",
        "Xbcd", "Ybcd", "Zbcd"
    ));
  }

  @Test
  public void composeLevenshteinTest() {
    final Morpher step = levenshteinStep(alphabet);

    Set<String> expected = Sets.newHashSet("abcd");
    expected.addAll(correctionSet(levenshteinStep(alphabet).corrections("abcd")));

    assertEquals(set(expected), correctionSet(compose(step).corrections("abcd")));

    expected.addAll(correctionSet(concat(transform(expected, new Function<String, Iterable<Correction>>() {
      @Override
      public Iterable<Correction> apply(String input) {
        return step.corrections(input);
      }
    }))));

    assertEquals(correctionSet(compose(step, step).corrections("abcd")), expected);
  }

  public static Set<String> correctionSet(Iterable<? extends Correction> corrections) {
    return FluentIterable.from(corrections)
        .transformAndConcat(new Function<Correction, Iterable<String>>() {
          @Override
          public Iterable<String> apply(Correction input) {
            return input.getPhrase().getWords();
          }
        })
        .toSet();
  }

  public static Set<String> set(String... word) {
    return set(asList(word));
  }

  public static Set<String> set(Iterable<? extends String> iterable) {
    return ImmutableSet.copyOf(iterable);
  }
}
