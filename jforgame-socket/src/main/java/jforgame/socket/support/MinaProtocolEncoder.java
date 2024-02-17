package jforgame.socket.support;

import jforgame.socket.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author kinson
 */
public class MinaProtocolEncoder implements ProtocolEncoder {

	private MessageFactory messageFactory;

	private MessageCodec messageCodec;

	private int WRITE_BUFF_SIZE = 1024;

	/**
	 * 消息元信息常量，为int类型的长度，表示消息的id
	 */
	private static final int MESSAGE_META_SIZE = 4;

	public MinaProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
		this.messageFactory = messageFactory;
		this.messageCodec = messageCodec;
	}

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = writeMessage(message);
		out.write(buffer);
	}

	private IoBuffer writeMessage(Object message) {
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]

		IoBuffer buffer = IoBuffer.allocate(WRITE_BUFF_SIZE);
		buffer.setAutoExpand(true);

		byte[] body = messageCodec.encode(message);
		final int metaSize = MESSAGE_META_SIZE;
		// the length of message body
		buffer.putInt(body.length + metaSize);
		int cmd = messageFactory.getMessageId(message.getClass());
		// 写入cmd类型
		buffer.putInt(cmd);
		// 写入消息体
		buffer.put(body);
		// 回到buff字节数组头部
		buffer.flip();

		return buffer;
	}

}