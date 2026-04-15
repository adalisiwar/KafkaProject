package com.ecommerce.kafka.service;

import com.ecommerce.kafka.dto.NotificationResponse;
import com.ecommerce.kafka.model.Notification;
import com.ecommerce.kafka.model.NotificationType;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification save(Order order, NotificationType type, boolean success) {
        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setType(type);
        notification.setSentAt(LocalDateTime.now());
        notification.setSuccess(success);
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> findByOrderId(Long orderId) {
        return notificationRepository.findByOrderIdOrderBySentAtAsc(orderId).stream().map(this::toDto).toList();
    }

    public NotificationResponse toDto(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setOrderId(notification.getOrder().getId());
        response.setType(notification.getType());
        response.setSentAt(notification.getSentAt());
        response.setSuccess(notification.isSuccess());
        return response;
    }
}
