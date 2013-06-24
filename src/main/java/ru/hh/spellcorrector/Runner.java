package ru.hh.spellcorrector;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import ru.hh.spellcorrector.dict.StreamDictionary;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;
import java.io.IOException;
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

  public static void main(String[] args) {
    try {
      StreamDictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Initialized dictionary");

    Morpher levenshtein = sum(keyboard(), delete(), replace(alphabet), insert(alphabet), transpose(), split());
    Morpher morpher = Morphers.compose(levenshtein, levenshtein);


    Ordering<Correction> weight = new Ordering<Correction>() {
      @Override
      public int compare(Correction left, Correction right) {
        return Doubles.compare(right.getWeight(), left.getWeight());
      }
    };

    Morpher language = Morphers.language(StreamDictionary.getInstance(), true);

    final Correction correction = Correction.of(Phrase.of("dtcnf"), 1.0);
    List<Correction> lookup = weight.sortedCopy(ImmutableSet.copyOf(language.corrections(morpher.corrections(correction))));

    System.out.println(lookup);
  }

}
