package com.lifeguardian.lifeguardian.utils;

import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.models.SensorsData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.annotation.Resource;
import jakarta.enterprise.inject.spi.CDI;

@Singleton
@Startup
public class MqttConnection {
    private static final Config config = ConfigProvider.getConfig();
    private final String uri = config.getValue("mqtt.uri", String.class);
//    private final String username = config.getValue("mqtt.username", String.class);
//    private final String password = config.getValue("mqtt.password", String.class);
    @Resource
    ManagedExecutorService mes;

    public void sendMessage(MqttClient client, String msg, String topic) throws MqttException {
        MqttMessage message = new MqttMessage(msg.getBytes());
        client.publish(topic, message);
    }


    @PostConstruct
    public void start() {
        try {
            MqttClient client = new MqttClient(
                    uri,
                    MqttClient.generateClientId(),
                    new MemoryPersistence());

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//            mqttConnectOptions.setUserName(username);
//            mqttConnectOptions.setPassword(password.toCharArray());
            mqttConnectOptions.setKeepAliveInterval(15);
            mqttConnectOptions.setConnectionTimeout(30);
            mqttConnectOptions.setAutomaticReconnect(true);
            client.connect(mqttConnectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("\n --------------------------------------------------- \n");
                    System.out.println("CLIENT LOST CONNECTION " + cause);
                    System.out.println("\n --------------------------------------------------- \n");
                    try {
                        client.reconnect();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    mes.execute(() ->  {
                        System.out.println("Message arrived!");
                        System.out.println("Topic: " + topic);
                        System.out.println("Message: " + new String(message.getPayload()));

                        // Obtain UserRepository within the managed task
                        UserRepository userRepository = CDI.current().select(UserRepository.class).get();

                        // Continue with your specific topic processing
                        if ("sensor/data".equals(topic)) {
                            // Process the message for the "sensor/data" topic
                            try (JsonReader jsonReader = Json.createReader(new StringReader(new String(message.getPayload())))) {
                                JsonObject json = jsonReader.readObject();
                                String username = json.getString("username");
                                JsonObject sensorDataJson = json.getJsonObject("sensorData");
                                User user = userRepository.findByUsername(username)
                                        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

                                SensorsData sensorData = new SensorsData();
                                sensorData.setSaturationData(sensorDataJson.getInt("saturatiOnData"));
                                sensorData.setTemp(sensorDataJson.getInt("temp"));
                                sensorData.setApHi(sensorDataJson.getInt("apHi"));
                                sensorData.setHeartRateData(sensorDataJson.getInt("heartRateData"));
                                sensorData.setApLo(sensorDataJson.getInt("apLo"));
                                user.setSensorsData(sensorData);
                                userRepository.save(user);
                            } catch (Exception e) {
                                System.out.println("Error processing message: " + e.getMessage());
                            }
                        }
                    });
                }



                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Delivery complete handling here
                }
            });

            client.subscribe("sensor/data", 1);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
