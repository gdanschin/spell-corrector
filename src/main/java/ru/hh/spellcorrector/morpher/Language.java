package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.dict.Dictionary;

import java.util.Arrays;

class Language extends Morpher {

  private final Dictionary dict;
  private final double power;

  public Language(Dictionary dict, double power)  {
    this.dict = dict;
    this.power = power;
  }

  @Override
  public Iterable<Correction> corrections(Correction source) {
    return corrections(Arrays.asList(source));
  }

  @Override
  public Iterable<Correction> corrections(Iterable<Correction> sources) {
    return Iterables.transform(Iterables.filter(sources, new Predicate<Correction>() {
          @Override
          public boolean apply(Correction input) {
            return dict.isKnown(input.getText());
          }
        }), new Function<Correction, Correction>() {
      @Override
      public Correction apply(Correction input) {
        return Correction.of(input.getText(), input.getWeight() * Math.pow(dict.getFreq(input.getText()), power));
      }
    });
  }
}
