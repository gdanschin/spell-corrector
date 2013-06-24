package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ru.hh.spellcorrector.Partition;
import static ru.hh.spellcorrector.Utils.stringPartitions;

class Insert extends StringTransform {

  private final ImmutableList<Character> alphabet;

  Insert(String alphabet) {
    this.alphabet = Lists.charactersOf(alphabet);
  }

  @Override
  protected Iterable<String> variants(String source) {
    return stringPartitions(source)
        .transformAndConcat(
            new Function<Partition, Iterable<String>>() {
              @Override
              public Iterable<String> apply(final Partition partition) {
                return Iterables.transform(alphabet, new Function<Character, String>() {
                  @Override
                  public String apply(Character character) {
                    return partition.left() + character + partition.right();
                  }
                });
              }
            });
  }
}
