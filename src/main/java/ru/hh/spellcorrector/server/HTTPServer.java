package ru.hh.spellcorrector.server;

import com.google.common.util.concurrent.AbstractService;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.Executors;

public class HTTPServer extends AbstractService {

  private static final Logger logger = LoggerFactory.getLogger(HTTPServer.class);

  private final ServerBootstrap bootstrap;
  private volatile Channel serverChannel;

  public HTTPServer(Map<String, Object> options, int ioThreads, final ChannelHandler handler) {
    ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool(), ioThreads);
    bootstrap = new ServerBootstrap(factory);
    bootstrap.setOptions(options);
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
            new HttpServerCodec(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), handler
        );
      }
    });
  }

  protected void doStart() {
    logger.debug("starting");
    try {
      serverChannel = bootstrap.bind();
      logger.info("started at {}", serverChannel.getLocalAddress());
    } catch (RuntimeException e) {
      logger.error("can't start", e);
      notifyFailed(e);
      throw e;
    }
  }

  protected void doStop() {
    logger.debug("stopping");
    try {
      serverChannel.close().awaitUninterruptibly();
      bootstrap.releaseExternalResources();
      logger.debug("stopped");
      notifyStopped();
    } catch (RuntimeException e) {
      logger.error("can't stop", e);
      notifyFailed(e);
      throw e;
    }
  }

  @ChannelHandler.Sharable
  private static class ChannelTracker extends SimpleChannelUpstreamHandler {
    private final ChannelGroup group = new DefaultChannelGroup();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
      logger.error("Unexpected exeption ", e.getCause());
      if (!(ClosedChannelException.class.isInstance(e.getCause()))) {

      }
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
      group.add(e.getChannel());
      ctx.sendUpstream(e);
    }

    public void waitForChildren() {
      for (Channel channel : group) {
        channel.getCloseFuture().awaitUninterruptibly();
      }
    }
  }
}
