package ru.hh.spellcorrector.morpher;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.SpellCorrector;
import ru.hh.spellcorrector.dict.StreamDictionary;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.hh.spellcorrector.morpher.Morphers.compose;
import static ru.hh.spellcorrector.morpher.Morphers.delete;
import static ru.hh.spellcorrector.morpher.Morphers.insert;
import static ru.hh.spellcorrector.morpher.Morphers.keyboard;
import static ru.hh.spellcorrector.morpher.Morphers.replace;
import static ru.hh.spellcorrector.morpher.Morphers.split;
import static ru.hh.spellcorrector.morpher.Morphers.sum;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class CorrectorTest {

//  public static final String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz";
  public static final String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

  @Test
  public void testPerfomance() throws IOException {
    StreamDictionary.load(CorrectorTest.class.getResourceAsStream("/corrections"));

    Morpher step1 = sum(keyboard(), delete(), replace(), insert(), transpose(), split());
    Morpher step2 = sum(keyboard(), delete(), transpose(), split());
    Morpher morpher = compose(step1, step2);

    Map<String, Morpher> morphers = ImmutableMap.<String, Morpher>builder()
        .put("full", compose(step1, step2))
//        .put("step1", step1)
//        .put("compose", compose(step1))
//        .put("slowCompose", slowCompose(step1))
        .build();

    while(true) {
      for (Map.Entry<String, Morpher> entry : morphers.entrySet()) {

        for (int time : runQuery(entry.getValue(), 50, 100, "ghjuhfvvbcn")) {
        }
      }
      try {
        TimeUnit.MILLISECONDS.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
  }

  public List<Integer> runQuery(Morpher morpher, int times, int cycles, String query) {
    List<Integer> result = Lists.newArrayListWithCapacity(times);
    SpellCorrector corrector = SpellCorrector.of(morpher, StreamDictionary.getInstance(), true);
    for (int i = 0; i < times; i++) {
      long start = System.currentTimeMillis();
      for (int j = 0; j < cycles; j++) {
        corrector.correct(query);
      }
      long end = System.currentTimeMillis();
      result.add((int) (end - start));
    }
    return result;
  }
}

