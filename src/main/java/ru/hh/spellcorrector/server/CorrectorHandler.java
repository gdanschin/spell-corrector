package ru.hh.spellcorrector.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.spellcorrector.SpellCorrector;
import ru.hh.spellcorrector.dto.Dto;

import java.nio.charset.Charset;
import java.util.List;

import static org.jboss.netty.channel.ChannelFutureListener.CLOSE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class CorrectorHandler extends SimpleChannelUpstreamHandler {

  private static final Logger logger = LoggerFactory.getLogger(CorrectorHandler.class);
  private final SpellCorrector corrector;

  public CorrectorHandler(SpellCorrector corrector) {
    this.corrector = corrector;
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

    List<String> qParams = new QueryStringDecoder(((HttpRequest) e.getMessage()).getUri()).getParameters().get("q");
    if (qParams == null || qParams.size() != 1) {
      sendAndClose(e.getChannel(), BAD_REQUEST, "Client must provide only one q argument\n");
    }
    logger.info("Start processing {}", qParams.get(0));

    Dto correction =  corrector.correct(qParams.get(0));
    logger.info("Corrected");
    HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
    ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
    correction.write(buf);
    response.setContent(buf);
    e.getChannel().write(response).addListener(CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    sendAndClose(e.getChannel(), INTERNAL_SERVER_ERROR, e.getCause().toString());
  }

  private void sendAndClose(Channel channel, HttpResponseStatus status, String message) {
    sendAndClose(channel, status, message, "text/plain; charset=utf-8");
  }

  private void sendAndClose(Channel channel, HttpResponseStatus status, String message, String contentType) {
    HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
    response.setHeader("Content-Type", contentType);
    ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
    buf.writeBytes(message.getBytes(Charset.forName("UTF-8")));

    response.setContent(buf);
    channel.write(response).addListener(CLOSE);
  }

}
