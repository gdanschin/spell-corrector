package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

class Transpose extends StringTransform {

  private static final Morpher INSTANCE = new Transpose();

  private Transpose() { }

  static Morpher instance() {
    return INSTANCE;
  }

  @Override
  protected Iterable<String> variants(final String source) {
    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new TransposeIterator(source);
      }
    };
  }

  class TransposeIterator extends AbstractIterator<String> {

    final char[] raw;
    int index = 0;

    TransposeIterator(String source) {
      raw = source.toCharArray();
    }

    private void swap(int first, int second) {
      char temp = raw[first];
      raw[first] = raw[second];
      raw[second] = temp;
    }

    @Override
    protected String computeNext() {
      if (index > 0) {
        swap(index, index - 1);
      }
      if (++index >= raw.length) {
        return endOfData();
      }
      swap(index, index - 1);
      return new String(raw);
    }
  }
}
