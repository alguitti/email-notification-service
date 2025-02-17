package br.com.andre.email_notification_service.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "PRODUCT_EVENT")
@Builder
public class ProductEventEntity implements Serializable {

	private static final long serialVersionUID = -5370183428757728566L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "MESSAGE_ID", nullable = false, unique = true)
	private String messageId;
	
	@Column(name = "PRODUCT_ID", nullable = false)
	private String productId;
	
}
