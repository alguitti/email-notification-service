package br.com.andre.email_notification_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

	private String server;
	private String topic;
	private String groupId;
	private String trustedPackages;
	
}
