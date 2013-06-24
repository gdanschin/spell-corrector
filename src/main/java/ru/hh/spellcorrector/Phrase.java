package ru.hh.spellcorrector;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.advance;
import static com.google.common.collect.Iterators.limit;
import static java.util.Arrays.asList;

public class Phrase {

  private final List<String> words;

  private Phrase(List<String> words) {
    this.words = words;
  }

  public static Phrase of(String word) {
    return of(asList(checkNotNull(word)));
  }

  public static Phrase of(List<String> words) {
    checkNotNull(words);
    checkArgument(words.size() > 0);
    return new Phrase(words);
  }

  public Phrase replace(String replace, int index) {
    return replace(asList(checkNotNull(replace)), index);
  }

  public Phrase replace(Iterable<String> replace, int index) {
    checkArgument(index >= 0 && index < words.size());
    checkNotNull(replace);

    ImmutableList.Builder builder = ImmutableList.builder();
    Iterator<String> wordIterator = words.iterator();

    builder.addAll(limit(wordIterator, index));
    builder.addAll(replace);
    advance(wordIterator, 1);
    builder.addAll(wordIterator);

    return new Phrase(builder.build());
  }

  public List<String> getWords() {
    return words;
  }

  public int size() {
    return words.size();
  }

  public String getWord(int index) {
    return words.get(index);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return words.equals(((Phrase) o).words);
  }

  @Override
  public int hashCode() {
    return words.hashCode();
  }

  private static final Joiner JOINER = Joiner.on(" ");

  @Override
  public String toString() {
    return words.size() > 0 ? "\"" + JOINER.join(words) + "\"" : words.iterator().next();
  }
}
