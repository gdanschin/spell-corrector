package ru.hh.spellcorrector;

import com.google.common.base.Optional;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class PrecomputationalIterator<T> implements Iterator<T> {

  private Optional<T> current = Optional.absent();

  @Override
  public final boolean hasNext() {
    return current.isPresent();
  }

  @Override
  public final T next() {
    if (!current.isPresent()) {
      throw new NoSuchElementException();
    }

    T result = current.get();
    nextStep();
    return result;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  protected final void setCurrent(T value) {
    current = Optional.of(value);
  }

  protected void stopIteration() {
    current = Optional.absent();
  }

  protected abstract void nextStep();
}
