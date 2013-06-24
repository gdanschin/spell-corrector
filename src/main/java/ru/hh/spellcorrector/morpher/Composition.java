package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Phrase;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

class Composition extends Morpher {

  private final Iterable<? extends Morpher> morphers;
  private final boolean memorizeLast;

  Composition(Iterable<? extends Morpher> morpher, boolean memorizeLast) {
    this.morphers = morpher;
    this.memorizeLast = memorizeLast;
  }

  @Override
  public Iterable<Correction> corrections(final Correction source) {
    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new PowerIterator(source);
      }
    };
  }

  private class PowerIterator extends AbstractIterator<Correction> {

    final Set<Phrase> passedPhrases = Sets.newHashSet();     //used for uniq
    final Iterator<? extends Morpher> morpherIt = morphers.iterator();

    Iterator<Correction> corrections;
    Queue<Correction> currentLevel = Lists.newLinkedList();  //used to construct new level
    boolean lastLevel = false;

    public PowerIterator(Correction source) {
      this.corrections = Iterators.forArray(source);
    }

    @Override
    protected Correction computeNext() {
      while (corrections.hasNext()) {
        Correction correction = corrections.next();
        if (!passedPhrases.contains(correction.getPhrase())) {
          if (!lastLevel || memorizeLast) {
            passedPhrases.add(correction.getPhrase());
            currentLevel.add(correction);
          }

          return correction;
        }
      }

      return nextLevel();
    }

    private Correction nextLevel() {
      if (!morpherIt.hasNext()) {
        return endOfData();
      }

      Morpher morpher = morpherIt.next();
      lastLevel = !morpherIt.hasNext();
      corrections = morpher.corrections(currentLevel).iterator();
      currentLevel = Lists.newLinkedList();

      return computeNext();
    }
  }
}
