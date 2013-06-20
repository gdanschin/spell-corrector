package ru.hh.spellcorrector;

import com.google.common.base.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Correction {

  public final String text;
  public final Integer distance;

  public Correction(String text, Integer distance) {
    this.text = checkNotNull(text);
    this.distance = checkNotNull(distance);
  }

  public static Correction of(String text, Integer distance) {
    return new Correction(text, distance);
  }

  public static final Function<Correction, String> TEXT = new Function<Correction, String>() {
    @Override
    public String apply(Correction input) {
      return input.text;
    }
  };

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Correction that = (Correction) o;

    return text.equals(that.text);
  }

  @Override
  public int hashCode() {
    return text.hashCode();
  }
}
