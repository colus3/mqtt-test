package com.example.mqtttest.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class IotMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(IotMessageHandler.class);

    @Override
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        Integer qos = (Integer) message.getHeaders().get(MqttHeaders.RECEIVED_QOS);
        Object payload = message.getPayload();

        log.info("[MQTT] topic={} qos={} payload={}", topic, qos, payload);
    }
}
