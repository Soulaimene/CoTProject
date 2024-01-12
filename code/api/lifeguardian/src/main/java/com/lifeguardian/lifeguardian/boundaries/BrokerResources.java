package com.lifeguardian.lifeguardian.boundaries;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.lifeguardian.lifeguardian.utils.MqttConnection;

import javax.net.ssl.SSLSocketFactory;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Hashtable;
import jakarta.ejb.Singleton;

@ApplicationScoped
@ServerEndpoint("/mqtt")
public class BrokerResources {
    private static Hashtable<String, Session> sessions = new Hashtable<>();

    @Inject
    private MqttConnection mqttConnection;

    @OnOpen
    public void onOpen(Session session){
        System.out.println("WebSocket opened: " + session.getId());
        mqttConnection.start();
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        System.out.println("WebSocket closed: " + session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("WebSocket error for session " + session.getId() + ": " + error.getMessage());
        sessions.remove(session.getId());
    }

    public static void broadcast(String message) {
        System.out.println("Broadcasting message to " + sessions.size() + " WebSocket sessions.");
        for (Session session : sessions.values()) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
                System.out.println("Message sent to session: " + session.getId());
            } else {
                System.out.println("Found closed session: " + session.getId());
            }
        }
    }
}
