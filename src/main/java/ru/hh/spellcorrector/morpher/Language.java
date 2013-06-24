package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.PrecomputationalIterator;
import ru.hh.spellcorrector.dict.Dictionary;

import java.util.Arrays;
import java.util.Iterator;

class Language extends Morpher {

  private final Dictionary dict;
  private final double power;
  private final double norm;
  private final boolean shortCircuit;

  public Language(Dictionary dict, double power, boolean shortCircuit)  {
    this.dict = dict;
    this.power = power;
    this.norm = Math.pow(dict.maxFreq(), power);
    this.shortCircuit = shortCircuit;
  }

  @Override
  public Iterable<Correction> corrections(Correction source) {
    return corrections(Arrays.asList(source));
  }

  final Predicate<Correction> fromDictionary = new Predicate<Correction>() {
    @Override
    public boolean apply(Correction input) {
      return Iterables.all(input.getWords(), new Predicate<String>() {
        @Override
        public boolean apply(String input) {
          return dict.isKnown(input);
        }
      });
    }
  };

  final Function<Correction, Correction> modifyWeight = new Function<Correction, Correction>() {
    @Override
    public Correction apply(Correction input) {
      double weight = norm;
      for (String word: input.getWords()) {
        weight *= Math.pow(dict.getFreq(word), power) / norm;
      }

      return Correction.of(input.getWords(), input.getWeight() * weight);
    }
  };

  @Override
  public Iterable<Correction> corrections(final Iterable<Correction> sources) {
    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new LangIterator(sources);
      }
    };
  }

  class LangIterator extends PrecomputationalIterator<Correction> {

    double maxWeight = 0;

    private final Iterator<Correction> source;
    long steps = 0;

    public LangIterator(Iterable<Correction> source) {
      this.source = source.iterator();
      nextStep();
    }

    @Override
    protected void nextStep() {
      while (source.hasNext()) {
        steps++;
        Correction next = source.next();

        if (shortCircuit && next.getWeight() * norm < maxWeight) {
          stopIteration();
          return;
        }

        if (fromDictionary.apply(next)) {
          Correction cur = modifyWeight.apply(next);
          setCurrent(cur);
          maxWeight = Math.max(cur.getWeight(), maxWeight);
          return;
        }
      }
      stopIteration();
    }

    @Override
    protected void stopIteration() {
      super.stopIteration();
      System.out.println(steps);
    }
  }
}
