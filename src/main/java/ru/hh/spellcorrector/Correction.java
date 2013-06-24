package ru.hh.spellcorrector;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Correction {

  private final List<String> words;
  private final double weight;

  public Correction(List<String> text, double distance) {
    checkArgument(checkNotNull(text).size() > 0);
    this.words = text;
    this.weight = checkNotNull(distance);
  }

  public static Correction of(String text) {
    return of(text, 0);
  }

  public static Correction of(String text, double distance) {
    return new Correction(Arrays.asList(text), distance);
  }

  public static Correction of(List<String> words, double distance) {
    return new Correction(words, distance);
  }

  public List<String> getWords() {
    return words;
  }

  public double getWeight() {
    return weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Correction that = (Correction) o;

    return words.equals(that.words) && (weight == that.weight);
  }

  @Override
  public int hashCode() {
    return 31 * words.hashCode() + Double.valueOf(weight).hashCode();
  }

  private static final Joiner joiner = Joiner.on(" ");

  @Override
  public String toString() {
    return (words.size() > 1 ? "\"" + joiner.join(words) + "\"" : words.get(0)) + ":" + weight;
  }
}
