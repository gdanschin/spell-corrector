package ru.hh.spellcorrector;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Iterators.unmodifiableIterator;

public class CachingIterable<T> implements Iterable<T> {

  public static <T> CachingIterable<T> from(Iterator<? extends T> source) {
    return new CachingIterable<T>(source);
  }

  private final Iterator<? extends T> source;
  private List<T> storage = Lists.newArrayList();
  private Master master = new Master();

  CachingIterable(Iterator<? extends T> source) {
    this.source = source;
  }

  @Override
  public Iterator<T> iterator() {
    return master.hasNext() ? new Slave() : unmodifiableIterator(storage.iterator());
  }

  public boolean isFetched() {
    return master.hasNext();
  }

  public void prefetch() {
    while (master.hasNext()) {
      master.next();
    }
  }

  private class Master extends UnmodifiableIterator<T> {
    private boolean hasNext = true;

    @Override
    public boolean hasNext() {
      if (hasNext) {
        hasNext = source.hasNext();
      }
      return hasNext;
    }

    @Override
    public T next() {
      T next = source.next();
      storage.add(next);
      return next;
    }
  }

  private class Slave extends UnmodifiableIterator<T> {

    int index = 0;

    @Override
    public boolean hasNext() {
      return index < storage.size() || master.hasNext();
    }

    @Override
    public T next() {
      return incAndRet(index < storage.size() ? storage.get(index) : master.next());
    }

    T incAndRet(T val) {
      index++;
      return val;
    }
  }
}
