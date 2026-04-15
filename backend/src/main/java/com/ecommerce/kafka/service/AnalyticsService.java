package com.ecommerce.kafka.service;

import com.ecommerce.kafka.dto.AnalyticsSummary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AnalyticsService {

    private final Map<LocalDate, BigDecimal> dailyRevenue = new ConcurrentHashMap<>();
    private final Map<LocalDate, AtomicLong> dailyPaidOrders = new ConcurrentHashMap<>();

    public void recordPaidOrder(LocalDate date, BigDecimal amount) {
        dailyRevenue.merge(date, amount, BigDecimal::add);
        dailyPaidOrders.computeIfAbsent(date, ignored -> new AtomicLong()).incrementAndGet();
    }

    public AnalyticsSummary getTodaySummary() {
        LocalDate today = LocalDate.now();
        return new AnalyticsSummary(
                today,
                dailyRevenue.getOrDefault(today, BigDecimal.ZERO),
                dailyPaidOrders.getOrDefault(today, new AtomicLong()).get()
        );
    }
}
