package ru.hh.spellcorrector.morpher;

public class Lower extends StringTransform {

  public Lower() {
    super(1.0);
  }

  @Override
  protected Iterable<String> variants(String word) {
    throw new UnsupportedOperationException();
  }

}
