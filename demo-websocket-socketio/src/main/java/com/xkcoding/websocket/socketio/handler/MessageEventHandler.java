package com.xkcoding.websocket.socketio.handler;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.xkcoding.websocket.socketio.config.DbTemplate;
import com.xkcoding.websocket.socketio.config.Event;
import com.xkcoding.websocket.socketio.payload.BroadcastMessageRequest;
import com.xkcoding.websocket.socketio.payload.GroupMessageRequest;
import com.xkcoding.websocket.socketio.payload.JoinRequest;
import com.xkcoding.websocket.socketio.payload.SingleMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 * Message event handling
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 18:57
 */
@Component
@Slf4j
public class MessageEventHandler {
    @Autowired
    private SocketIOServer server;

    @Autowired
    private DbTemplate dbTemplate;

    /**
     * Add the connect event, which is called when the client initiates a connection
     *
     * @param client object
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            The impersonated user id and token are consistent
            String userId = client.getHandshakeData().getSingleUrlParam("token");
            UUID sessionId = client.getSessionId();

            dbTemplate.save(userId, sessionId);
            log.info("连接成功,【token】= {},【sessionId】= {}", token, sessionId);
        } else {
            log.error("客户端为空");
        }
    }

    /**
     * Add disconnect event, called when the client disconnects, refresh client information
     *
     * @param client object
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (client != null) {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            The impersonated user id and token are consistent
            String userId = client.getHandshakeData().getSingleUrlParam("token");
            UUID sessionId = client.getSessionId();

            dbTemplate.deleteByUserId(userId);
            log.info("客户端断开连接,【token】= {},【sessionId】= {}", token, sessionId);
            client.disconnect();
        } else {
            log.error("客户端为空");
        }
    }

    /**
     * Join a group chat
     *
     * @param client client
     * @param request request
     * @param data group chat
     */
    @OnEvent(value = Event.JOIN)
    public void onJoinEvent(SocketIOClient client, AckRequest request, JoinRequest data) {
        log.info("用户：{} 已加入群聊：{}", data.getUserId(), data.getGroupId());
        client.joinRoom(data.getGroupId());

        server.getRoomOperations(data.getGroupId()).sendEvent(Event.JOIN, data);
    }


    @OnEvent(value = Event.CHAT)
    public void onChatEvent(SocketIOClient client, AckRequest request, SingleMessageRequest data) {
        Optional<UUID> toUser = dbTemplate.findByUserId(data.getToUid());
        if (toUser.isPresent()) {
            log.info("用户 {} 刚刚私信了用户 {}：{}", data.getFromUid(), data.getToUid(), data.getMessage());
            sendToSingle(toUser.get(), data);
            request.sendAckData(Dict.create().set("flag", true).set("message", "发送成功"));
        } else {
            request.sendAckData(Dict.create().set("flag", false).set("message", "发送失败，对方不想理你(" + data.getToUid() + "不在线)"));
        }
    }

    @OnEvent(value = Event.GROUP)
    public void onGroupEvent(SocketIOClient client, AckRequest request, GroupMessageRequest data) {
        Collection<SocketIOClient> clients = server.getRoomOperations(data.getGroupId()).getClients();

        boolean inGroup = false;
        for (SocketIOClient socketIOClient : clients) {
            if (ObjectUtil.equal(socketIOClient.getSessionId(), client.getSessionId())) {
                inGroup = true;
                break;
            }
        }
        if (inGroup) {
            log.info("群号 {} 收到来自 {} 的群聊消息：{}", data.getGroupId(), data.getFromUid(), data.getMessage());
            sendToGroup(data);
        } else {
            request.sendAckData("请先加群！");
        }
    }

    /**
     * Single chat
     */
    public void sendToSingle(UUID sessionId, SingleMessageRequest message) {
        server.getClient(sessionId).sendEvent(Event.CHAT, message);
    }

    /**
     * Broadcast
     */
    public void sendToBroadcast(BroadcastMessageRequest message) {
        log.info("系统紧急广播一条通知：{}", message.getMessage());
        for (UUID clientId : dbTemplate.findAll()) {
            if (server.getClient(clientId) == null) {
                continue;
            }
            server.getClient(clientId).sendEvent(Event.BROADCAST, message);
        }
    }

    /**
     * Group chat
     */
    public void sendToGroup(GroupMessageRequest message) {
        server.getRoomOperations(message.getGroupId()).sendEvent(Event.GROUP, message);
    }
}
