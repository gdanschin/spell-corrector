package ru.hh.spellcorrector;

import com.google.common.base.Function;
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
import static ru.hh.spellcorrector.morpher.Morphers.sum;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class Runner {

  public static final String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz";

  private static double score(double distance, int freq) {
    return (double) Math.pow(freq, 0.5) / Math.pow(10, distance);
  }

  public static void main(String[] args) {
    try {
      StreamDictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    Morpher levenshtein1 = sum(delete(), replace(alphabet), insert(alphabet), transpose(), keyboard());
    Morpher levenshtein2 = sum(delete(), replace(alphabet), insert(alphabet), transpose());
    Morpher morpher = Morphers.compose(levenshtein1, levenshtein2);


    Ordering<Correction> weight = new Ordering<Correction>() {
      @Override
      public int compare(Correction left, Correction right) {
        return Doubles.compare(right.getWeight(), left.getWeight());
      }
    };

    Function<Correction, Correction> SCORE = new Function<Correction, Correction>() {
      @Override
      public Correction apply(Correction input) {
        return Correction.of(input.getText(), score(input.getWeight(), StreamDictionary.getInstance().getFreq(input.getText())));
      }
    };

    Morpher language = Morphers.language(StreamDictionary.getInstance());

    List<Correction> lookup = weight.sortedCopy(ImmutableSet.copyOf(language.corrections
        (morpher.corrections(Correction.of("дуы", 1.0)))));

    System.out.println(lookup);
  }

}
