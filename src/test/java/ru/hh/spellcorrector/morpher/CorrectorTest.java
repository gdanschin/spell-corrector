package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.SpellCorrector;
import ru.hh.spellcorrector.dict.StreamDictionary;

import java.io.IOException;
import java.util.Iterator;
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

    for (Map.Entry<String, Morpher> entry : morphers.entrySet()) {
      System.out.println(entry.getKey());
      for (int time : runQuery(entry.getValue(), 20, 100, "ghjuhfvvbcn")) {
        System.out.println(time);
      }
    }
  }

  public Iterable<Integer> runQuery(Morpher morpher, final int times, final int cycles, final String query) {
    final SpellCorrector corrector = SpellCorrector.of(morpher, StreamDictionary.getInstance(), true);

    return new Iterable<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new AbstractIterator<Integer>() {
          int i = 0;

          @Override
          protected Integer computeNext() {
            if (i++ >= times) {
              return endOfData();
            }

            long start = System.currentTimeMillis();
            for (int j = 0; j < cycles; j++) {
              corrector.correct(query);
            }
            long end = System.currentTimeMillis();
            return (int) (end - start);
          }
        };
      }
    };
  }
}

