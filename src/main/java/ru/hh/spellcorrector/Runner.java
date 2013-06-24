package ru.hh.spellcorrector;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import ru.hh.spellcorrector.dict.StreamDictionary;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ru.hh.spellcorrector.morpher.Morphers.delete;
import static ru.hh.spellcorrector.morpher.Morphers.insert;
import static ru.hh.spellcorrector.morpher.Morphers.keyboard;
import static ru.hh.spellcorrector.morpher.Morphers.replace;
import static ru.hh.spellcorrector.morpher.Morphers.split;
import static ru.hh.spellcorrector.morpher.Morphers.sum;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class Runner {

  public static final String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz";

  static Predicate<Correction> GOOD = new Predicate<Correction>() {
    @Override
    public boolean apply(Correction input) {
      return Iterables.all(input.getWords(), new Predicate<String>() {
        @Override
        public boolean apply(String input) {
          return StreamDictionary.getInstance().isKnown(input);
        }
      });
    }
  };

  public static void main(String[] args) {
    try {
      StreamDictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Initialized dictionary");

    Morpher levenshtein1 = sum(keyboard(), delete(), replace(alphabet), insert(alphabet), transpose(), split());
    Morpher morpher = Morphers.compose(levenshtein1, levenshtein1);


    Ordering<Correction> weight = new Ordering<Correction>() {
      @Override
      public int compare(Correction left, Correction right) {
        return Doubles.compare(right.getWeight(), left.getWeight());
      }
    };

    Morpher language = Morphers.language(StreamDictionary.getInstance(), true);

    final Correction correction = Correction.of(Arrays.asList("dtcnf"), 1.0);
    List<Correction> lookup = weight.sortedCopy(ImmutableSet.copyOf(language.corrections(morpher.corrections(correction))));

    System.out.println(lookup);
  }

}
