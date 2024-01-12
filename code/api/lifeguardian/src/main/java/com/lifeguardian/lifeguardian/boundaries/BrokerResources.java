//package com.lifeguardian.lifeguardian.boundaries;
//
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import com.lifeguardian.lifeguardian.utils.MqttConnection;
//
//import javax.net.ssl.SSLSocketFactory;
//import jakarta.websocket.*;
//import jakarta.websocket.server.ServerEndpoint;
//import java.util.Hashtable;
//import jakarta.ejb.Singleton;
//
//@ApplicationScoped
//
//@ServerEndpoint("/mqtt")
//public class BrokerResources {
//    private static Hashtable<String, Session> sessions = new Hashtable<>();
//    @Inject
//    private MqttConnection mqttConnection;
//    @OnOpen
//    public void onOpen(Session session){
//        mqttConnection.start();
//        sessions.put(session.getId(), session); //add the new session
//
//    }
//    @OnClose
//    public void onClose(Session session, CloseReason reason){
//        sessions.remove(session); // delete sessions when client leave
//    }
//    @OnError
//    public void onError(Session session, Throwable error){
//        System.out.println("Push WebSocket error for ID " + session.getId() + ": " + error.getMessage());
//    }
//
//
//}