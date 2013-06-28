package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Phrase;
import java.util.Set;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static ru.hh.spellcorrector.morpher.Morphers.*;
import static ru.hh.spellcorrector.TreeNode.node;

public class MorpherTest {

  @Test
  public void deleteTest() {
    assertEquals(correctionSet(delete().corrections(phrase("abcd"))), singlePhrases("abc", "abd", "acd", "bcd"));
  }

  @Test
  public void transposeTest() {
    assertEquals(correctionSet(transpose().corrections(phrase("abcd"))), singlePhrases("abdc", "acbd", "bacd"));
  }

  @Test
  public void insertTest() {
    assertEquals(correctionSet(testInsert("XYZ").corrections(phrase("abcd"))), singlePhrases(
        "abcdX", "abcdY", "abcdZ",
        "abcXd", "abcYd", "abcZd",
        "abXcd", "abYcd", "abZcd",
        "aXbcd", "aYbcd", "aZbcd",
        "Xabcd", "Yabcd", "Zabcd"

    ));
  }

  @Test
  public void replaceTest() {
    assertEquals(correctionSet(testReplace("XYZ").corrections(phrase("abcd"))), singlePhrases(
        "abcX", "abcY", "abcZ",
        "abXd", "abYd", "abZd",
        "aXcd", "aYcd", "aZcd",
        "Xbcd", "Ybcd", "Zbcd"

    ));
  }

  @Test
  public void splitTest() {
    assertEquals(correctionSet(split().corrections(phrase("abcd"))), set(
        phrase("a", "bcd"), phrase("ab", "cd"), phrase("abc", "d")
    ));
  }

  @Test
  public void levenshteinTest() {
    assertEquals(correctionSet(testLevensteinStep("XYZ").corrections(phrase("abcd"))), set(
        phrase("abc"), phrase("abd"), phrase("acd"), phrase("bcd"),
        phrase("abdc"), phrase("acbd"), phrase("bacd"),
        phrase("abcdX"), phrase("abcdY"), phrase("abcdZ"),
        phrase("abcXd"), phrase("abcYd"), phrase("abcZd"),
        phrase("abXcd"), phrase("abYcd"), phrase("abZcd"),
        phrase("aXbcd"), phrase("aYbcd"), phrase("aZbcd"),
        phrase("Xabcd"), phrase("Yabcd"), phrase("Zabcd"),
        phrase("abcX"), phrase("abcY"), phrase("abcZ"),
        phrase("abXd"), phrase("abYd"), phrase("abZd"),
        phrase("aXcd"), phrase("aYcd"), phrase("aZcd"),
        phrase("Xbcd"), phrase("Ybcd"), phrase("Zbcd"),
        phrase("a", "bcd"), phrase("ab", "cd"), phrase("abc", "d")
    ));
  }

  @Test
  public void optimizeLevenshteinTest() {
    final Morpher step = sum(delete(), transpose(), insert(), replace(), split());

    Morpher optimized =  tree(node(identity(),
        node(delete(), delete(), transpose(), replace(), insert(), split()),
        node(transpose(), transpose(), replace(), insert(), split()),
        node(replace(), replace(), insert(), split()),
        node(insert(), insert(), split()),
        node(split(), split()))
    );


    Set<Phrase> expected = Sets.newHashSet(phrase("abcd"));
    expected.addAll(correctionSet(step.corrections(phrase("abcd"))));
    expected.addAll(correctionSet(concat(transform(expected, new Function<Phrase, Iterable<Correction>>() {
      @Override
      public Iterable<Correction> apply(Phrase input) {
        return step.corrections(input);
      }
    }))));

    assertEquals(correctionSet(optimized.corrections(phrase("abcd"))), expected);
  }

  public static Set<Phrase> correctionSet(Iterable<? extends Correction> corrections) {
    return FluentIterable.from(corrections)
        .transform(new Function<Correction, Phrase>() {
          @Override
          public Phrase apply(Correction input) {
            return input.getPhrase();
          }
        })
        .toSet();
  }

  public static Set<Phrase> singlePhrases(String... words) {
    return singlePhrases(asList(words));
  }

  public static Set<Phrase> singlePhrases(Iterable<? extends String> iterable) {
    return FluentIterable.from(iterable)
        .transform(new Function<String, Phrase>() {
          @Override
          public Phrase apply(String input) {
            return Phrase.of(input);
          }
        })
        .toSet();
  }

  public static Phrase phrase(String... words) {
    return Phrase.of(asList(words));
  }

  public static Set<Phrase> set(Phrase... phrases) {
    return set(asList(phrases));
  }

  public static Set<Phrase> set(Iterable<? extends Phrase> iterable) {
    return ImmutableSet.copyOf(iterable);
  }
}
