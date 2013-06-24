package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import ru.hh.spellcorrector.Partition;

import static ru.hh.spellcorrector.Utils.stringPartitions;

class Delete extends StringTransform {

  private static final Morpher INSTANCE = new Delete();

  private Delete() {
  }

  static Morpher instance() {
    return INSTANCE;
  }

  @Override
  protected Iterable<String> variants(String source) {
    return stringPartitions(source)
        .filter(new Predicate<Partition>() {
          @Override
          public boolean apply(Partition input) {
            return input.right().length() > 0;
          }
        })
        .transform(new Function<Partition, String>() {
          @Override
          public String apply(Partition input) {
            return input.left() + input.right().substring(1);
          }
        })
        .filter(new Predicate<String>() {
          @Override
          public boolean apply(String input) {
            return input.length() > 0;
          }
        });
  }
}
