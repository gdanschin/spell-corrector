package ru.hh.spellcorrector.morpher;

import ru.hh.spellcorrector.Correction;

import java.util.Collections;

class Identity extends Morpher {

  private static final Identity INSTANCE = new Identity();

  static Identity instance() {
    return INSTANCE;
  }

  private Identity() {
  }

  @Override
  public Iterable<Correction> corrections(Correction source) {
    return Collections.singleton(source);
  }

  @Override
  public Iterable<Correction> corrections(Iterable<Correction> sources) {
    return sources;
  }
}
