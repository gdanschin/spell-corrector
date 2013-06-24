package ru.hh.spellcorrector.morpher;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Partition;
import ru.hh.spellcorrector.Utils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    private final List<String> words;
    private int index = -1;
    private Iterator<Partition> variants;
    private final double weight;

    public SplitIterator(Correction correction, double weight) {
      this.words = correction.getWords();
      this.weight = weight;
    }

    @Override
    public Correction computeNext() {
      while ((variants != null && variants.hasNext()) || index < words.size()) {
        if (variants != null && variants.hasNext()) {
          return makeCorrection(variants.next());
        }
        if (++index < words.size()) {
          variants = Iterables.filter(Utils.stringPartitions(words.get(index)), new Predicate<Partition>() {
            @Override
            public boolean apply(Partition input) {
              return input.left().length() > 0 && input.right().length() > 0;
            }
          }).iterator();
        }
      }

      return endOfData();
    }

    Correction makeCorrection(Partition variant) {
      List<String> newWords = Lists.newArrayList(words);
      newWords.remove(index);
      newWords.addAll(index, Arrays.asList(variant.left(), variant.right()));
      return Correction.of(ImmutableList.copyOf(newWords), weight);
    }
  }
}
