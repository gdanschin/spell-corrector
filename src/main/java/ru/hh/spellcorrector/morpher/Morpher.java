package ru.hh.spellcorrector.morpher;

import com.google.common.base.Function;
import ru.hh.spellcorrector.Correction;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

public abstract class Morpher implements Function<Correction, Iterable<Correction>> {

  public abstract Iterable<Correction> corrections(Correction source);

  public Iterable<Correction> corrections(Iterable<Correction> sources) {
    return concat(transform(sources, this));
  }

  public final Iterable<Correction> corrections(String source) {
    return corrections(Correction.of(source));
  }

  @Override
  public final Iterable<Correction> apply(Correction input) {
    return corrections(input);
  }
}
