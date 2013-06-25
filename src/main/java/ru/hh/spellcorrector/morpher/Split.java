package ru.hh.spellcorrector.morpher;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Partition;
import ru.hh.spellcorrector.Phrase;
import java.util.Iterator;
import static java.util.Arrays.asList;
import static ru.hh.spellcorrector.Utils.stringPartitions;

class Split extends Morpher {

  protected final double multiplier;

  Split() {
    this(0.1);
  }

  Split(double multiplier) {
    this.multiplier = multiplier;
  }


  @Override
  public Iterable<Correction> corrections(final Correction source) {
    final double weight = source.getWeight() * multiplier;

    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new SplitIterator(source, weight);
      }
    };
  }

  class SplitIterator extends AbstractIterator<Correction> {

    private final Phrase phrase;
    private int index = -1;
    private Iterator<Partition> variants = Iterators.emptyIterator();
    private final double weight;

    public SplitIterator(Correction correction, double weight) {
      this.phrase = correction.getPhrase();
      this.weight = weight;
    }

    @Override
    protected Correction computeNext() {
      if (variants.hasNext()) {
        Partition variant = variants.next();
        return Correction.of(phrase.replace(variant, index), weight);
      }
      return nextWord();
    }

    private Correction nextWord() {
      if (++index < phrase.size()) {
        variants = stringPartitions(phrase.getWord(index))
            .filter(new Predicate<Partition>() {
              @Override
              public boolean apply(Partition input) {
                return input.left().length() > 0 && input.right().length() > 0;
              }
            })
            .iterator();
        return computeNext();
      }
      return endOfData();
    }
  }
}
