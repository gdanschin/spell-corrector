package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ru.hh.spellcorrector.Correction;
import java.util.Iterator;
import java.util.List;

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
        return new MultiIterator(source, weight);
      }
    };
  }


  class MultiIterator extends AbstractIterator<Correction> {

    private final List<String> words;
    private int index = -1;
    private Iterator<String> variants;
    private final double weight;

    public MultiIterator(Correction correction, double weight) {
      this.words = Lists.newArrayList(correction.getWords());
      this.weight = weight;
    }

    @Override
    public Correction computeNext() {
      while ((variants != null && variants.hasNext()) || index < words.size()) {
        if (variants != null && variants.hasNext()) {
          return makeCorrection(variants.next());
        }
        if (++index < words.size()) {
          variants = variants(words.get(index)).iterator();
        }
      }

      return endOfData();
    }

    Correction makeCorrection(String variant) {
      String previous = words.get(index);
      words.set(index, variant);
      Correction result = Correction.of(ImmutableList.copyOf(words), weight);
      words.set(index, previous);
      return result;
    }
  }

}
