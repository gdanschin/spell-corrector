package ru.hh.spellcorrector;
import static com.google.common.base.Preconditions.checkNotNull;

public class Correction {

  private final Phrase phrase;
  private final double weight;

  public Correction(Phrase phrase, double weight) {
    this.phrase = checkNotNull(phrase);
    this.weight = weight;
  }

  public static Correction of(String text) {
    return of(text, 1);
  }

  public static Correction of(String text, double weight) {
    return new Correction(Phrase.of(text), weight);
  }

  public static Correction of (Phrase phrase, double weight) {
    return new Correction(phrase, weight);
  }

  public Phrase getPhrase() {
    return phrase;
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
    return (weight == that.weight) && phrase.equals(that.phrase);
  }

  @Override
  public int hashCode() {
    return 31 * phrase.hashCode() + Double.valueOf(weight).hashCode();
  }

  @Override
  public String toString() {
    return phrase.toString() + ":" + weight;
  }
}
