package ru.hh.spellcorrector.dto;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public abstract class Dto {

  public abstract void write(ChannelBuffer buffer);

  public String toString() {
    ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
    write(buffer);
    return buffer.toString();
  }

}
