package ru.hh.spellcorrector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import ru.hh.spellcorrector.dict.Dictionary;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;

public class SpellCorrector {

  private static final Splitter SPLITTER = Splitter.onPattern("\\s+").trimResults().omitEmptyStrings();
  private static final Joiner JOINER = Joiner.on(" ");
  private static final Ordering<Correction> WEIGHT = new Ordering<Correction>() {
    @Override
    public int compare(Correction left, Correction right) {
      return Doubles.compare(left.getWeight(), right.getWeight());
    }
  };

  private final Morpher morpher;
  private final Morpher lang;

  public static SpellCorrector of(Morpher morpher, Dictionary lang, boolean shortCircuit) {
    return new SpellCorrector(morpher, lang, shortCircuit);
  }

  private SpellCorrector(Morpher morpher, Dictionary lang, boolean shortCircuit) {
    this.morpher = morpher;
    this.lang = Morphers.language(lang, shortCircuit);
  }

  public String correct(String input) {
    return JOINER.join(Iterables.transform(SPLITTER.split(input),
        new Function<String, String>() {
          @Override
          public String apply(String word) {
            if (word.length() <= 3) {
              return word;
            }

            Correction source = Correction.of(Phrase.of(word), 1.0);
            final Iterable<Correction> corrections = concat(asList(Correction.of(Phrase.of(word), 0.0)), lang.corrections(morpher.corrections(source)));
            Correction correction = WEIGHT.max(corrections);
            return JOINER.join(correction.getPhrase().getWords());
          }
        }));
  }
}
