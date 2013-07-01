package ru.hh.spellcorrector.dto;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.List;

public class Nigmer extends Dto {

  private static final String HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

  public final List<Word> words;

  public static Nigmer nigmer(List<Word> words) {
    return new Nigmer(words);
  }

  private Nigmer(List<Word> words) {
    this.words = words;
  }

  @Override
  public void write(ChannelBuffer buffer) {
    buffer.writeBytes(HEADER.getBytes());
    buffer.writeBytes("<nigmer>".getBytes());
    for (Word word : words) {
      word.write(buffer);
    }
    buffer.writeBytes("</nigmer>".getBytes());
  }
}
