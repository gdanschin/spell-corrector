package ru.hh.spellcorrector;

import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static ru.hh.spellcorrector.morpher.Morphers.delete;
import static ru.hh.spellcorrector.morpher.Morphers.insert;
import static ru.hh.spellcorrector.morpher.Morphers.replace;
import static ru.hh.spellcorrector.morpher.Morphers.sum;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class Runner {

  public static final String eng = "abcdefghijklmnopqrstuvwxyz";
  public static final String rus = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

  public static void main(String[] args) {
    try {
      Dictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    Morpher levenshtein = sum(delete(), replace(rus), insert(rus), transpose());
    Morpher morpher = Morphers.compose(levenshtein, levenshtein, sum(transpose(), delete()));


    Ordering<String> ordering = new Ordering<String>() {
      @Override
      public int compare(String left, String right) {
        Dictionary dict = Dictionary.getInstance();
        return Ints.compare(dict.dict.get(right), dict.dict.get(left));
      }
    };


    List<String> lookup = ordering.sortedCopy(filter(morpher.variants("ловииь"),
        new Predicate<String>() {
          @Override
          public boolean apply(String input) {
            return Dictionary.getInstance().dict.containsKey(input);
          }
        }
    ));

    System.out.println(lookup);
  }

}
