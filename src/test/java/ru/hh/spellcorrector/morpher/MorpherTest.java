package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;

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
    assertEquals(set(delete().variants("abcd")), set("abc", "abd", "acd", "bcd"));
  }

  @Test
  public void transposeTest() {
    assertEquals(set(transpose().variants("abcd")), set("abdc", "acbd", "bacd"));
  }

  @Test
  public void insertTest() {
    assertEquals(set(insert(alphabet).variants("abcd")), set(
        "abcdX", "abcdY", "abcdZ",
        "abcXd", "abcYd", "abcZd",
        "abXcd", "abYcd", "abZcd",
        "aXbcd", "aYbcd", "aZbcd",
        "Xabcd", "Yabcd", "Zabcd"

    ));
  }

  @Test
  public void replaceTest() {
    assertEquals(set(replace(alphabet).variants("abcd")), set(
        "abcX", "abcY", "abcZ",
        "abXd", "abYd", "abZd",
        "aXcd", "aYcd", "aZcd",
        "Xbcd", "Ybcd", "Zbcd"

    ));
  }

  @Test
  public void levenshteinTest() {
    assertEquals(set(levenshteinStep(alphabet).variants("abcd")), set(
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
    expected.addAll(set(levenshteinStep(alphabet).variants("abcd")));

    assertEquals(set(expected), set(compose(step).variants("abcd")));

    expected.addAll(set(concat(transform(expected, new Function<String, Iterable<String>>() {
      @Override
      public Iterable<String> apply(String input) {
        return step.variants(input);
      }
    }))));

    assertEquals(set(compose(step, step).variants("abcd")), expected);
  }

  public static Set<String> set(String... word) {
    return set(asList(word));
  }

  public static Set<String> set(Iterable<? extends String> iterable) {
    return ImmutableSet.copyOf(iterable);
  }
}
