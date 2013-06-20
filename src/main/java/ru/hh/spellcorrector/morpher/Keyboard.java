package ru.hh.spellcorrector.morpher;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.charactersOf;

class Keyboard extends FixedMultiplierMorpher {

  public Keyboard(double multiplier) {
    super(multiplier);
  }

  private static final ImmutableMap<Character, Character> TO_RUS;
  private static final ImmutableMap<Character, Character> TO_ENG;

  static {
    String rus = "йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ.";
    String eng = "qwertyuiop[]asdfghjkl;'zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?";
    Map<Character, Character> toEng = Maps.newHashMap();
    Map<Character, Character> toRus = Maps.newHashMap();
    for (int i = 0; i < rus.length(); i++) {
      toEng.put(rus.charAt(i), eng.charAt(i));
      toRus.put(eng.charAt(i), rus.charAt(i));
    }

    TO_RUS = ImmutableMap.copyOf(toRus);
    TO_ENG = ImmutableMap.copyOf(toEng);
  }

  private static String fromList(List<Character> chars) {
    char[] res = new char[chars.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = chars.get(i);
    }
    return new String(res);
  }

  boolean check(String source, Map<Character, Character> map) {
    for (Character ch : Lists.charactersOf(source)) {
      if (map.containsKey(ch)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Iterable<String> variants(final String source) {
    List<String> result = Lists.newLinkedList();
    if (check(source, TO_RUS)) {
      result.add(fromList(copyOf(transform(charactersOf(source), Functions.forMap(TO_RUS)))));
    }
    if (check(source, TO_ENG)) {
      result.add(fromList(copyOf(transform(charactersOf(source), Functions.forMap(TO_ENG)))));
    }
    return result;
  }
}
