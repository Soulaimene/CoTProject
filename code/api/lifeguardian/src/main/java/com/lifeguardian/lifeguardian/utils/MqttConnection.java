package com.lifeguardian.lifeguardian.utils;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import com.lifeguardian.lifeguardian.models.SensorsData;

@ApplicationScoped
@Startup
public class HealthMqttConnection {


    @PostConstruct
    public void start() {
        try {
            MqttClient client = new MqttClient(
                    "tcp://localhost:1883",
                    MqttClient.generateClientId(),
                    new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("broker");
            options.setPassword("broker".toCharArray());
            client.connect(options);

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                public void messageArrived(String topic, MqttMessage message) {
                    if ("sensor/data".equals(topic)) {
                        String payload = new String(message.getPayload());
                        SensorsData sensorData = parseSensorData(payload);
                    }
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete: " + token.isComplete());
                }
            });

            client.subscribe("sensor/data");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private SensorsData parseSensorData(String payload) {
        JSONObject json = new JSONObject(payload);
        SensorsData sensorData = new SensorsData();
        sensorData.fromJson((JsonObject) json);
        return sensorData;
    }
}
