package ru.hh.spellcorrector;

import com.google.common.base.Function;
import java.util.Iterator;

public class Itertools {

  public static <T> CachingIterable<T> memorizeIterable(final Iterable<T> iterable) {
    return memorizeIterator(iterable.iterator());
  }

  public static <T> CachingIterable<T> memorizeIterator(Iterator<T> iterator) {
    return CachingIterable.from(iterator);
  }

  public static <T> Function<Iterable<T>, Iterator<T>> iterate() {
    return new Function<Iterable<T>, Iterator<T>>() {
      @Override
      public Iterator<T> apply(Iterable<T> input) {
        return input.iterator();
      }
    };
  }

}
