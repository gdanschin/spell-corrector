package ru.hh.spellcorrector.dict;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
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

  private final Map<String, Double>[] dict;
  private final double maxVal;

  private StreamDictionary(InputStream stream) throws IOException  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));


    Map<Integer, Map<String, Double>> builders = Maps.newHashMap();
    Splitter splitter = Splitter.on('|');

    double maxVal = 0;

    String line;
    while ((line = reader.readLine()) != null) {
      List<String> split = ImmutableList.copyOf(splitter.split(line));

      String key = split.get(0).toLowerCase();
      double val = Double.valueOf(split.get(1));
      int length = key.length();

      if (builders.containsKey(length)) {
        Map<String, Double> builder = builders.get(length);
        if (builder.containsKey(key)) {
          builder.put(key, max(val, builder.get(key)));
        } else {
          maxVal = max(val, maxVal);
          builder.put(key, val);
        }
      } else {
        Map<String, Double> builder = Maps.newHashMap();
        maxVal = max(val, maxVal);
        builder.put(key, val);
        builders.put(length, builder);
      }
    }

    int dictSize = Ordering.natural().max(builders.keySet());
    dict = new Map[dictSize];
    for (int i = 0; i < dictSize; i++) {
      int length = i + 1;
      Map<String, Double> builder = builders.get(length);
      if (builder == null) {
        dict[i] = Collections.emptyMap();
      } else {
        dict[i] = ImmutableMap.copyOf(builder);
      }
    }

    this.maxVal = maxVal;
  }

  @Override
  public double getFreq(String word) {
    int index = word.length() - 1;
    if (index >= dict.length) {
      return 1;
    } else {
      return dict[index].containsKey(word) ? dict[index].get(word) + 1 : 1;
    }
  }

  @Override
  public boolean isKnown(String word) {
    int index = word.length() - 1;
    if (index > dict.length) {
      return false;
    } else {
      return dict[index].containsKey(word);
    }
  }

  @Override
  public double maxFreq() {
    return maxVal;
  }
}
