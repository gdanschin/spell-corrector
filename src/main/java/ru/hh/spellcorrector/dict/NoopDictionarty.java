package ru.hh.spellcorrector.dict;

public class NoopDictionarty implements Dictionary {
  @Override
  public double getFreq(String word) {
    return 1;
  }

  @Override
  public boolean isKnown(String word) {
    return true;
  }

  @Override
  public double maxFreq() {
    return 1;
  }
}
