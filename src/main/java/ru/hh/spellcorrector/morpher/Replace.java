package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ru.hh.spellcorrector.Partition;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static ru.hh.spellcorrector.Utils.stringPartitions;

class Replace extends FixedMultiplierMorpher {

  private final ImmutableList<Character> alphabet;

  Replace(String alphabet) {
    this.alphabet = Lists.charactersOf(alphabet);
  }

  @Override
  public Iterable<String> variants(final String source) {
    return
        filter(
            concat(
                transform(
                    filter(stringPartitions(source),
                        new Predicate<Partition>() {
                          @Override
                          public boolean apply(Partition input) {
                            return input.right().length() > 0;
                          }
                        }
                    ),
                    new Function<Partition, Iterable<String>>() {
                      @Override
                      public Iterable<String> apply(final Partition partition) {
                        return transform(alphabet, new Function<Character, String>() {
                          @Override
                          public String apply(Character character) {
                            return partition.left() + character + partition.right().substring(1);
                          }
                        });
                      }
                    }
                )
            ),
            new Predicate<String>() {
              @Override
              public boolean apply(String input) {
                return !input.equals(source);
              }
            }
        );
  }

}
