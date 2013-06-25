package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

class Charsets {
  public static final String RUS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
  public static final String ENG = "abcdefghijklmnopqrstuvwxyz";

  List<Set<Character>> charsets;

  static Charsets of(Iterable<String> alphabets) {
    return new Charsets(alphabets);
  }

  Charsets(Iterable<String> alphabets) {
    this.charsets = ImmutableList.copyOf(Iterables.transform(alphabets, new Function<String, Set<Character>>() {
      @Override
      public Set<Character> apply(String input) {
        return ImmutableSet.copyOf(Lists.charactersOf(input));
      }
    }));
  }

  public Optional<Set<Character>> guess(String word) {
    List<Character> characters = Lists.charactersOf(word);
    Character first = characters.get(0);

    for (Set<Character> alphabet: charsets) {
      if (alphabet.contains(first) && Iterables.all(characters, Predicates.in(alphabet))) {
        return Optional.of(alphabet);
      }
    }

    return Optional.absent();
  }

}
