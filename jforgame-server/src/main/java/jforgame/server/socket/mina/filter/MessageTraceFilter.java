package jforgame.server.socket.mina.filter;

import com.google.gson.Gson;
import jforgame.server.logs.LoggerUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MessageTraceFilter extends IoFilterAdapter {

	private Logger logger = LoggerFactory.getLogger(MessageTraceFilter.class);

	private boolean debug = true;

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		if (debug && traceRequest(message)) {
			logger.error("<<<<<<<<<<[{}]{}={}",
					session,
					message.getClass().getSimpleName(), new Gson().toJson(message));
		}
		nextFilter.messageReceived(session, message);
	}

	private boolean traceRequest(Object message) {
		Set<Class<?>> ignores = new HashSet<>();

		return ! ignores.contains(message.getClass());
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		Object message = writeRequest.getMessage();
		if (debug && traceResponse(message)) {
			LoggerUtils.error(">>>>>>>>>>[{}]{}={}",
					session,
					message.getClass().getSimpleName(),
					new Gson().toJson(message));
		}
		nextFilter.messageSent(session, writeRequest);
	}

	private boolean traceResponse(Object message) {
		Set<Class<?>> ignores = new HashSet<>();

		return ! ignores.contains(message.getClass());
	}

}