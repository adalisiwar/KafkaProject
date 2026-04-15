package com.ecommerce.kafka.repository;

import com.ecommerce.kafka.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderIdOrderBySentAtAsc(Long orderId);
}
