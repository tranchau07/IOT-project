package com.example.Iot_Project.configuration;

import com.example.Iot_Project.enums.MqttTopicType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MqttConfig {

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
        options.setServerURIs(new String[]{SERVER_URI});
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        // Production configuration
        options.setCleanSession(false);          // Giữ session khi reconnect
        options.setAutomaticReconnect(true);     // Tự reconnect
        options.setConnectionTimeout(10);        // Timeout connect
        options.setKeepAliveInterval(30);        // Ping mỗi 30s
        options.setMaxInflight(200);             // Tăng số message song song

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public ThreadPoolTaskExecutor mqttTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mqtt-worker-");
        executor.initialize();
        return executor;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new ExecutorChannel(mqttTaskExecutor());
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new ExecutorChannel(mqttTaskExecutor());
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        CLIENT_ID + "_inbound",
                        mqttPahoClientFactory()
                );

        adapter.addTopic(
                MqttTopicType.RESPONSE.getTopic(),
                MqttTopicType.STATE.getTopic(),
                MqttTopicType.SENSOR_READING.getTopic()
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

        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(
                        CLIENT_ID + "_outbound",
                        mqttPahoClientFactory()
                );

        handler.setAsync(true);
        handler.setDefaultQos(1);
        handler.setDefaultRetained(false);

        return handler;
    }
}