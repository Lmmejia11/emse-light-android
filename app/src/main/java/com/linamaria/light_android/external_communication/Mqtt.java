package com.linamaria.light_android.external_communication;

import com.linamaria.light_android.ContextManagementActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Mqtt implements MqttCallback {

    private final ContextManagementActivity cm;

    static String user = "ndtzubom";
    static String pass = "yGCB-nJ4kcOC";
    static String server = "m23.cloudmqtt.com";
    static String port = "14984";
    static String[] subscribe = {"#"};
    private final int qos = 1;
    private MqttClient client;
    private MqttConnectOptions conOpt;

    public Mqtt(ContextManagementActivity cm) throws MqttException {

        this.cm = cm;

        // Parameters
        System.out.println("Loading parameters");
        String host = String.format("tcp://%s:%s", server, port);
        String clientId = "DB-client";
        conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(user);
        conOpt.setPassword(pass.toCharArray());

        // Create client
        System.out.println("Creating client");
        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(this);

    }

    public void connect() throws MqttException {

        // Connect
        this.client.connect(conOpt);

        // Subscribe
        System.out.println("Subscribing");
        for (String s: subscribe){
            System.out.println("before s" );
            System.out.println("s = " + s + " " + qos + " " + client);
            this.client.subscribe(s, qos);
            System.out.println("after s");
        }
        System.out.println("Subscribed");
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

    public void sendMessage(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        this.client.publish(topic, message); // Blocking publish
        System.out.println("Sending message "+topic+": "+message);
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Message sent");
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("Message arrived [%s] %s", topic, new String(message.getPayload())));
    }




}
