package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import ru.hh.spellcorrector.Correction;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

class Sum extends Morpher {

  private final Iterable<? extends Morpher> children;

  public Sum(Iterable<? extends Morpher> children) {
    this.children = children;
  }

  @Override
  public Iterable<Correction> corrections(final Correction source) {
    return concat(
        transform(
            children,
            new Function<Morpher, Iterable<Correction>>() {
              @Override
              public Iterable<Correction> apply(Morpher input) {
                return input.corrections(source);
              }
            }));
  }
}
