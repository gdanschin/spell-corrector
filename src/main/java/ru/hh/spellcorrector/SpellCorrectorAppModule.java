package ru.hh.spellcorrector;

import com.google.inject.Scopes;
import com.google.inject.util.Providers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.nab.NabModule;
import ru.hh.spellcorrector.dict.StreamDictionary;
import ru.hh.spellcorrector.morpher.Morphers;

import java.io.IOException;

public class SpellCorrectorAppModule extends NabModule {

  private static final Logger logger = LoggerFactory.getLogger(SpellCorrectorAppModule.class);

  @Override
  protected void configureApp() {
    try {
      StreamDictionary.load(SpellCorrectorAppModule.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      logger.error("Can't load dictionary {}", e);
    }
    bind(SpellCorrector.class).toProvider(Providers.of(SpellCorrector.of(Morphers.cutDoubleSteps(), StreamDictionary.getInstance(), true))).in(Scopes.SINGLETON);
    bindJerseyResources(SpellCorrectorResource.class);
  }

}

