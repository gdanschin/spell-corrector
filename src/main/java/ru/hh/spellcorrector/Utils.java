package ru.hh.spellcorrector;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Iterator;

import static com.google.common.collect.Iterables.transform;

public class Utils {

  public static Iterable<Partition> stringPartitions(final String string) {
    Preconditions.checkNotNull(string);
    return new Iterable<Partition>() {
      @Override
      public Iterator<Partition> iterator() {
        return new PartitionIterator(string);
      }
    };
  }

  private static class PartitionIterator implements Iterator<Partition> {

    private final String string;
    private int position;

    public PartitionIterator(String string) {
      this.string = string;
    }


    @Override
    public boolean hasNext() {
      return position <= string.length();
    }

    @Override
    public Partition next() {
      Partition partition = Partition.of(string.substring(0, position), string.substring(position));
      position++;
      return partition;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public static <F, T> Iterable<T> applySeveral(
      Iterable<? extends Function<? super F, ? extends T>> functions, final F argument) {
    return transform(functions, new Function<Function<? super F, ? extends T>, T>() {
      @Override
      public T apply(Function<? super F, ? extends T> function) {
        return function.apply(argument);
      }
    });
  }

}
