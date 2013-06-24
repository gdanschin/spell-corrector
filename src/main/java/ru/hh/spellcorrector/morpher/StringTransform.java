package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Phrase;

import java.util.Iterator;

abstract class StringTransform extends Morpher {

  protected final double multiplier;

  StringTransform() {
    this(0.1);
  }

  StringTransform(double multiplier) {
    this.multiplier = multiplier;
  }

  protected abstract Iterable<String> variants(String word);

  public final Iterable<Correction> corrections(final Correction source) {
    final double weight = source.getWeight() * multiplier;

    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new CorrectionIterator(source, weight);
      }
    };
  }


  class CorrectionIterator extends AbstractIterator<Correction> {

    private final Phrase phrase;
    private final double weight;

    private int index = -1;
    private Iterator<String> wordTransform = Iterators.emptyIterator();

    public CorrectionIterator(Correction correction, double weight) {
      this.phrase = correction.getPhrase();
      this.weight = weight;
    }

    @Override
    public Correction computeNext() {
      if (wordTransform.hasNext()) {
        return Correction.of(phrase.replace(wordTransform.next(), index), weight);
      }
      return nextWord();
    }

    private Correction nextWord() {
      if (++index < phrase.size()) {
        wordTransform = variants(phrase.getWord(index)).iterator();
        return computeNext();
      }
      return endOfData();
    }
  }

}
