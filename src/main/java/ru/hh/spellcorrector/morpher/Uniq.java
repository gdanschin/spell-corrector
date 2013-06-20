package ru.hh.spellcorrector.morpher;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

class Uniq extends Morpher {

  private final Morpher morpher;

  Uniq(Morpher morpher) {
    this.morpher = Preconditions.checkNotNull(morpher);
  }

  @Override
  public Iterable<String> variants(String source) {
    return ImmutableSet.copyOf(morpher.variants(source));
  }
}
