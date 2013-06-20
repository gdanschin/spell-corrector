package ru.hh.spellcorrector.morpher;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static ru.hh.spellcorrector.Utils.applySeveral;

class Sum extends Morpher {

  private final Iterable<? extends Morpher> children;

  public Sum(Iterable<? extends Morpher> children) {
    this.children = children;
  }

  @Override
  public Iterable<String> variants(String source) {
    return concat(applySeveral(transform(children, Morpher.toFunction()), source));
  }
}
