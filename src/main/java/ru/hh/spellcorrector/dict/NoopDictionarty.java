package ru.hh.spellcorrector.dict;

public class NoopDictionarty implements Dictionary {
  @Override
  public int getFreq(String word) {
    return 1;
  }

  @Override
  public boolean isKnown(String word) {
    return true;
  }
}
