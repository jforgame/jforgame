package jforgame.socket.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.IdSession;
import jforgame.socket.share.message.IMessageDispatcher;
import jforgame.socket.netty.ChannelUtils;
import jforgame.socket.netty.NSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MsgIoHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MsgIoHandler.class);

    private IMessageDispatcher messageDispatcher;


    public MsgIoHandler(IMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!ChannelUtils.addChannelSession(ctx.channel(),
                new NSession(ctx.channel()))) {
            ctx.channel().close();
            logger.error("Duplicate session,IP=[{}]", ChannelUtils.getIp(ctx.channel()));
            return;
        }

        IdSession session = ChannelUtils.getSessionBy(ctx.channel());
        messageDispatcher.onSessionCreated(session);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        logger.debug("receive pact, content is {}", packet.getClass().getSimpleName());

        final Channel channel = context.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        if (packet instanceof Traceable) {
            Traceable traceable = (Traceable) packet;
            RpcResponseData callback = new RpcResponseData();
            callback.setIndex(traceable.getIndex());
            callback.setResponse(packet);
            CallBackService.getInstance().fillCallBack(traceable.getIndex(), callback);
            return;
        }
        messageDispatcher.dispatch(session, packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel[{}] inactive", ctx.channel());
        IdSession session = ChannelUtils.getSessionBy(ctx.channel());
        messageDispatcher.onSessionClosed(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (channel.isActive() || channel.isOpen()) {
            ctx.close();
        }
        if (!(cause instanceof IOException)) {
            logger.error("remote:" + channel.remoteAddress(), cause);
        }
    }
}