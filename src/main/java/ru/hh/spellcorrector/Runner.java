package ru.hh.spellcorrector;

import ru.hh.spellcorrector.dict.StreamDictionary;
import java.io.IOException;
import static ru.hh.spellcorrector.morpher.Morphers.levenshteinSquared;
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


    System.out.println(SpellCorrector.of(levenshteinSquared(), StreamDictionary.getInstance(), false).correct("вокансея"));
  }

}
