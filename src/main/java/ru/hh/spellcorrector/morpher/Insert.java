package ru.hh.spellcorrector.morpher;

import com.google.common.base.Optional;
import com.google.common.collect.AbstractIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

class Insert extends StringTransform {

  private final Charsets charsets;

  Insert(Charsets charsets) {
    this.charsets = charsets;
  }

  @Override
  protected Iterable<String> variants(final String source) {
    final Optional<Set<Character>> charset = charsets.guess(source);

    if (!charset.isPresent()) {
      return Collections.emptyList();
    }

    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new InsertIterator(source, charset.get());
      }
    };
  }

  static class InsertIterator extends AbstractIterator<String> {

    final char[] source;
    final char[] raw;

    int index = 0;

    final Set<Character> charset;
    Iterator<Character> charIterator;

    InsertIterator(String source, Set<Character> charset) {
      this.source = source.toCharArray();
      raw = new char[this.source.length + 1];
      this.charset = charset;

      updateState();
    }

    @Override
    protected String computeNext() {
      if (charIterator.hasNext()) {
        raw[index] = charIterator.next();
        return new String(raw);
      }
      return nextIndex();
    }

    private String nextIndex() {
      if (++index >= raw.length) {
        return endOfData();
      }
      updateState();
      return computeNext();
    }

    private void updateState() {
      System.arraycopy(source, 0, raw, 0, index);
      System.arraycopy(source, index, raw, index + 1, source.length - index);
      charIterator = charset.iterator();
    }
  }
}
