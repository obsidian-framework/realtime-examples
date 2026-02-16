package fr.kainovaii.obsidian.app.websockets;

import fr.kainovaii.obsidian.routing.methods.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.eclipse.jetty.websocket.api.annotations.WebSocket
@WebSocket("/ws/chat")
public class SimpleChatHandler
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleChatHandler.class);
    private static final Map<Session, String> users = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        logger.info("New connection from: {}", session.getRemoteAddress());

        try {
            session.getRemote().sendString("Welcome to the chat! Send your username to join.");
            logger.info("Welcome message sent to {}", session.getRemoteAddress());
        } catch (Exception e) {
            logger.error("Error sending welcome message", e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        logger.info("Message from {}: {}", session.getRemoteAddress(), message);

        try {
            if (!users.containsKey(session)) {
                String username = message.trim();
                users.put(session, username);
                logger.info("User '{}' registered ({} total users)", username, users.size());

                String joinMsg = username + " joined the chat! (" + users.size() + " users online)";
                broadcast(joinMsg);
            } else {
                String username = users.get(session);
                String chatMsg = username + ": " + message;
                logger.info("Broadcasting message from '{}'", username);
                broadcast(chatMsg);
            }

        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String reason)
    {
        String username = users.remove(session);
        logger.info("Connection closed: {} (code: {}, reason: {})", username, code, reason);

        if (username != null) {
            try {
                String leaveMsg = username + " left the chat. (" + users.size() + " users online)";
                broadcast(leaveMsg);
            } catch (Exception e) {
                logger.error("Error broadcasting leave message", e);
            }
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error)
    {
        logger.error("WebSocket error for {}: {}", session.getRemoteAddress(), error.getMessage(), error);
    }

    private void broadcast(String message) throws IOException
    {
        logger.debug("Broadcasting to {} sessions: {}", users.size(), message);

        int sent = 0;
        for (Session session : users.keySet()) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
                sent++;
            }
        }

        logger.debug("Message broadcast to {} sessions", sent);
    }
}