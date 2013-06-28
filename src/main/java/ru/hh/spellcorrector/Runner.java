package ru.hh.spellcorrector;

import ru.hh.spellcorrector.dict.StreamDictionary;
import java.io.IOException;

public class Runner {

  public static void main(String[] args) {
    try {
      StreamDictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Initialized dictionary");
  }

}
