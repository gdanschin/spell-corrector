package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.SpellCorrector;
import ru.hh.spellcorrector.dict.StreamDictionary;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static ru.hh.spellcorrector.morpher.Morphers.charset;
import static ru.hh.spellcorrector.morpher.Morphers.delete;
import static ru.hh.spellcorrector.morpher.Morphers.insert;
import static ru.hh.spellcorrector.morpher.Morphers.pipe;
import static ru.hh.spellcorrector.morpher.Morphers.replace;
import static ru.hh.spellcorrector.morpher.Morphers.split;
import static ru.hh.spellcorrector.morpher.Morphers.sum;
import static ru.hh.spellcorrector.morpher.Morphers.transpose;

public class CorrectorTest {

  @Test
  public void testPerfomance() throws IOException {
    StreamDictionary.load(CorrectorTest.class.getResourceAsStream("/corrections"));

    Map<String, Morpher> morphers = ImmutableMap.<String, Morpher>builder()
        .put("Cut", pipe(
            sum(delete(), transpose(), replace(), insert(), charset(), split()),
            sum(delete(), transpose(), charset(), split())
        ))
        .put("CutOpt", sum(
            pipe(delete(), sum(delete(), transpose(), replace(), insert(), split())),
            pipe(transpose(), sum(transpose(), replace(), insert(), split())),
            pipe(replace(), split()),
            pipe(insert(), split()),
            pipe(charset(), sum(delete(), transpose(), replace(), insert())),
            pipe(split(), sum(charset(), split()))
        ))
        .build();

    for (Map.Entry<String, Morpher> entry : morphers.entrySet()) {
      System.out.println(entry.getKey());
      for (int time : runQuery(entry.getValue(), 20, 1000, "программияя")) {
        System.out.println(time);
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

