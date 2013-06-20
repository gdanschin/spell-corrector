package ru.hh.spellcorrector.dict;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class StreamDictionary implements Dictionary {

  private static volatile StreamDictionary instance;

  public static Dictionary getInstance() {
    return instance;
  }

  public static Dictionary load(InputStream stream) throws IOException {
    if (instance == null) {
      synchronized (StreamDictionary.class) {
        if (instance == null) {
          instance = new StreamDictionary(stream);
        }
      }
    }
    return instance;
  }

  private final Map<String, Integer> dict;

  private StreamDictionary(InputStream stream) throws IOException  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));


    Map<String, Integer> mapBuilder = Maps.newHashMap();
    Splitter splitter = Splitter.on('|');

    String line;
    while ((line = reader.readLine()) != null) {
      List<String> split = ImmutableList.copyOf(splitter.split(line));
      mapBuilder.put(split.get(0), Integer.valueOf(split.get(1)));
    }
    dict = ImmutableMap.copyOf(mapBuilder);
  }

  @Override
  public int getFreq(String word) {
    return dict.containsKey(word) ? dict.get(word) + 1 : 1;
  }

  @Override
  public boolean isKnown(String word) {
    return dict.containsKey(word);
  }

}
