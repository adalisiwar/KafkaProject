package com.ecommerce.kafka.service;

import com.ecommerce.kafka.dto.OrderRequest;
import com.ecommerce.kafka.dto.OrderResponse;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.model.OrderItem;
import com.ecommerce.kafka.model.OrderStatus;
import com.ecommerce.kafka.model.Product;
import com.ecommerce.kafka.producer.OrderProducerService;
import com.ecommerce.kafka.producer.PaymentProducerService;
import com.ecommerce.kafka.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final EventMapper eventMapper;
    private final OrderProducerService orderProducerService;
    private final PaymentProducerService paymentProducerService;
    private final NotificationService notificationService;
    private final Random random = new Random();

    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        ProductService productService,
                        EventMapper eventMapper,
                        OrderProducerService orderProducerService,
                        PaymentProducerService paymentProducerService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.productService = productService;
        this.eventMapper = eventMapper;
        this.orderProducerService = orderProducerService;
        this.paymentProducerService = paymentProducerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomer(customerService.findEntity(request.getCustomerId()));
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequest.OrderRequestItem requestItem : request.getItems()) {
            Product product = productService.findEntity(requestItem.getProductId());
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(requestItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.addItem(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity())));
        }
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        orderProducerService.publish(eventMapper.toCreatedEvent(savedOrder));
        return toResponse(loadDetailed(savedOrder.getId()));
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse findById(Long id) {
        return toResponse(loadDetailed(id));
    }

    public Order loadDetailed(Long id) {
        return orderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    @Transactional
    public String pay(Long id) {
        Order order = loadDetailed(id);
        if (order.getStatus() != OrderStatus.VALIDATED) {
            throw new IllegalStateException("Only VALIDATED orders can be paid");
        }
        if (random.nextInt(100) >= 90) {
            return "Payment failed during simulation. Try again.";
        }
        String paymentReference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        paymentProducerService.publish(eventMapper.toPaidEvent(order, paymentReference));
        return "Payment accepted with reference " + paymentReference;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = loadDetailed(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomer(customerService.toDto(order.getCustomer()));
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setItems(order.getItems().stream().map(item -> {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return itemResponse;
        }).toList());

        List<OrderResponse.TimelineEntry> timeline = new ArrayList<>();
        timeline.add(new OrderResponse.TimelineEntry("Order placed", "Order entered the pipeline", order.getCreatedAt()));
        notificationService.findByOrderId(order.getId()).forEach(notification -> timeline.add(
                new OrderResponse.TimelineEntry(notification.getType().name(), "Email notification sent", notification.getSentAt())
        ));
        timeline.sort(Comparator.comparing(OrderResponse.TimelineEntry::getAt));
        response.setTimeline(timeline);
        return response;
    }
}
