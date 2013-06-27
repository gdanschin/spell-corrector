package ru.hh.spellcorrector;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Queue;

public class Itertools {

  public static <T> Iterable<T> zipAndConcat(final Iterable<? extends Iterable<? extends T>> iterables) {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return zipAndConcat(iterators(iterables));
      }
    };
  }

  public static <T> Iterator<T> zipAndConcat(Iterator<? extends Iterator<? extends T>> iterators) {
    return new ZipIterator<T>(iterators);
  }

  static class ZipIterator<T> extends AbstractIterator<T> {

    final Iterator<? extends Iterator<? extends T>> iterators;
    final Queue<Iterator<? extends T>> queue = Lists.newLinkedList();

    ZipIterator(Iterator<? extends Iterator<? extends T>> iterators) {
      this.iterators = iterators;
    }

    @Override
    protected T computeNext() {
      Iterator<? extends T> iterator;

      while (iterators.hasNext() && ((iterator = iterators.next()) != null)) {
        if (iterator.hasNext()) {
          return queueAndReturn(iterator);
        }
      }

      while ((iterator = queue.poll()) != null) {
        if (iterator.hasNext()) {
          return queueAndReturn(iterator);
        }
      }

      return endOfData();
    }

    T queueAndReturn(Iterator<? extends T> iterator) {
      T next = iterator.next();
      queue.add(iterator);
      return next;
    }
  }

  private static <T> Iterator<Iterator<? extends T>> iterators(Iterable<? extends Iterable<? extends T>> iterables) {
    final Iterator<? extends Iterable<? extends T>> iterableIterator = iterables.iterator();
    return new UnmodifiableIterator<Iterator<? extends T>>() {

      @Override
      public boolean hasNext() {
        return iterableIterator.hasNext();
      }

      @Override
      public Iterator<? extends T> next() {
        return iterableIterator.next().iterator();
      }
    };
  }

  public static <T> Iterable<T> memorizeIterable(final Iterable<T> iterable) {
    return memorizeIterator(iterable.iterator());
  }

  public static <T> Iterable<T> memorizeIterator(Iterator<T> iterator) {
    return CachingIterable.from(iterator);
  }

}
