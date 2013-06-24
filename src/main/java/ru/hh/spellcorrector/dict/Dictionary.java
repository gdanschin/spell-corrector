package ru.hh.spellcorrector.dict;

public interface Dictionary {

  public double getFreq(String word);

  public boolean isKnown(String word);

  public double maxFreq();
}
