package ru.hh.spellcorrector.dto;

import org.jboss.netty.buffer.ChannelBuffer;

public class Word extends Dto {

  public final String text;
  public final String correction;
  public final String source;

  public static Word text(String text) {
    return new Word(text, null, null);
  }

  public static Word correction(String correction, String source) {
    return new Word(null, correction, source);
  }

  public static Word correction(String text, String correction, String source) {
    return new Word(text, correction, source);
  }

  private Word(String text, String correction, String source) {
    this.text = text;
    this.correction = correction;
    this.source = source;
  }

  @Override
  public void write(ChannelBuffer buffer) {
    buffer.writeBytes("<word>".getBytes());
    if (text != null && !text.equals("")) {
      buffer.writeBytes("<text>".getBytes());
      buffer.writeBytes(text.getBytes());
      buffer.writeBytes("</text>".getBytes());
    }
    if (correction != null && !correction.equals("")) {
      buffer.writeBytes("<correction><variant>".getBytes());
      buffer.writeBytes(correction.getBytes());
      buffer.writeBytes("</variant></correction>".getBytes());
    }
    if (source != null) {
      buffer.writeBytes("<source>".getBytes());
      buffer.writeBytes(source.getBytes());
      buffer.writeBytes("</source>".getBytes());
    }
    buffer.writeBytes("</word>".getBytes());
  }
}
