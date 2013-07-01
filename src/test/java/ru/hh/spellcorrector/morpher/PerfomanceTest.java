package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.SpellCorrector;
import ru.hh.spellcorrector.dict.StreamDictionary;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static ru.hh.spellcorrector.morpher.Morphers.cutDoubleSteps;
import static ru.hh.spellcorrector.morpher.Morphers.fullDoubleSteps;

public class PerfomanceTest {

  private static final Logger log = LoggerFactory.getLogger(PerfomanceTest.class);

  @Test(enabled = false)
  public void testPerfomance() throws IOException {
    StreamDictionary.load(PerfomanceTest.class.getResourceAsStream("/corrections"));

    Map<String, Morpher> morphers = ImmutableMap.<String, Morpher>builder()
        .put("Full", fullDoubleSteps())
        .put("Cut", cutDoubleSteps())
        .build();

    for (Map.Entry<String, Morpher> entry : morphers.entrySet()) {
      log.debug("Profiling morpher {}", entry.getKey());

      for (int time : runQuery(entry.getValue(), 20, 100, "ghjuhfvvbcn")) {
        log.debug("time: {}", time);
      }
    }
  }

  public Iterable<Integer> runQuery(Morpher morpher, final int times, final int cycles, final String query) {
    final SpellCorrector corrector = SpellCorrector.of(morpher, StreamDictionary.getInstance(), false);

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
              corrector.correctWord(query);
            }
            long end = System.currentTimeMillis();
            return (int) (end - start);
          }
        };
      }
    };
  }
}
