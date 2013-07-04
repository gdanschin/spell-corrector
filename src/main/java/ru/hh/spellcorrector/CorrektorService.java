package ru.hh.spellcorrector;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.nab.Settings;
import ru.hh.spellcorrector.dict.Dictionary;
import ru.hh.spellcorrector.dto.NigmerDto;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;
import static ru.hh.spellcorrector.dto.WordDto.correction;
import static ru.hh.spellcorrector.dto.WordDto.text;

public class CorrektorService {

  private static final Logger logger = LoggerFactory.getLogger(CorrektorService.class);

  private static final Joiner JOINER = Joiner.on(" ");
  private static final Pattern PATTERN = Pattern.compile("[\\w|\\p{InCyrillic}|']+");

  private int lowBound = 0;
  private int highBound = 0;

  private static final Ordering<Correction> WEIGHT = new Ordering<Correction>() {
    @Override
    public int compare(Correction left, Correction right) {
      return Doubles.compare(left.getWeight(), right.getWeight());
    }
  };

  private final Morpher morpher;
  private final Morpher lang;

  @Inject
  public CorrektorService(Morpher morpher, Dictionary lang, Settings settings) {
    this(morpher, lang,
        Integer.valueOf(settings.subTree("correktor").getProperty("lowLengthBound")),
        Integer.valueOf(settings.subTree("correktor").getProperty("highLengthBound")));
  }

  public CorrektorService(Morpher morpher, Dictionary lang) {
    this(morpher, lang, 0, 0);
  }

  public CorrektorService(Morpher morpher, Dictionary lang, int lowBound, int highBound) {
    this.morpher = morpher;
    this.lang = Morphers.language(lang, true);
    this.lowBound = lowBound;
    this.highBound = highBound;
  }

  public NigmerDto correct(String input) {
    logger.info("Start correcting \"{}\"", input);
    long startTime = System.currentTimeMillis();

    Matcher matcher = PATTERN.matcher(input);

    NigmerDto result = new NigmerDto();

    int lastMatch = 0;
    while (matcher.find()) {
      String text = input.substring(lastMatch, matcher.start());
      String original = matcher.group();

      CaseState state = CaseState.getState(original);

      String normalized = original.toLowerCase();
      String correction = correctWord(normalized);

      lastMatch =  matcher.end();
      result.word.add(normalized.equals(correction) ? text(text + original) : correction(text, state.apply(correction), normalized));
    }

    long time = System.currentTimeMillis() - startTime;
    logger.info("End correcting \"{}\", {}ms", input, time);

    return result;
  }

  public String correctWord(String word) {
    if ((lowBound > 0 && word.length() < lowBound) || (highBound > 0 && word.length() > highBound)) {
      return word;
    }

    Correction source = Correction.of(Phrase.of(word), 1.0);
    final Iterable<Correction> corrections = concat(asList(Correction.of(Phrase.of(word), 0.0)), lang.corrections(morpher.corrections(source)));
    Correction correction = WEIGHT.max(corrections);
    return JOINER.join(correction.getPhrase().getWords());
  }

}
