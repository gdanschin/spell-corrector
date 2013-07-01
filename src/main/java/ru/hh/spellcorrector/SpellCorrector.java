package ru.hh.spellcorrector;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import ru.hh.spellcorrector.dict.Dictionary;
import ru.hh.spellcorrector.dto.Dto;
import ru.hh.spellcorrector.dto.Nigmer;
import ru.hh.spellcorrector.dto.Word;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Iterables.concat;
import static java.util.Arrays.asList;

public class SpellCorrector {

  private static final Joiner JOINER = Joiner.on(" ");
  private static final Pattern PATTERN = Pattern.compile("[\\w|\\p{InCyrillic}]+");

  private static final Ordering<Correction> WEIGHT = new Ordering<Correction>() {
    @Override
    public int compare(Correction left, Correction right) {
      return Doubles.compare(left.getWeight(), right.getWeight());
    }
  };

  private final Morpher morpher;
  private final Morpher lang;

  public static SpellCorrector of(Morpher morpher, Dictionary lang, boolean shortCircuit) {
    return new SpellCorrector(morpher, lang, shortCircuit);
  }

  private SpellCorrector(Morpher morpher, Dictionary lang, boolean shortCircuit) {
    this.morpher = morpher;
    this.lang = Morphers.language(lang, shortCircuit);
  }

  public Dto correct(String input) {
    Matcher matcher = PATTERN.matcher(input);

    List<Word> words = Lists.newArrayList();

    int lastMatch = 0;
    while (matcher.find()) {
      String text = input.substring(lastMatch, matcher.start());
      String original = matcher.group();
      String correction = correctWord(original);
      words.add(original.equals(correction) ? Word.text(text + original) : Word.correction(text, correction, original));
      lastMatch =  matcher.end();
    }

    return Nigmer.nigmer(words);
  }

  public String correctWord(String word) {
    if (word.length() <= 3) {
      return word;
    }

    Correction source = Correction.of(Phrase.of(word), 1.0);
    final Iterable<Correction> corrections = concat(asList(Correction.of(Phrase.of(word), 0.0)), lang.corrections(morpher.corrections(source)));
    Correction correction = WEIGHT.max(corrections);
    return JOINER.join(correction.getPhrase().getWords());
  }



}
