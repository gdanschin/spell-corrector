package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
  public Iterable<String> variants(final String source) {
    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new PowerIterator(source);
      }
    };
  }

  private class PowerIterator implements Iterator<String> {

    final Set<String> passedElements = Sets.newHashSet();
    final Iterator<? extends Morpher> morpherIt = morphers.iterator();

    Iterator<String> source;
    Queue<String> level = Lists.newLinkedList();
    Optional<String> current;
    boolean memorize = true;

    public PowerIterator(String source) {
      this.source = Iterators.forArray(source);
      nextStep();
    }

    @Override
    public boolean hasNext() {
      return current.isPresent();
    }

    @Override
    public String next() {
      if (!current.isPresent()) {
        throw new NoSuchElementException();
      }
      String result = current.get();
      nextStep();
      return result;
    }

    private void nextStep() {
      while (morpherIt.hasNext() || source.hasNext()) {

        while (source.hasNext()) {
          String variant = source.next();
          if (!passedElements.contains(variant)) {
            if (memorize || memorizeLast) {
              passedElements.add(variant);
              level.add(variant);
            }
            current = Optional.of(variant);
            return;
          }
        }

        if (morpherIt.hasNext()) {
          final Morpher morpher = morpherIt.next();

          memorize = morpherIt.hasNext();
          source = Iterators.concat(Iterators.transform(level.iterator(), new Function<String, Iterator<String>>() {
            @Override
            public Iterator<String> apply(String input) {
              return morpher.variants(input).iterator();
            }
          }));
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
