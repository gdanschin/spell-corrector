package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;

import java.util.Collections;
import java.util.Iterator;

class Delete extends StringTransform {

  private static final Morpher INSTANCE = new Delete();

  private Delete() {
  }

  static Morpher instance() {
    return INSTANCE;
  }

  @Override
  protected Iterable<String> variants(final String source) {
    if (source.length() <= 1) {
      return Collections.emptyList();
    }
    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new DeleteIterator(source);
      }
    };
  }

  class DeleteIterator extends AbstractIterator<String> {

    final char[] source;
    final char[] raw;
    int index = -1;

    DeleteIterator(String source) {
      this.source = source.toCharArray();
      this.raw = new char[source.length() - 1];
    }

    @Override
    protected String computeNext() {
      if (++index >= source.length) {
        return endOfData();
      }

      System.arraycopy(source, 0, raw, 0, index);
      System.arraycopy(source, index + 1, raw, index, raw.length - index);

      return new String(raw);
    }
  }
}
