package ru.hh.spellcorrector;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.spellcorrector.dict.StreamDictionary;
import ru.hh.spellcorrector.morpher.Morphers;
import ru.hh.spellcorrector.server.CorrectorHandler;
import ru.hh.spellcorrector.server.HTTPServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class Runner {

  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) {
    try {
      StreamDictionary.load(Runner.class.getResourceAsStream("/corrections"));
    } catch (IOException e) {
      log.error("Can't load dictionary {}", e);
    }

    Map<String, Object> tcpOpts = ImmutableMap.<String, Object>of(
        "child.tcpNoDelay", true,
        "child.keepAlive", true,
        "localAddress", new InetSocketAddress(8080)
    );

    final SpellCorrector corrector = SpellCorrector.of(Morphers.cutDoubleSteps(), StreamDictionary.getInstance(), true);

    HTTPServer server = new HTTPServer(tcpOpts, 4, new CorrectorHandler(corrector));

    server.start();
  }

}
