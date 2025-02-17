package br.com.andre.email_notification_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.andre.email_notification_service.model.ProductEventEntity;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductEventRepository extends JpaRepository<ProductEventEntity, Long> {

	Optional<ProductEventEntity> findByMessageId(String messageId);
	
}
