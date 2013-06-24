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

import static java.lang.Math.max;

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

  private final Map<String, Double> dict;
  private final double maxVal;

  private StreamDictionary(InputStream stream) throws IOException  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));


    Map<String, Double> mapBuilder = Maps.newHashMap();
    Splitter splitter = Splitter.on('|');

    double maxVal = 0;

    String line;
    while ((line = reader.readLine()) != null) {
      List<String> split = ImmutableList.copyOf(splitter.split(line));

      String key = split.get(0).toLowerCase();
      double val = Double.valueOf(split.get(1));

      if (mapBuilder.containsKey(key)) {
        mapBuilder.put(key, max(val, mapBuilder.get(key)));
      } else {
        maxVal = max(val, maxVal);
        mapBuilder.put(key, val);
      }
    }

    dict = ImmutableMap.copyOf(mapBuilder);
    this.maxVal = maxVal;
  }

  @Override
  public double getFreq(String word) {
    return dict.containsKey(word) ? dict.get(word) + 1 : 1;
  }

  @Override
  public boolean isKnown(String word) {
    return dict.containsKey(word);
  }

  @Override
  public double maxFreq() {
    return maxVal;
  }
}
