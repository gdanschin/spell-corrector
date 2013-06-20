package ru.hh.spellcorrector;

import com.google.common.base.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Correction {

  private final String text;
  private final double weight;

  public Correction(String text, double distance) {
    this.text = checkNotNull(text);
    this.weight = checkNotNull(distance);
  }

  public static Correction of(String text) {
    return of(text, 0);
  }

  public static Correction of(String text, double distance) {
    return new Correction(text, distance);
  }

  public String getText() {
    return text;
  }

  public double getWeight() {
    return weight;
  }

  public static final Function<Correction, String> TEXT = new Function<Correction, String>() {
    @Override
    public String apply(Correction input) {
      return input.text;
    }
  };

  public static Function<Correction, Correction> alterDistance(final Function<Double, Double> distanceFunction) {
    return new Function<Correction, Correction>() {
      @Override
      public Correction apply(Correction input) {
        return Correction.of(input.text, distanceFunction.apply(input.weight));
      }
    };
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

    return text.equals(that.text) && (weight == that.weight);
  }

  @Override
  public int hashCode() {
    return 31 * text.hashCode() + Double.valueOf(weight).hashCode();
  }

  @Override
  public String toString() {
    return weight > 0 ? text + ":" + weight : text;
  }
}
