package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import ru.hh.spellcorrector.Partition;
import static ru.hh.spellcorrector.Utils.stringPartitions;

class Transpose extends StringTransform {

  private static final Morpher INSTANCE = new Transpose();

  private Transpose() { }

  static Morpher instance() {
    return INSTANCE;
  }

  @Override
  protected Iterable<String> variants(String source) {
    return stringPartitions(source)
        .filter(new Predicate<Partition>() {
          @Override
          public boolean apply(Partition input) {
            return input.right().length() > 1;
          }
        })
        .transform(new Function<Partition, String>() {
          @Override
          public String apply(Partition input) {
            return input.left() + input.right().charAt(1) + input.right().charAt(0) + input.right().substring(2);
          }
        });
  }
}
