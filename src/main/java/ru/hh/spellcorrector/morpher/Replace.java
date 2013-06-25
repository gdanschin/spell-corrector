package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import ru.hh.spellcorrector.Partition;
import java.util.Collections;
import java.util.Set;
import static ru.hh.spellcorrector.Utils.stringPartitions;

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

    return stringPartitions(source)
        .filter(new Predicate<Partition>() {
          @Override
          public boolean apply(Partition input) {
            return input.right().length() > 0;
          }
        })
        .transformAndConcat(new Function<Partition, Iterable<String>>() {
          @Override
          public Iterable<String> apply(final Partition partition) {
            return Iterables.transform(charset.get(), new Function<Character, String>() {
              @Override
              public String apply(Character character) {
                return partition.left() + character + partition.right().substring(1);
              }
            });
          }
        })
        .filter(new Predicate<String>() {
          @Override
          public boolean apply(String input) {
            return !input.equals(source);
          }
        });
  }

}
