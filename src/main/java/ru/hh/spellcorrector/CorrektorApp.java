package ru.hh.spellcorrector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.nab.NabModule;
import ru.hh.spellcorrector.dict.Dictionary;
import ru.hh.spellcorrector.dict.StreamDictionary;
import ru.hh.spellcorrector.morpher.Morpher;
import ru.hh.spellcorrector.morpher.Morphers;

import java.io.IOException;

public class CorrektorApp extends NabModule {

  private static final Logger logger = LoggerFactory.getLogger(CorrektorApp.class);

  @Override
  protected void configureApp() {
    try {
      StreamDictionary.load(CorrektorApp.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      logger.error("Can't load dictionary {}", e);
    }

    bind(Morpher.class).toInstance(Morphers.cutDoubleSteps());
    bind(Dictionary.class).toInstance(StreamDictionary.getInstance());
    bind(CorrektorService.class);

    bindJerseyResources(CorrektorResource.class);
  }

}

