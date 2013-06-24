package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import ru.hh.spellcorrector.Correction;

class Sum extends Morpher {

  private final FluentIterable<? extends Morpher> children;

  public Sum(Iterable<? extends Morpher> children) {
    this.children = FluentIterable.from(children);
  }

  @Override
  public Iterable<Correction> corrections(final Correction source) {
    return children.transformAndConcat(new Function<Morpher, Iterable<? extends Correction>>() {
      @Override
      public Iterable<Correction> apply(Morpher input) {
        return input.corrections(source);
      }
    });
  }
}
