package ru.hh.spellcorrector.morpher;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.hh.spellcorrector.Correction;

import java.util.Iterator;
import java.util.NoSuchElementException;
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

  private class PowerIterator implements Iterator<Correction> {

    final Set<String> passedElements = Sets.newHashSet();
    final Iterator<? extends Morpher> morpherIt = morphers.iterator();

    Iterator<Correction> source;
    Queue<Correction> level = Lists.newLinkedList();
    Optional<Correction> current;
    boolean memorize = true;

    public PowerIterator(Correction source) {
      this.source = Iterators.forArray(source);
      nextStep();
    }

    @Override
    public boolean hasNext() {
      return current.isPresent();
    }

    @Override
    public Correction next() {
      if (!current.isPresent()) {
        throw new NoSuchElementException();
      }
      Correction result = current.get();
      nextStep();
      return result;
    }

    private void nextStep() {
      while (morpherIt.hasNext() || source.hasNext()) {

        while (source.hasNext()) {
          Correction variant = source.next();
          if (!passedElements.contains(variant.getText())) {
            if (memorize || memorizeLast) {
              passedElements.add(variant.getText());
              level.add(variant);
            }
            current = Optional.of(variant);
            return;
          }
        }

        if (morpherIt.hasNext()) {
          final Morpher morpher = morpherIt.next();
          memorize = morpherIt.hasNext();

          source = morpher.corrections(level).iterator();
          level = Lists.newLinkedList();
        }
      }

      current = Optional.absent();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
