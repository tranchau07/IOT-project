package com.example.Iot_Project.configuration;

import com.example.Iot_Project.enums.MqttTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class MqttConfiguration {

    @NonFinal
    @Value("${mqtt.server-uri}")
    String SERVER_URI;

    @NonFinal
    @Value("${mqtt.username}")
    String USERNAME;

    @NonFinal
    @Value("${mqtt.password}")
    String PASSWORD;

    @NonFinal
    @Value("${mqtt.client-id}")
    String CLIENT_ID;

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{SERVER_URI}); //8080
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true); // Tự động kết nối lại khi mất kết nối
        options.setConnectionTimeout(10); // Timeout 10 giây
        options.setKeepAliveInterval(60);

        factory.setConnectionOptions(options);

        return factory;
    }
    //inbound / outbound

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                CLIENT_ID + "_inbound",
                mqttPahoClientFactory()
        );
        adapter.addTopic(
                MqttTopics.DEVICE_CONTROL_RESPONSE.getTopic(),
                MqttTopics.DEVICE_SENSOR_DATA.getTopic(),
                MqttTopics.DEVICE_STATUS.getTopic(),
                MqttTopics.DEVICE_HEARTBEAT.getTopic()
        );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
                CLIENT_ID + "_outbound",
                mqttPahoClientFactory()
        );

        handler.setAsync(true);
        handler.setDefaultQos(1);
        handler.setDefaultRetained(false);

        return handler;
    }


}
