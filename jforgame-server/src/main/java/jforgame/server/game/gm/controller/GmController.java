package jforgame.server.game.gm.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.gm.message.ReqGmExec;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestMapping;

@MessageRoute
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExec msg) {
        GameContext.gmManager.receiveCommand(playerId, msg.command);
	}
	
}