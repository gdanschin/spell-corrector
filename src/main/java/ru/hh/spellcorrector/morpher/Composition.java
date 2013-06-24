package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.hh.spellcorrector.Correction;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Composition extends Morpher {

  private final Iterable<? extends Morpher> morphers;
  private final boolean memorizeLast;

  Composition(Iterable<? extends Morpher> morpher, boolean memorizeLast) {
    this.morphers = morpher;
    this.memorizeLast = memorizeLast;
  }

  @Override
  public Iterable<Correction> corrections(final Correction source) {
    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new PowerIterator(source);
      }
    };
  }

  private class PowerIterator extends AbstractIterator<Correction> {

    final Set<List<String>> passedElements = Sets.newHashSet();
    final Iterator<? extends Morpher> morpherIt = morphers.iterator();

    Iterator<Correction> source;
    Queue<Correction> level = Lists.newLinkedList();
    boolean memorize = true;

    public PowerIterator(Correction source) {
      this.source = Iterators.forArray(source);
    }

    @Override
    protected Correction computeNext() {
      while (morpherIt.hasNext() || source.hasNext()) {

        while (source.hasNext()) {
          Correction variant = source.next();
          if (!passedElements.contains(variant.getWords())) {
            if (memorize || memorizeLast) {
              passedElements.add(variant.getWords());
              level.add(variant);
            }

            return variant;
          }
        }

        if (morpherIt.hasNext()) {
          final Morpher morpher = morpherIt.next();
          memorize = morpherIt.hasNext();

          source = morpher.corrections(level).iterator();
          level = Lists.newLinkedList();
        }
      }

      return endOfData();
    }
  }
}
