package jforgame.server.game.core;

import jforgame.server.game.database.config.ConfigDataPool;
import jforgame.server.game.database.config.bean.ConfigNotice;
import jforgame.server.game.database.config.storage.ConfigNoticeStorage;
import jforgame.server.socket.SessionManager;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.Message;
import org.apache.mina.core.session.IoSession;

import java.util.Collection;

public class MessagePusher {

    public static void pushMessage(long playerId, Message message) {
        IdSession userSession = SessionManager.INSTANCE.getSessionBy(playerId);
        pushMessage(userSession, message);
    }

    public static void pushMessage(Collection<Long> playerIds, Message message) {
        for (long playerId:playerIds) {
            pushMessage(playerId, message);
        }
    }

    public static void pushMessage(IdSession session, Message message) {
        if (session == null || message == null) {
            return;
        }
        session.send(message);
    }

    public static void notify2Player(IoSession session, int i18nId) {
        ConfigNoticeStorage noticeStorage = ConfigDataPool.getInstance().getStorage(ConfigNoticeStorage.class);
        ConfigNotice idResource = noticeStorage.getNoticeBy(i18nId);
        if (idResource != null) {
//            MessagePusher.pushMessage(session, new RespMsg(idResource.getContent()));
        }
    }

    public static void notify2Player(IoSession session, int i18nId, Object... args) {
        ConfigNoticeStorage noticeStorage = ConfigDataPool.getInstance().getStorage(ConfigNoticeStorage.class);
        ConfigNotice idResource = noticeStorage.getNoticeBy(i18nId);
        if (idResource != null) {
            String content = String.format(idResource.getContent(), args);
        }
    }


}
