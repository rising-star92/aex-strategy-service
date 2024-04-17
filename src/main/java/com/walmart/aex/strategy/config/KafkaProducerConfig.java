package com.walmart.aex.strategy.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.walmart.aex.strategy.properties.KafkaProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Profile("!test")
@Slf4j
public class KafkaProducerConfig {

    @ManagedConfiguration
    private KafkaProperties kafkaProperties;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    private static final String LOCAL = "local";

    @ConditionalOnMissingBean(type = "Properties")
    @Bean
    public Properties properties() {
        Properties props = new Properties();
        props.putAll(producerConfigs());
        return props;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getStrategyServiceKafkaServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        if (!activeProfile.contains(LOCAL)) {
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.location", getTrustStoreFileLocation());
            props.put("ssl.truststore.password", getTrustStoreFilePassword());
            props.put("ssl.keystore.location", getKeyStoreFileLocation());
            props.put("ssl.keystore.password", getTrustStoreFilePassword());
            props.put("ssl.key.password", getTrustStoreFilePassword());
            props.put("ssl.endpoint.identification.algorithm", "");

        }

        return props;
    }

    private String getTrustStoreFileLocation() {
        String truststoreFileName = "/tmp/kafkatruststore.jks";

        File file = new File(truststoreFileName);
        log.info("Truststore File Location: {}", truststoreFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String truststore = new String(Files.readAllBytes(Paths.get("/etc/secrets/ssl.truststore.txt")));
            fos.write(Base64.getDecoder().decode(truststore));
        } catch (Exception e) {
            log.error("error writing file: {} | {} | {}", e.getClass().getCanonicalName(), e.getMessage(),
                    e.getCause());
            return "";
        }
        return truststoreFileName;
    }

    private String getKeyStoreFileLocation() {
        String keystoreFileName = "/tmp/kafkakeystore.jks";

        File file = new File(keystoreFileName);
        log.info("Truststore File Location: {}", keystoreFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String keystore = new String(Files.readAllBytes(Paths.get("/etc/secrets/ssl.keystore.txt")));
            fos.write(Base64.getDecoder().decode(keystore));
        } catch (Exception e) {
            log.error("error writing file: {} | {} | {}", e.getClass().getCanonicalName(), e.getMessage(),
                    e.getCause());
            return "";
        }
        return keystoreFileName;
    }

    private Object getTrustStoreFilePassword() {
        String trustStorePassword = null;
        try {
            trustStorePassword = new String(Files.readAllBytes(Paths.get("/etc/secrets/ssl.truststore.password.txt")));
        } catch (IOException e) {
            log.error("Error reading truststore password" + e.getMessage());
        }
        return trustStorePassword;
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<Object, Object>(producerFactory());
    }
}
