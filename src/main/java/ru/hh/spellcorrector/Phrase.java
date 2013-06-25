package ru.hh.spellcorrector;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.List;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
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
    checkArgument(index >= 0 && index < words.size());

    if (singeWord()) {
      return Phrase.of(replace);
    }

    List<String> phrase = Lists.newArrayList(words);
    phrase.set(index, replace);
    return Phrase.of(phrase);
  }

  public Phrase replace(Partition partition, int index) {
    checkArgument(index >= 0 && index < words.size());

    if (singeWord()) {
      return Phrase.of(asList(partition.left(), partition.right()));
    }

    List<String> phrase = Lists.newArrayList(words);
    phrase.set(index, partition.left());
    phrase.add(index + 1, partition.right());
    return Phrase.of(phrase);
  }

  private boolean singeWord() {
    return words.size() == 1;
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
