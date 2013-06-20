package ru.hh.spellcorrector.dict;

public interface Dictionary {

  public int getFreq(String word);

  public boolean isKnown(String word);

}
