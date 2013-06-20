package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import ru.hh.spellcorrector.Correction;

import static com.google.common.collect.Iterables.transform;

abstract class FixedMultiplierMorpher extends Morpher {

  protected final double multiplier;

  FixedMultiplierMorpher() {
    this(0.1);
  }

  FixedMultiplierMorpher(double multiplier) {
    this.multiplier = multiplier;
  }

  protected abstract Iterable<String> variants(String source);

  public final Iterable<Correction> corrections(final Correction source) {
    final double distance = source.getWeight() * multiplier;

    return transform(variants(source.getText()), new Function<String, Correction>() {
      @Override
      public Correction apply(String modifiedText) {
        return Correction.of(modifiedText, distance);
      }
    });
  }

}
