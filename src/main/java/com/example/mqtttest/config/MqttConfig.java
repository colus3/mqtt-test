package com.example.mqtttest.config;

import com.example.mqtttest.handler.IotMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttConfig {

    private final MqttProperties props;

    public MqttConfig(MqttProperties props) {
        this.props = props;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{props.brokerUrl()});
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        if (props.username() != null && !props.username().isBlank()) {
            options.setUserName(props.username());
            options.setPassword(props.password().toCharArray());
        }

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(MqttPahoClientFactory factory) {
        String[] topicArray = props.topics().toArray(new String[0]);
        int[] qosArray = new int[topicArray.length];
        java.util.Arrays.fill(qosArray, props.qos());

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(props.clientId(), factory, topicArray);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qosArray);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public IotMessageHandler iotMessageHandler() {
        return new IotMessageHandler();
    }
}
