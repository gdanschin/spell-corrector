package ru.hh.spellcorrector.morpher;

import com.google.common.base.Optional;
import com.google.common.collect.AbstractIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

class Replace extends StringTransform {

  private final Charsets charsets;

  Replace(Charsets charsets) {
    this.charsets = charsets;
  }

  @Override
  public Iterable<String> variants(final String source) {
    final Optional<Set<Character>> charset = charsets.guess(source);

    if (!charset.isPresent()) {
      return Collections.emptyList();
    }

    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new ReplaceIterator(source, charset.get());
      }
    };
  }

  static class ReplaceIterator extends AbstractIterator<String> {

    final char[] raw;

    int index = 0;
    char current;

    final Set<Character> charset;
    Iterator<Character> charIterator;

    ReplaceIterator(String source, Set<Character> charset) {
      raw = source.toCharArray();
      this.charset = charset;
      updateState();
    }

    @Override
    protected String computeNext() {
      while (charIterator.hasNext()) {
        char next = charIterator.next();
        if (next != current) {
          raw[index] = next;
          return new String(raw);
        }
      }
      return nextIndex();
    }

    private String nextIndex() {
      raw[index] = current;
      if (++index >= raw.length) {
        return endOfData();
      }
      updateState();
      return computeNext();
    }

    private void updateState() {
      current = raw[index];
      charIterator = charset.iterator();
    }
  }

}
