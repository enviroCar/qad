package org.envirocar.qad.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.envirocar.qad.model.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.enabled", matchIfMissing = true)
public class KafkaConfiguration {

    private final ObjectMapper objectMapper;
    private final KafkaParameters kafkaParameters;

    @Autowired
    public KafkaConfiguration(ObjectMapper objectMapper, KafkaParameters kafkaParameters) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.kafkaParameters = Objects.requireNonNull(kafkaParameters);
    }

    @Bean
    public ConsumerFactory<String, FeatureCollection> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaParameters.getBootstrap().getServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaParameters.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, this.kafkaParameters.getClientId());
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 157286400); // 150MB
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 157286400); // 150MB
        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer(), valueDeserializer());
    }

    @Bean
    public StringDeserializer keyDeserializer() {
        return new StringDeserializer();
    }

    @Bean
    public KafkaJacksonDeserializer<FeatureCollection> valueDeserializer() {
        return new KafkaJacksonDeserializer<>(FeatureCollection.class, this.objectMapper);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FeatureCollection> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FeatureCollection> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
